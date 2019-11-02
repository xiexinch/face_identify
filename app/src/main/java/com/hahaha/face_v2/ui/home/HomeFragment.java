package com.hahaha.face_v2.ui.home;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hahaha.face_v2.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button select;
    private Button photo;
    private Button face;
    private Button upload;
    private ImageView selectImage;
    private TextView result;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new HomeViewModel();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化组件
        select = (Button)root.findViewById(R.id.selectBtn);
        photo = (Button)root.findViewById(R.id.photoBtn);
        face = (Button)root.findViewById(R.id.faceBtn);
        upload = (Button)root.findViewById(R.id.uploadBtn);
        selectImage = (ImageView)root.findViewById(R.id.selectImage);
        result = (TextView)root.findViewById(R.id.result);



        return root;
    }



//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.selectBtn:
//                // TODO
//                break;
//            case R.id.photoBtn:
//                // TODO
//                break;
//            case R.id.faceBtn:
//                // TODO
//                break;
//            case R.id.uploadBtn:
//                // TODO
//                break;
//        }
//    }

    public void setImageView(Bitmap bitmap) {
        this.selectImage.setImageBitmap(bitmap);
    }

    public void setResultText(String resultText) {
        this.result.setText(resultText);
    }
}