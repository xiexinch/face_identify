package com.hahaha.face_v2.postbodys;

public class PostBody {
    String img;
    String filename;

    public PostBody(String img, String filename) {
        this.img = img;
        this.filename = filename;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
