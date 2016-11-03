package com.wjc.im.modul.bean;

/**
 * Created by ${万嘉诚} on 2016/11/3.
 * WeChat：wjc398556712
 * Function：
 */

public class MyUser {

    private String nickname;
    private String imgurl;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MyUser() {
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
