package com.hahaha.face_v2.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Util {

    public Util() {
    }

    public static byte[] convertToBytes(InputStream inputStream) throws Exception {
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
