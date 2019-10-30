package com.hahaha.face_v2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.hahaha.face_v2.postbodys.PostBody;
import com.hahaha.face_v2.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView result;
    private HomeFragment homeFragment;

    private byte[] fileBuf;
    private String uploadFileName;
    private final String aliyunURL = "http://121.199.23.49:8000";


    private final int INTENT_REQUEST_IMAGE_CODE = 1;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE_CODE = 1;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FragmentManager fm = getSupportFragmentManager();
        homeFragment = (HomeFragment) fm.findFragmentById(R.layout.fragment_home);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            fm.beginTransaction()
                    .add(R.id.nav_host_fragment, homeFragment)
                    .commit();
        }

    }

    public void select(View view) {
        String[] permissions = new String[] {
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
        }
    }

    public void photo(View view) {

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

                    String img = new String(Base64.getEncoder().encode(fileBuf));


                    PostBody postBody = new PostBody(img, uploadFileName);
                    String postdata = new Gson().toJson(postBody);


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", img)
                            .build();

                    Request request = new Request.Builder()
                            .url(aliyunURL + "/search_face")
                            .post(requestBody)
                            .build();


                    Response response = client.newCall(request).execute();
                    Log.i("返回值", response.body().string());

                    homeFragment.setResultText(response.body().toString());

                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public void upload(View view) {

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
            homeFragment.setImageView(bitmap);


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


}
