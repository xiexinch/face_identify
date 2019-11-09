package com.hahaha.face_v2.postbodys;

import java.io.Serializable;
import java.util.ArrayList;

import entity.Face;

public class FaceSearchResBody implements Serializable {
    private Integer face_num;
    //private ArrayList<Face> face_list;
    private String face_list;

    public Integer getFace_num() {
        return face_num;
    }

    public void setFace_num(Integer face_num) {
        this.face_num = face_num;
    }

    public String getFace_list() {
        return face_list;
    }

    public void setFace_list(String face_list) {
        this.face_list = face_list;
    }
}
