package com.wjc.im.modul.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.modul.db.DBHelper;
import com.wjc.im.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/11/2.
 * WeChat：wjc398556712
 * Function：联系人表的操作类
 */

public class ContactTableDao {
    private DBHelper mHelper;

    public ContactTableDao(DBHelper helper) {
        mHelper = helper;
    }

    // 获取所有联系人
    public List<MyUserInfo> getContacts() {

        // 获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // 执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_IS_CONTACT + "=1";//所有的联系人
        Cursor cursor = db.rawQuery(sql, null);

        List<MyUserInfo> users = new ArrayList<>();

        while (cursor.moveToNext()) {
            MyUserInfo userInfo = new MyUserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));

            users.add(userInfo);
        }

        // 关闭资源
        cursor.close();

        // 返回数据
        return users;
    }

    // 通过环信id获取联系人单个信息
    public MyUserInfo getContactByHx(String hxId) {

        if (hxId == null) {
            return null;
        }

        // 获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // 执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_HXID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});

        MyUserInfo userInfo = null;

        if (cursor.moveToNext()) {
            userInfo = new MyUserInfo();

            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
        }

        // 关闭资源
        cursor.close();

        // 返回数据
        return userInfo;
    }

    // 通过环信id获取用户联系人信息
    public List<MyUserInfo> getContactsByHx(List<String> hxIds) {

        if (hxIds == null || hxIds.size() <= 0) {
            return null;
        }

        List<MyUserInfo> contacts = new ArrayList<>();

        // 遍历hxIds，来查找
        for (String hxid : hxIds) {
            MyUserInfo contact = getContactByHx(hxid);

            contacts.add(contact);
        }

        // 返回查询的数据
        return contacts;
    }

    // 保存单个联系人
    public void saveContact(MyUserInfo user, boolean isMyContact) {

        if (user == null) {
            return;
        }

        // 获取数据库链接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        // 执行保存语句
        ContentValues values = new ContentValues();
        values.put(ContactTable.COL_HXID, user.getHxid());
        values.put(ContactTable.COL_NAME, user.getName());
        values.put(ContactTable.COL_NICK, user.getNick());
        values.put(ContactTable.COL_PHOTO, user.getPhoto());
        values.put(ContactTable.COL_IS_CONTACT, isMyContact ? 1 : 0);

        LogUtil.e("1111111111111111111111111111111111111111111111");

        db.replace(ContactTable.TAB_NAME, null, values);
    }

    // 保存联系人信息
    public void saveContacts(List<MyUserInfo> contacts, boolean isMyContact) {
        if(contacts == null || contacts.size() == 0) {
            return;
        }

        LogUtil.e("2222222222222222222222222222222222222---contacts=====" + contacts);

        for (MyUserInfo userInfo : contacts) {
            LogUtil.e("33333333333333333333333333333333333333333---userInfo====" + userInfo);
            saveContact(userInfo,isMyContact);
        }
    }

    // 删除联系人信息
    public void deleteContactByHxId(String hxId){
        if(hxId == null) {
            return;
        }

        SQLiteDatabase database = mHelper.getReadableDatabase();
        database.delete(ContactTable.TAB_NAME,ContactTable.COL_NICK + "=?",new String[]{hxId});
    }

}
