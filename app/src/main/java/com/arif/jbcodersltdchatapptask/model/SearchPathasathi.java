package com.arif.jbcodersltdchatapptask.model;

public class SearchPathasathi {

    private String name;
    private String url;
    private String user_id;

    public SearchPathasathi(String name, String url, String user_id) {
        this.name = name;
        this.url = url;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
