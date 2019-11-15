package entity;


import java.io.Serializable;
import java.util.ArrayList;

public class Face implements Serializable {
    private String face_token;
    private Location location;
    private ArrayList<User> user_list;

    public String getFace_token() {
        return face_token;
    }

    public void setFace_token(String face_token) {
        this.face_token = face_token;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<User> getUser_list() {
        return user_list;
    }

    public void setUser_list(ArrayList<User> user_list) {
        this.user_list = user_list;
    }
}
