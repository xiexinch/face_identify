package com.hahaha.face_v2;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonParser;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import entity.Face;
import entity.User;

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
    public void test2() {
        String response = "[{\"face_token\":\"526c4b82099a3713c232332664b9b2d9\",\"location\":{\"left\":628.48,\"top\":309.34,\"width\":91,\"height\":83,\"rotation\":-5},\"user_list\":[{\"group_id\":\"lxh1\",\"user_id\":\"f85c4f19b0bc44e79ca9606329d8a341\",\"user_info\":\"谢昕辰\",\"score\":96.241302490234}]},{\"face_token\":\"d584f543574ce7d41d7ac8cd0055a5c0\",\"location\":{\"left\":438.57,\"top\":323.56,\"width\":83,\"height\":87,\"rotation\":0},\"user_list\":[]}]";
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

}