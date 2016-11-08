package com.wjc.im.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ${万嘉诚} on 2016/11/8.
 * WeChat：wjc398556712
 * Function：设置界面工具类
 */

public class SettingFragmentSPUtil {

    private SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private String SHARED_KEY_NOTIFY = "shared_key_notify";
    private String SHARED_KEY_VOICE = "shared_key_sound";
    private String SHARED_KEY_VIBRATE = "shared_key_vibrate";

    public SettingFragmentSPUtil(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    // 是否允许推送通知
    public boolean isAllowPushNotify() {
        return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
    }

    public void setPushNotifyEnable(boolean isChecked) {
        mEditor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
        mEditor.commit();
    }

    // 允许声音
    public boolean isAllowVoice() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
    }

    public void setAllowVoiceEnable(boolean isChecked) {
        mEditor.putBoolean(SHARED_KEY_VOICE, isChecked);
        mEditor.commit();
    }

    // 允许震动
    public boolean isAllowVibrate() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
    }

    public void setAllowVibrateEnable(boolean isChecked) {
        mEditor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
        mEditor.commit();
    }
}
