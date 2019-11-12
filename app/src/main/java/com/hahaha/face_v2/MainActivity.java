package com.hahaha.face_v2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.hahaha.face_v2.postbodys.FaceSearchResBody;
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
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import entity.Face;
import entity.Location;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView resultText;
    static volatile boolean threadFlag = false;

    private String img64;
    private String originImg64;
    private Bitmap bitmap;
    private Bitmap originBitmap;
    private byte[] fileBuf;
    private Uri imageUri;
    private String uploadFileName;
    private final String aliyunURL = "http://121.199.23.49:8000";
    private String user_id;
    private float score;
    private String user_info;
    private final String localURL = "http://192.168.43.156:8000";
    private final String URL416 = "http://192.168.1.100:8000";

    private final int REQUEST_CODE_CAMERA = 2;
    private final int INTENT_REQUEST_IMAGE_CODE = 1;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE_CODE = 1;

    @SuppressLint({"ResourceType", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = new MyImageView(this);
        //imageView.setImageResource(R.id.selectImage);
        imageView = findViewById(R.id.selectImage);
        //resultText = findViewById(R.id.result);

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
                        String path = imageUri.getPath();
                        int degree = readPictureDegree(path);

                        fileBuf = convertToBytes(inputStream_byte);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap = BitmapFactory.decodeStream(inputStream_map);

                        Bitmap rotateBitmap = rotaingImageView(degree, bitmap);

                        imageView.setImageBitmap(rotateBitmap);
                        //bitmap.compress(Bitmap.CompressFormat.JPEG, 4, out);
                        byte[] compressBytes = out.toByteArray();
                        fileBuf = compressBytes;
                        //Bitmap compressBitmap = BitmapFactory.decodeByteArray(compressBytes, 0, compressBytes.length);

                        img64 = Base64.encodeToString(compressBytes, Base64.DEFAULT);
                        //imageView.setImageBitmap(bitmap);
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
        //这是Android 7后，更加安全的获取文件uri的方式（需要配合Provider,在Manifest.xml中加以配置）
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "photoFileprovider", outImg);

        } else {
            imageUri = Uri.fromFile(outImg);
        }
        //利用actionName和Extra,启动《相机Activity》
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, REQUEST_CODE_CAMERA);



    }

    public void face(View view) {
        if (fileBuf == null) {
            Toast.makeText(this, "请选择照片或拍照", Toast.LENGTH_LONG).show();
            return;
        }
        final String[] result = {null, null};

        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();


                    PostBody postBody = new PostBody(img64, uploadFileName);
                    String postdata = new Gson().toJson(postBody);


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", img64)
                            .build();

                    Request request = new Request.Builder()
                            .url(aliyunURL + "/search_face")
                            //.url(localURL + "/search_face")
                            //.url(URL416 + "/search_face")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    result[0] = response.body().string();
                    if (JSON.isValidArray(result[0])) {
                        result[1] = "success";
                    } else {
                        result[1] = "fail";
                    }
                    Log.i("子线程返回值", result[1]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();



        while (result[0] == null || result[1] == null){
            System.out.println("循环");
        }
        Log.i("测试", "跳出子线程");
        Log.i("返回值", result[0]);
        if (result[1].equals("success")) {
            //List list = JSON.parseArray(result[0]);
//            Intent intent = new Intent();
//            intent.setClass(this, InfoActivity.class);
//            intent.putExtra("faceList", result[0]);
            //intent.putExtra("sendImage", fileBuf);
//            startActivity(intent);
            List faceList = JSON.parseArray(result[0]);
            Iterator it = faceList.iterator();
            List<Face> faces = new ArrayList<>();
            while (it.hasNext()) {
                Face face = JSON.parseObject(it.next().toString(), Face.class);
                faces.add(face);
            }
            this.drawRectOnBitmap(imageView, originBitmap, faces);

        } else {
            Log.i("测试", result[0]);
            Toast.makeText(this, "识别失败", Toast.LENGTH_LONG).show();
        }


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
        String path = uri.getPath();
        //int degree = readPictureDegree(path);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName = cursor.getString(columnIndex);
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            fileBuf = convertToBytes(inputStream);
            originImg64 = Base64.encodeToString(fileBuf, Base64.DEFAULT);
            originBitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
            //originBitmap = rotaingImageView(-90, originBitmap);
            imageView.setImageBitmap(originBitmap);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
            //bitmap = rotaingImageView(-90, bitmap);
            //imageView.setImageBitmap(bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 4, out);
            byte[] compressBytes = out.toByteArray();

            Bitmap compressBitmap = BitmapFactory.decodeByteArray(compressBytes, 0, compressBytes.length);

            img64 = Base64.encodeToString(compressBytes, Base64.DEFAULT);
            //imageView.setImageBitmap(bitmap);


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
        boolean flag = false;
        new Thread() {
            public void run() {
                try {
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
                    user_info= (String) userinfo.get("user_info");
                    user_id = String.valueOf(userinfo.get("user_id"));
                    score = Float.parseFloat(userinfo.get("score").toString());
                    if (score < 80) {
                        //不存在该用户，新建用户组
                        user_info=null;
                        System.out.println("创建新的用户组");
                        new Thread(){
                            @Override
                            public void run() {
                                Looper.prepare();
                                alert_edit();
                                Looper.loop();
                            }
                        }.start();
                        while(user_info==null){

                        }
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
                    threadFlag = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        while (threadFlag == false) {}
        Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
        threadFlag = false;
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
                }).setNegativeButton("取消",null).show();




    }

    private void drawRectOnBitmap(ImageView iv, Bitmap bitmap, List<Face> faceList) {
        Bitmap drawBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(drawBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);

        final float scale = drawBitmap.getDensity();

        Paint text = new Paint();
        text.setColor(Color.YELLOW);
        text.setTextSize(180);



        //canvas.drawRect(location.getLeft().floatValue(), location.getTop().floatValue(), location.getWidth().floatValue(), location.getHeight().floatValue(), paint);
        Iterator it = faceList.iterator();
        while (it.hasNext()) {
            Face face = (Face) it.next();
            Location location = face.getLocation();
            //canvas.rotate(location.getRotation().floatValue());
            canvas.drawRect(location.getLeft().floatValue() , location.getTop().floatValue() , location.getLeft().floatValue() + location.getWidth().floatValue(), location.getTop().floatValue() + location.getHeight().floatValue(), paint);
            if (!face.getUser_list().isEmpty()) {
                canvas.drawText(face.getUser_list().get(0).getUser_info(), location.getLeft().floatValue(), location.getTop().floatValue(), text);
            }
        }
        iv.setImageBitmap(drawBitmap);

    }


    //获取图片的旋转角度
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}


