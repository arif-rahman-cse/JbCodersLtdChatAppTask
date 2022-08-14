package com.arif.jbcodersltdchatapptask.model;

public class MyPathasathi {

    private String name;
    private String avatar;
    private String user_id;

    public MyPathasathi(String name, String avatar, String user_id) {
        this.name = name;
        this.avatar = avatar;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
