package com.wjc.im;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.wjc.im.modul.Model;
import com.wjc.im.utils.SettingFragmentSPUtil;

/**
 * Created by ${万嘉诚} on 2016/11/1.
 * WeChat：wjc398556712
 * Function：初始化
 */

public class IMApplication extends Application{
    private static Context mContext;
    private static IMApplication imApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        imApplication = this;

        // 初始化EaseUI
        EMOptions emOptions = new EMOptions();
        emOptions.setAutoAcceptGroupInvitation(false);// 不自动接受群邀请信息
        emOptions.setAcceptInvitationAlways(false);// 不总是一直接受所有邀请

        EaseUI.getInstance().init(this,emOptions);
        // 初始化模型层数据
        Model.getInstance().init(this);
        // 初始化全局上下文对象
        mContext = this;
        //注册EventBus事件
//        EventBus.getDefault().register(new ChatFragment());
    }

    public static IMApplication getInstance(){
        return imApplication;
    }

    // 获取全局上下文
    public static Context getGlobalApplication(){
        return mContext;
    }

    private SettingFragmentSPUtil mSpUtil;
    public static final String PREFERENCE_NAME = "_spinfo";

    /**
     * 单例模式，才能及时返回数据
     * @return
     */
    public synchronized SettingFragmentSPUtil getSPUtils(){
        if(mSpUtil == null) {
            String currentId = EMClient.getInstance().getCurrentUser();
            String spName = currentId + PREFERENCE_NAME;
            mSpUtil = new SettingFragmentSPUtil(this, spName);
        }

        return mSpUtil;
    }

}
