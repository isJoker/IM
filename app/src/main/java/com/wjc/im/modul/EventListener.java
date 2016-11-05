package com.wjc.im.modul;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.wjc.im.modul.bean.InvitationInfo;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.utils.MyConstants;
import com.wjc.im.utils.PreferenceUtils;

/**
 * Created by ${万嘉诚} on 2016/11/4.
 * WeChat：wjc398556712
 * Function：全局事件的监听类
 */

public class EventListener {

    private static Context mContext;
    private final LocalBroadcastManager manager;

    public EventListener(Context mContext) {
        this.mContext = mContext;

        // 创建一个发送广播的管理者对象
        manager = LocalBroadcastManager.getInstance(mContext);

        // 注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(mContactListener);

        // 注册一个群信息变化的监听

    }

    // 注册一个联系人变化的监听
    private final EMContactListener mContactListener = new EMContactListener() {

        // 联系人增加后执行的方法
        @Override
        public void onContactAdded(String hxid) {
            // 数据库数据更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new MyUserInfo(hxid),true);

            // 发送联系人变化的广播（更新界面UI）
            manager.sendBroadcast(new Intent(MyConstants.CONTACT_CHANGED));

        }

        // 联系人删除后执行的方法
        @Override
        public void onContactDeleted(String hxid) {
            // 数据库数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);

            // 发送联系人变化的广播（更新界面UI）
            manager.sendBroadcast(new Intent(MyConstants.CONTACT_CHANGED));

        }

        // 接受到联系人的新邀请
        @Override
        public void onContactInvited(String hxid, String reason) {
            // 数据库更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new MyUserInfo(hxid));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            // 红点的处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);
            // 发送邀请信息变化的广播
            manager.sendBroadcast(new Intent(MyConstants.CONTACT_INVITE_CHANGED));

        }

        // 别人同意了你的好友邀请
        @Override
        public void onContactAgreed(String hxid) {
            // 数据库数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new MyUserInfo(hxid));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);// 别人同意了你的邀请

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            // 红点的处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);
            // 发送邀请信息变化的广播
            manager.sendBroadcast(new Intent(MyConstants.CONTACT_INVITE_CHANGED));


        }

        // 别人拒绝了你好友邀请
        @Override
        public void onContactRefused(String s) {
            // 红点的处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);
            // 发送邀请信息变化的广播
            manager.sendBroadcast(new Intent(MyConstants.CONTACT_INVITE_CHANGED));

        }
    };
}
