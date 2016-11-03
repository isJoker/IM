package com.wjc.im.modul.db;

import com.wjc.im.modul.bean.MyUser;

/**
 * Created by ${万嘉诚} on 2016/11/3.
 * WeChat：wjc398556712
 * Function：EventBus   定义事件类型
 */

public class LoginEvent {

    public MyUser mMyUser;

    public LoginEvent(MyUser myUser) {
        mMyUser = myUser;
    }
}
