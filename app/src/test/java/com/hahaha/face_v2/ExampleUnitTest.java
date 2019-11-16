package com.hahaha.face_v2;

import android.text.format.Time;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonParser;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import entity.Face;
import entity.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test1() {
        String s = UUID.randomUUID().toString();
        String[] arr = s.split("-");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            stringBuffer.append(arr[i]);
        }
        System.out.println(stringBuffer.toString());

    }
    @Test
    public void  test_11(){
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void test2() {
        String response = "[{\"face_token\":\"873550c5a57c93f84b5c71094d76182f\",\"location\":{\"left\":518.66,\"top\":516.13,\"width\":275,\"height\":275,\"rotation\":72},\"user_list\":[{\"group_id\":\"lxh1\",\"user_id\":\"f85c4f19b0bc44e79ca9606329d8a341\",\"user_info\":\"谢昕辰\",\"score\":91.280830383301}]},{\"face_token\":\"f8f0b04ab19583ee4498890ce86e2756\",\"location\":{\"left\":488.99,\"top\":304.14,\"width\":210,\"height\":234,\"rotation\":128},\"user_list\":[{\"group_id\":\"lxh1\",\"user_id\":\"4bfea364b5b0409ba88b80758f4ce100\",\"user_info\":\"李浩\",\"score\":95.890327453613}]}]";
        List list = JSON.parseArray(response);
        Iterator it  = list.iterator();
        while (it.hasNext()) {
            Face face = JSON.parseObject(it.next().toString(), Face.class);
            System.out.println(face.getFace_token());
            System.out.println(face.getLocation().getHeight());
            List<User> userList = face.getUser_list();
            if (!userList.isEmpty()) {
                Iterator userIt = userList.iterator();
                while ((userIt.hasNext())) {
                    System.out.println(((User)userIt.next()).getUser_info());
                }
            }
        }
    }

    @Test
    public void testConnect() {

        OkHttpClient client = new OkHttpClient();

        Request requestBody = new Request.Builder()
                .url("http://localhost:8000/face/hello")
                .get()
                .build();

        try {
            Response response = client.newCall(requestBody).execute();
            System.out.println(response.body().string());

        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testAddFace() {
        String localURL = "http://localhost:8000";
        byte[] fileBuf = null;

        try {
            InputStream imgInputStream = new FileInputStream("/Users/xxc/AndroidStudioProjects/face_v2/app/src/main/res/drawable/test2.jpg");
            fileBuf = convertToBytes(imgInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fileBuf == null) {
            System.out.println("fail");
            return;
        }

        String s = UUID.randomUUID().toString();
        String[] arr = s.split("-");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            stringBuffer.append(arr[i]);
        }
        String uuid = stringBuffer.toString();
        System.out.println(uuid);

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = RequestBody
                .create(MediaType.parse("image/jpg"), fileBuf);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userid", uuid)
                .addFormDataPart("userinfo", "test2_face")
                .addFormDataPart("user_face", "test2.jpg", formBody)
                .build();
        Request request = new Request.Builder()
                .url(localURL + "/face/add_face")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
























