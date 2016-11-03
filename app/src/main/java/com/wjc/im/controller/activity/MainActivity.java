package com.wjc.im.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.RadioGroup;

import com.wjc.im.R;
import com.wjc.im.controller.fragment.ChatFragment;
import com.wjc.im.controller.fragment.ContactListFragment;
import com.wjc.im.controller.fragment.SettingFragment;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();
    }

    private void initView() {
        rg_main = (RadioGroup)findViewById(R.id.rg_main);
    }

    private void initData() {
        //创建三个Fragment
        chatFragment = new ChatFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();
    }

    private void initListener() {
        //RadioGroup的选择事件
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Fragment fragment = null;
                switch (checkedId) {
                    case R.id.rb_main_conversation:
                        fragment = chatFragment;
                        break;
                    case R.id.rb_main_contact:
                        fragment = contactListFragment;
                        break;
                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;
                }

                switchFragment(fragment);
            }
        });
        // 默认选择会话列表页面
        rg_main.check(R.id.rb_main_conversation);
    }

    // 实现fragment切换的方法
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main,fragment).commit();
    }
}
