package com.hahaha.face_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hahaha.face_v2.adapter.FaceInfoAdapter;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entity.Face;


public class InfoActivity extends AppCompatActivity {

    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        rv = findViewById(R.id.face_list);

        Intent intent = getIntent();


        String response = intent.getStringExtra("faceList");
        Log.i("INFO测试", response);
        List faceList = JSON.parseArray(response);
        Iterator it = faceList.iterator();
        List<Face> faces = new ArrayList<Face>();
        while (it.hasNext()) {
            Face face = JSON.parseObject(it.next().toString(), Face.class);
            faces.add(face);
            //System.out.println(face.getUser_list().get(0).getUser_info());
        }
        FaceInfoAdapter adapter = new FaceInfoAdapter(faces);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(linearLayoutManager);

    }

}
