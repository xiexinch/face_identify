package com.hahaha.face_v2.postbodys;


import com.alibaba.fastjson.JSONObject;

public class checkBody {
    public JSONObject getResult() {
        return result;
    }

    public void setResult(JSONObject result) {
        this.result = result;
    }

    private JSONObject result;

    public String getError_code() {
        return error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    private String error_msg;

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    private String error_code;
}
