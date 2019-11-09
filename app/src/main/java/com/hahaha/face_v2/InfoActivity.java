package com.hahaha.face_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import entity.Face;
import entity.User;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        imageView = findViewById(R.id.selectImage);

        Intent intent = getIntent();


        String response = intent.getStringExtra("faceList");
        List faceList = JSON.parseArray(response);
        Iterator it  = faceList.iterator();
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
