package com.hahaha.face_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText userInfo;
    private String img64;
    String url_add = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.92fd062c410c85e7d563e758acccb0af.2592000.1574862037.282335-16234596";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        imageView = findViewById(R.id.fromMain);
        userInfo = findViewById(R.id.userInfo);

        Intent intent = getIntent();
        String img = intent.getStringExtra("image");
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
        img64 = new String(img);
    }

    public void resetInfo(View view) {
        this.userInfo.setText("");
    }

    public void submitFaceInfo(View view) {
        new Thread(){
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .addFormDataPart("user_info", userInfo.getText().toString())
                        .addFormDataPart("new_face_image", img64)
                        .build();

                Request request = new Request.Builder()
                        .url(url_add)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
