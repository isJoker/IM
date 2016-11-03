package com.wjc.im.modul.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wjc.im.modul.dao.ContactTable;
import com.wjc.im.modul.dao.InviteTable;

/**
 * Created by ${万嘉诚} on 2016/11/1.
 * WeChat：wjc398556712
 * Function：
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 创建联系人的表
        sqLiteDatabase.execSQL(ContactTable.CREATE_TAB);
        // 创建邀请信息的表
        sqLiteDatabase.execSQL(InviteTable.CREATE_TAB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
