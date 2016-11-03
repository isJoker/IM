package com.wjc.im.modul.bean;

/**
 * Created by ${万嘉诚} on 2016/11/1.
 * WeChat：wjc398556712
 * Function：用户信息
 */

public class MyUserInfo {
    private String name;// 用户名称
    private String hxid;// 环信id
    private String nick;// 用户的昵称
    private String photo;// 头像

    public MyUserInfo() {

    }

    public MyUserInfo(String name) {
        this.name = name;
        this.hxid = name;
        this.nick = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHxid() {
        return hxid;
    }

    public void setHxid(String hxid) {
        this.hxid = hxid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "MyUserInfo{" +
                "name='" + name + '\'' +
                ", hxid='" + hxid + '\'' +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
