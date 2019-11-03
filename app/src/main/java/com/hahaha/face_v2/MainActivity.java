package com.hahaha.face_v2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.hahaha.face_v2.postbodys.PostBody;
import com.hahaha.face_v2.postbodys.addBody;
import com.hahaha.face_v2.postbodys.checkBody;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView resultText;


    private String img64;
    private Bitmap bitmap;
    private byte[] fileBuf;
    private Uri imageUri;
    private String uploadFileName;
    private final String aliyunURL = "http://121.199.23.49:8000";
    private String user_id;
    private float score;
    private String user_info;


    private final int REQUEST_CODE_CAMERA = 2;
    private final int INTENT_REQUEST_IMAGE_CODE = 1;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE_CODE = 1;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.selectImage);
        resultText = findViewById(R.id.result);

    }

    public void select(View view) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_WRITE_EXTERNAL_STORAGE_CODE);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, INTENT_REQUEST_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_REQUEST_IMAGE_CODE:
                handleSelect(data);
                break;
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    try {
                        InputStream inputStream_map = getContentResolver().openInputStream(imageUri);
                        InputStream inputStream_byte = getContentResolver().openInputStream(imageUri);
                        fileBuf = convertToBytes(inputStream_byte);

                        bitmap = BitmapFactory.decodeStream(inputStream_map);
                        img64 = compressBitmap(bitmap, 1024000, false);
                        imageView.setImageBitmap(bitmap);
                        uploadFileName = System.currentTimeMillis() + ".jpg";
                        File outImg = new File(getExternalCacheDir(), "temp.jpg");
                        if (outImg.exists()) outImg.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public void photo(View view) throws IOException {
        //创建临时文件保存照片
        File outImg = new File(getExternalCacheDir(), "temp.jpg");
        if (outImg.exists()) outImg.delete();
        outImg.createNewFile();
        //
        if (Build.VERSION.SDK_INT >= 24)
        //这是Android 7后，更加安全的获取文件uri的方式（需要配合Provider,在Manifest.xml中加以配置）
        {
            imageUri = FileProvider.getUriForFile(this, "xjtu.lxh.camera.fileprovider", outImg);

        } else
            imageUri = Uri.fromFile(outImg);
        //利用actionName和Extra,启动《相机Activity》
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, REQUEST_CODE_CAMERA);

        //到此，启动了相机，等待用户拍照


    }

    public void face(View view) {
        if (fileBuf == null) {
            Toast.makeText(this, "请选择照片或拍照", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    //String img = new String(Base64.getEncoder().encode(fileBuf));


                    PostBody postBody = new PostBody(img64, uploadFileName);
                    String postdata = new Gson().toJson(postBody);


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", img64)
                            .build();

                    Request request = new Request.Builder()
                            .url(aliyunURL + "/search_face")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String resultInfo = response.body().string();
                    Log.i("返回值", resultInfo);
                    resultText.setText(resultInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void upload(View view) {
        if (fileBuf == null) {
            Toast.makeText(this, "请选择照片或拍照", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                OkHttpClient client1 = new OkHttpClient();
                //上传文件域的请求体部分
                RequestBody formBody = RequestBody
                        .create(MediaType.parse("JPGE"), fileBuf);
                //整个上传的请求体部分（普通表单+文件上传域）
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", "Square Logo")
                        //filename:avatar,originname:abc.jpg
                        .addFormDataPart("picture", uploadFileName, formBody)
                        .build();
                Request request = new Request.Builder()
                        .url(aliyunURL)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client1.newCall(request).execute();
                    Log.i("数据", response.body().string() + "....");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    private void handleSelect(Intent intent) {
        Cursor cursor = null;
        Uri uri = intent.getData();
        cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName = cursor.getString(columnIndex);
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            fileBuf = convertToBytes(inputStream);
            Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
            img64 = compressBitmap(bitmap, 1024000, false);
            imageView.setImageBitmap(bitmap);


        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return out.toByteArray();
    }

    //质量压缩
    private String compressBitmap(Bitmap bitmap, double maxSize, boolean needRecycle) {
        if (bitmap == null) {
            return null;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            //计算等比缩放
            double x = Math.sqrt(maxSize / (width * height));
            Bitmap tmp = Bitmap.createScaledBitmap(bitmap, (int) Math.floor(width * x), (int) Math.floor(height * x), true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;
            //生产byte[]
            tmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
            //判断byte[]与上线存储空间的大小
            if (baos.toByteArray().length > maxSize) {
                //根据内存大小的比例，进行质量的压缩
                options = (int) Math.ceil((maxSize / baos.toByteArray().length) * 100);
                baos.reset();
                tmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
                //循环压缩
                while (baos.toByteArray().length > maxSize) {
                    baos.reset();
                    options -= 1.5;
                    tmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
                }
                recycle(tmp);
                if (needRecycle) {
                    recycle(bitmap);
                }
            }
            byte[] data = baos.toByteArray();
            String image64 = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
            System.out.println(image64.length());
            try {
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image64;
        }
    }

    /**
     * 回收Bitmap
     *
     * @param thumbBmp 需要被回收的bitmap
     */
    public static void recycle(Bitmap thumbBmp) {
        if (thumbBmp != null && !thumbBmp.isRecycled()) {
            thumbBmp.recycle();
        }
    }

    public void addFace(View view) {
        if (fileBuf == null) {
            Toast.makeText(this, "请选择照片或拍照", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread() {
            public void run() {
                try {
                    Looper.prepare();
                    //调用人脸识别接口看是否存在该用户
                    String url_add = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.92fd062c410c85e7d563e758acccb0af.2592000.1574862037.282335-16234596";
                    String url_face = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=24.92fd062c410c85e7d563e758acccb0af.2592000.1574862037.282335-16234596";
                    Map<String, String> checkMap = new HashMap<>();
                    checkMap.put("image", img64);
                    checkMap.put("image_type", "BASE64");
                    checkMap.put("group_id_list", "lxh1");
                    String face = new Gson().toJson(checkMap);

                    MediaType mediaType = MediaType.get("application/json;charset=utf-8");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody check_body = RequestBody.create(mediaType, face);
                    Request check_request = new Request.Builder()
                            .url(url_face)
                            .post(check_body)
                            .build();
                    Response check_response = client.newCall(check_request).execute();
                    String check_result = check_response.body().string();
                    System.out.println(check_result);
                    checkBody cb = JSONArray.parseObject(check_result, checkBody.class);
                    //如果照片没有脸
                    if (cb.getResult() == null) {
                        resultText.setText(cb.getError_msg());
                        return;
                    }
                    //解析返回的Json
                    checkBody checkBody = JSONArray.parseObject(check_result, checkBody.class);
                    JSONObject result = checkBody.getResult();
                    JSONArray user_list = result.getJSONArray("user_list");
                    JSONObject userinfo = user_list.getJSONObject(0);
                    user_id = String.valueOf(userinfo.get("user_id"));
                    score = Float.parseFloat(userinfo.get("score").toString());
                    if (score < 80) {
                        //不存在该用户，新建用户组
                        System.out.println("创建新的用户组");
                        alert_edit();
                        System.out.println(user_info);
                        String[] rand = String.valueOf(UUID.randomUUID()).split("-");

                        StringBuffer s = new StringBuffer();
                        for (String str : rand) {
                            s.append(str);
                        }
                        user_id = String.valueOf(s);
                    } else System.out.println("已存在该用户，直接存储");
                    Map<String, String> addMap = new HashMap<>();
                    addMap.put("group_id", "lxh1");
                    addMap.put("user_id", user_id);
                    addMap.put("image", img64);
                    addMap.put("image_type", "BASE64");
                    addMap.put("user_info", user_info);
                    String faceJson = new Gson().toJson(addMap);

                    RequestBody add_body = RequestBody.create(mediaType, faceJson);
                    Request add_request = new Request.Builder()
                            .url(url_add)
                            .post(add_body)
                            .build();
                    Response add_response = client.newCall(add_request).execute();
                    String add_result = add_response.body().string();
                    System.out.println(add_result);
                    addBody ab = JSONArray.parseObject(add_result, addBody.class);
                    resultText.setText(ab.getError_msg());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    public void alert_edit(){

        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("请输入用户信息")
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        user_info = et.getText().toString();
                    }
                }).setNegativeButton("取消",null);



    }
}


