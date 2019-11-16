package com.hahaha.face_v2.Service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hahaha.face_v2.InfoActivity;
import com.hahaha.face_v2.MainActivity;
import com.hahaha.face_v2.UserAdapter.UserAdapter;
import com.hahaha.face_v2.postbodys.checkBody;

import java.io.ByteArrayOutputStream;
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

import static android.app.PendingIntent.getActivity;
import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.hahaha.face_v2.util.Util.convertToBytes;

public class MyService {
    private String result;

    private String resultmsg;
    private String ar_msg;
    static volatile boolean threadFlag = false;
    private String user_info;
    private String user_id;
    private String img;
    private final String aliyunURL = "http://121.199.23.49:8001";
    private final String localURL = "http://192.168.43.156:8000";
    private final String URL416 = "http://192.168.1.100:8000";
    private volatile boolean threadOver = false;

    private Context context;
    private InfoActivity activity;
    public MyService(Context context, InfoActivity activity1) {
        this.context = context;
        this.activity = activity1;
    }
    //删
    @JavascriptInterface
    public String deletUser(String uuid) {
        final String uid = uuid;
        new Thread() {
            public void run() {
                String deleteUrl = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete?access_token=24.92fd062c410c85e7d563e758acccb0af.2592000.1574862037.282335-16234596";
                Map<String, String> map = new HashMap<>();
                map.put("group_id", "lxh1");
                map.put("user_id", uid);
                String del = new Gson().toJson(map);
                MediaType mediaType = MediaType.get("application/json;charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                RequestBody delete_body = RequestBody.create(mediaType, del);
                Request delete_request = new Request.Builder()
                        .url(deleteUrl)
                        .post(delete_body)
                        .build();
                Response delete_response = null;
                try {
                    delete_response = client.newCall(delete_request).execute();
                    String delete_result = delete_response.body().string();
                    System.out.println("response_result" + delete_result);
                    JSONObject response_result = JSONObject.parseObject(delete_result);
                    result = response_result.getString("error_msg");
                    System.out.println(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return result;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_WRITE_EXTERNAL_STORAGE_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    openGallery();
//                } else {
//                    Toast.makeText(context, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//    }
    @JavascriptInterface
    public String addface() throws InterruptedException {
         img = InfoActivity.img64;
        activity.addFace_info();
        while(img == InfoActivity.img64){

        }
        img = InfoActivity.img64;
        Thread af = new Thread() {
            public void run() {
                try {
                    //调用人脸识别接口看是否存在该用户

                    String url_face = "https://aip.baidubce.com/rest/2.0/face/v3/search?access_token=24.92fd062c410c85e7d563e758acccb0af.2592000.1574862037.282335-16234596";
                    Map<String, String> checkMap = new HashMap<>();
                    checkMap.put("image", img);
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
                    System.out.println("check_result"+check_result);
                    checkBody cb = JSONArray.parseObject(check_result, checkBody.class);
                    if (cb.getResult() == null) {
                        //resultText.setText(cb.getError_msg());
                        //Toast.makeText(mContext, cb.getError_msg(), Toast.LENGTH_LONG).show();
                        threadFlag = true;
                        resultmsg = cb.getError_msg();
                    }

                    //解析返回的Json
                    checkBody checkBody = JSONArray.parseObject(check_result, checkBody.class);
                    JSONObject result = checkBody.getResult();
                    JSONArray user_list = result.getJSONArray("user_list");
                    JSONObject userinfo = user_list.getJSONObject(0);
                    user_info = (String) userinfo.get("user_info");
                    user_id = String.valueOf(userinfo.get("user_id"));
                    float score = Float.parseFloat(userinfo.get("score").toString());
                    if(score<80){
                        resultmsg="请上传已有用户的照片";
                    }else{
                        String url_add = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.92fd062c410c85e7d563e758acccb0af.2592000.1574862037.282335-16234596";
                        Map<String, String> addMap = new HashMap<>();
                        addMap.put("group_id", "lxh1");
                        addMap.put("user_id", user_id);
                        addMap.put("image", img);
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
                        JSONObject ar = JSON.parseObject(add_result);
                        ar_msg = ar.getString("error_msg");
                        threadOver = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        af.start();
        af.join();
        //上传到服务器
        while (threadOver == false) {}
        threadOver = false;
        if(ar_msg.equals("SUCCESS")){
            Thread uploadToServer =new Thread(){
                public void run(){
                    //解析add_resut，拿到user_id,user_info,img64
                    byte[] filebuf = InfoActivity.fileBuf;
                    RequestBody formBody = RequestBody
                            .create(MediaType.parse("image/jpg"), filebuf);
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("userid", user_id)
                            .addFormDataPart("userinfo", user_info)
                            .addFormDataPart("user_face", user_id + System.currentTimeMillis() + ".jpg", formBody)
                            .build();
                    Request request = new Request.Builder()
                            .url(aliyunURL + "/face/add_face")
                            .post(requestBody)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            uploadToServer.start();
            uploadToServer.join();
        }else return resultmsg;
        return  ar_msg;


    }
}
