package com.hahaha.face_v2.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AndroidUtil {

    public boolean requestPermisions(Activity activity, Context context, String[] permissions, int requestCode) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
            return true;
        }
        return false;
    }

}
