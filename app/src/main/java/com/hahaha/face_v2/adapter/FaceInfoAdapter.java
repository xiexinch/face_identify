package com.hahaha.face_v2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.hahaha.face_v2.R;

import java.util.List;

import entity.Face;

public class FaceInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Face> list;

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tokenTv;
        TextView infoTv;

        public MyHolder(View itemView) {
            super(itemView);
            this.infoTv = itemView.findViewById(R.id.infoTv);
            this.tokenTv = itemView.findViewById(R.id.tokenTv);
        }
    }

    public FaceInfoAdapter(List data) {this.list = data;}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyHolder myHolder = null;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_componant, parent, false);
        myHolder = new MyHolder(itemView);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder holder1 = (MyHolder)holder;
        Face face = list.get(position);
        List userList = face.getUser_list();
        if (userList != null && !userList.isEmpty()) {
            holder1.infoTv.setText(face.getUser_list().get(0).getUser_info());
        } else {
            holder1.infoTv.setText("未知人脸");
        }

        holder1.tokenTv.setText(face.getFace_token());
    }

    @Override
    public int getItemCount() {

        return list.size();
    }
}
