package com.hahaha.face_v2.util;

import android.widget.ListAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hahaha.face_v2.postbodys.ErrorBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FaceManagerUtil {
    private ListView listView;
    private ListAdapter listAdapter;
    private String access_token="";
    private JSONArray userList;
    private String result;
    private String uid;
    //删除用户
    public  String deleteUser(String user_id) {
        if (uid!=null) uid =null;
        uid = user_id;
               if (result!=null)result=null;
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
                   System.out.println("response_result"+delete_result);
                   JSONObject response_result = JSONObject.parseObject(delete_result);
                   result =response_result.getString("error_msg");
                   System.out.println(result);
               } catch (IOException e) {
                   e.printStackTrace();
               }
        return result;
    }
    //获取用户列表
    public JSONArray getUserList(){
        if ((userList!=null))userList.clear();
                String get_Url ="https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers?access_token=24.92fd062c410c85e7d563e758acccb0af.2592000.1574862037.282335-16234596";
                Map<String ,String> map = new HashMap<>();
                map.put("group_id","lxh1");
                String get_user = new Gson().toJson(map);
                MediaType mediaType = MediaType.get("application/json;charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                RequestBody getUser_body = RequestBody.create(mediaType, get_user);
                Request get_request = new Request.Builder()
                        .url(get_Url)
                        .post(getUser_body)
                        .build();
                Response getUser_response = null;
                try {
                    getUser_response = client.newCall(get_request).execute();
                    String get_result = getUser_response.body().string();
                    JSONObject response_result = JSON.parseObject(get_result);
                    JSONObject result = response_result.getJSONObject("result");
                    userList = result.getJSONArray("user_id_list");

                } catch (IOException e) {
                    e.printStackTrace();
                }

        return userList;
    }
}
