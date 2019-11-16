package com.hahaha.face_v2.UserAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.hahaha.face_v2.MainActivity;
import com.hahaha.face_v2.R;


import org.json.JSONException;

public class UserAdapter extends BaseAdapter {

    private JSONArray users;
    private Context context;






    //通过LayoutInflater来实例化子布局


    public UserAdapter(Context context,JSONArray users){
        this.context =context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public String getItem(int position) {

        return users.getString(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater= LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.user,parent,false);

            holder.textView=convertView.findViewById(R.id.nameTv);
            holder.delBtn =convertView.findViewById(R.id.delete_btn);
            convertView.setTag(holder);

        }else{
            holder=(ViewHolder) convertView.getTag();
        }

            holder.textView.setText(users.getString(position));
            holder.delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteListener.onDeleteClick(position);
                }
            });
        return convertView;
    }
    public interface onItemDeleteListener{
        void onDeleteClick(int i );

    }
    private onItemDeleteListener  DeleteListener;
    public void setDeleteListener(onItemDeleteListener DeleteListener){
        this.DeleteListener = DeleteListener;
    }

    private static class ViewHolder {
        private TextView textView;
        private Button delBtn;
    }

}