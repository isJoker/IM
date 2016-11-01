package com.wjc.im.modul.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wjc.im.modul.dao.UserAccountTable;

/**
 * Created by ${万嘉诚} on 2016/11/1.
 * WeChat：wjc398556712
 * Function：
 */

public class UserAccountDB extends SQLiteOpenHelper {

    public UserAccountDB(Context context) {
        super(context, "accound.db", null, 1);
    }

    // 数据库创建的时候调用
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建数据库表的语句
        sqLiteDatabase.execSQL(UserAccountTable.CREATE_TAB);
    }

    // 数据库更新的时候调用
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
