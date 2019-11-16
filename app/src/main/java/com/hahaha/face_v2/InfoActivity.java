package com.hahaha.face_v2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qmuiteam.qmui.widget.webview.QMUIWebView;


public class InfoActivity extends AppCompatActivity {

    private WebView webView;

    private final String aliyunURL = "http://121.199.23.49:8001";


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        //webView.loadUrl("http://192.168.43.156:8000/face/");
        //webView.loadUrl("http://192.168.1.103:8001/face/");
        webView.loadUrl(aliyunURL + "/face/");
    }

    public void toHome(View view) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }

    public void facesRefresh(View view) {
        webView.reload();
    }



}
