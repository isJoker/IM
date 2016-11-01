package com.wjc.im.modul.db;

import android.content.Context;

/**
 * Created by ${万嘉诚} on 2016/11/1.
 * WeChat：wjc398556712
 * Function：联系人和邀请信息表的操作类的管理类
 */

public class DBManager {

    private final DBHelper dbHelper;

    public DBManager(Context context, String name) {
        // 创建数据库
        dbHelper = new DBHelper(context, name);

        // 创建该数据库中两张表的操作类
    }

    // 关闭数据库的方法
    public void close() {
        dbHelper.close();
    }


}
