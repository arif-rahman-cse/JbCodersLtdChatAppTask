package com.arif.jbcodersltdchatapptask.model;

import android.app.Application;

public class UserClient extends Application {

    private Users users = null;

    public Users getUser() {
        return users;
    }

    public void setUser(Users users) {
        this.users = users;
    }
}
