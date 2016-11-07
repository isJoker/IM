package com.wjc.im.modul;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.wjc.im.modul.bean.GroupInfo;
import com.wjc.im.modul.bean.InvitationInfo;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.utils.LogUtil;
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
        EMClient.getInstance().groupManager().addGroupChangeListener(eMGroupChangeListener);
    }

    // 群信息变化的监听
    private final EMGroupChangeListener eMGroupChangeListener = new EMGroupChangeListener() {

        //收到 群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            // 数据更新
            LogUtil.e("groupId-------------------------->>>>>" + groupId);
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);

            // 发送广播
            manager.sendBroadcast(new Intent(MyConstants.GROUP_INVITE_CHANGED));
        }

        //收到 群申请通知
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applicant, String reason) {

            // 数据更新
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, applicant));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);

            // 发送广播
            manager.sendBroadcast(new Intent(MyConstants.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被接受
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            // 更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,accepter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);

            // 发送广播
            manager.sendBroadcast(new Intent(MyConstants.GROUP_INVITE_CHANGED));
        }

        //收到 群申请被拒绝
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,decliner));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);

            // 发送广播
            manager.sendBroadcast(new Intent(MyConstants.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {

            // 更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));//在这里我们之前让 groupId 和groupName相同
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);

            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            // 红点处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);

            // 发送广播
            manager.sendBroadcast(new Intent(MyConstants.GROUP_INVITE_CHANGED));
        }

        //收到 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {
            // 更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);

            // 红点处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);

            // 发送广播
            manager.sendBroadcast(new Intent(MyConstants.GROUP_INVITE_CHANGED));
        }

        //收到 群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //收到 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            // 更新数据
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(inviteMessage);
            invitationInfo.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);

            // 红点处理
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED,true);

            // 发送广播
            manager.sendBroadcast(new Intent(MyConstants.GROUP_INVITE_CHANGED));
        }
    };

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
