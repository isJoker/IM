package com.wjc.im.modul.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wjc.im.modul.bean.GroupInfo;
import com.wjc.im.modul.bean.InvationInfo;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.modul.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/11/2.
 * WeChat：wjc398556712
 * Function：邀请信息表的操作类
 */

public class InviteTableDao {
    private DBHelper mDbHelper;

    public InviteTableDao(DBHelper mDbHelper) {
        this.mDbHelper = mDbHelper;
    }

    // 添加邀请
    public void addInvitation(InvationInfo invitationInfo) {
        // 获取数据库链接
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // 执行添加语句
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_REASON, invitationInfo.getReason());// 原因
        values.put(InviteTable.COL_STATUS, invitationInfo.getStatus().ordinal());// 状态 ordinal()下标

        MyUserInfo userInfo = invitationInfo.getUser();

        if(userInfo == null) {//群组邀请
            values.put(InviteTable.COL_GROUP_HXID,invitationInfo.getGroup().getGroupId());
            values.put(InviteTable.COL_GROUP_NAME,invitationInfo.getGroup().getGroupName());
            values.put(InviteTable.COL_USER_HXID,invitationInfo.getGroup().getInvatePerson());

        } else {//联系人邀请
            values.put(InviteTable.COL_USER_HXID,invitationInfo.getUser().getHxid());
            values.put(InviteTable.COL_USER_NAME,invitationInfo.getUser().getName());

        }

        database.replace(InviteTable.TAB_NAME,null,values);

    }

    // 获取所有邀请信息
    public List<InvationInfo> getInvitations() {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String sql = "select * from " + InviteTable.TAB_NAME;
        Cursor cursor = database.rawQuery(sql, null);

        List<InvationInfo> invationInfos = new ArrayList<>();

        while (cursor.moveToNext()){
            InvationInfo info = new InvationInfo();
            info.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            info.setStatus(int2InviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS))));

            String groupId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID));
            if(groupId == null) {//用户邀请信息
                MyUserInfo userInfo = new MyUserInfo();
                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));

                info.setUser(userInfo);
            } else {//群组邀请信息
                GroupInfo groupInfo = new GroupInfo();

                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setInvatePerson(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));

                info.setGroup(groupInfo);
            }
            // 添加本次循环的邀请信息到总的集合中
            invationInfos.add(info);

        }

        // 关闭资源
        cursor.close();
        return invationInfos;
    }

    // 将int类型状态转换为邀请的状态
    private InvationInfo.InvitationStatus int2InviteStatus(int intStatus) {
        if (intStatus == InvationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.NEW_INVITE;
        }

        if (intStatus == InvationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvationInfo.InvitationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }

    // 删除邀请
    public void removeInvitation(String hxId) {
        if(hxId == null) {
            return;
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        database.delete(InviteTable.TAB_NAME,InviteTable.COL_USER_HXID + "=?", new String[]{hxId});

    }

    // 更新邀请状态
    public void updateInvitationStatus(InvationInfo.InvitationStatus invitationStatus, String hxId) {

        if(hxId == null) {
            return;
        }

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_STATUS,invitationStatus.ordinal());

        database.update(InviteTable.TAB_NAME,values,InviteTable.COL_USER_HXID + "=?", new String[]{hxId});
    }
}
