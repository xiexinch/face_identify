package com.hahaha.face_v2;


import androidx.annotation.RequiresApi;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.os.Build;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qmuiteam.qmui.widget.webview.QMUIWebView;

import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hahaha.face_v2.Service.MyService;
import com.hahaha.face_v2.UserAdapter.UserAdapter;
import com.hahaha.face_v2.postbodys.checkBody;
import com.hahaha.face_v2.util.FaceManagerUtil;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.hahaha.face_v2.util.Util.convertToBytes;
import static kotlin.text.Typography.degree;



public class InfoActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_CODE = 1;
    private static final int INTENT_REQUEST_IMAGE_CODE = 1;
    private ListView listView;
    private JSONArray users;
    private UserAdapter adapter;
    private String Uname;
    private String result;
    private String uploadFileName;
    public static byte[] fileBuf;
    private String originImg64;
    private Bitmap originBitmap;
    private Bitmap bitmap;
    public static
    String img64;
    private String resultmsg;
    public static String ar_msg;
    static volatile boolean threadFlag = false;
    private String user_info;
    private String user_id;
    private final String aliyunURL = "http://121.199.23.49:8001";
    private final String localURL = "http://192.168.43.156:8000";
    private final String URL416 = "http://192.168.1.100:8000";


    private WebView webView;


    private Context context;



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        webView =findViewById(R.id.webview);


        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        webView.addJavascriptInterface(new MyService(this,InfoActivity.this),"myService");


        //webView.loadUrl("http://192.168.43.156:8000/face/");
        //webView.loadUrl("http://192.168.1.103:8001/face/");
        webView.loadUrl(aliyunURL + "/face/");
    }

    public void toHome(View view) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }
    public String addFace_info() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_WRITE_EXTERNAL_STORAGE_CODE);
        else {
            openGallery();
        }
        return "done";
    }
    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, INTENT_REQUEST_IMAGE_CODE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_REQUEST_IMAGE_CODE:
                handleSelect(data);
                break;
                }
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

            originImg64 = Base64.encodeToString(fileBuf, Base64.DEFAULT);
            originBitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
            bitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);

//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
//            byte[] compressBytes = out.toByteArray();
//            img64 = Base64.encodeToString(compressBytes, Base64.DEFAULT);
            img64 = compressBitmap(bitmap,2048000,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
    }
//    public String addface() throws InterruptedException {
//
//
//    }

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


    /*
        public void getUserList(View view) throws InterruptedException {
            if (users!=null)users.clear();
            final Thread getuser = new Thread(){
                public void run(){

                    FaceManagerUtil faceManagerUtil = new FaceManagerUtil();
                    users = faceManagerUtil.getUserList();
                }
            };
            getuser.start();
            getuser.join();
            adapter = new UserAdapter(this,users);

            //获取点击Item信息

            adapter.setDeleteListener(new UserAdapter.onItemDeleteListener() {
                FaceManagerUtil faceManagerUtil = new FaceManagerUtil();

                TextView textView=null;
                String deResult=null;
                @Override
                public void onDeleteClick(int i) {

                    View view = findView(i,listView);       //  根据 position的下标 与 listView找到他的 View
                    textView = view.findViewById(R.id.nameTv);
                    Uname = textView.getText().toString();
                    //删除user
                    Thread delUser = new Thread(){
                        public  void run(){
                            deResult=faceManagerUtil.deleteUser(Uname);
                        }
                    };
                    delUser.start();
                    try {
                        delUser.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(deResult);
                    users.clear();
                    Thread  getnewUser = new Thread(){
                        public void run(){
                            users=faceManagerUtil.getUserList();
                        }
                    } ;
                    getnewUser.start();
                    try {
                        getnewUser.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        getuser.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        getUserList(view);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            listView.setAdapter(adapter);
        }



        private View findView(int position, ListView listView) {
            int firstListItemPosition = listView.getFirstVisiblePosition();
            int lastListItemPosition = firstListItemPosition
                    + listView.getChildCount() - 1;

            if (position < firstListItemPosition || position > lastListItemPosition) {
                return listView.getAdapter().getView(position, null, listView);
            } else {
                final int childIndex = position - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }
    */
    //增
//    @JavascriptInterface
//    public void addFace_info() {
//        View view = new View(this);
//        MainActivity activity = new MainActivity();
//        activity.select(view);
//        activity.addFace(view);
//        System.out.println("hi");
//    }




    public void facesRefresh(View view) {
        webView.reload();
    }



}
