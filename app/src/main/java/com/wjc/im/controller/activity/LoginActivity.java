package com.wjc.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.wjc.im.R;
import com.wjc.im.modul.Model;
import com.wjc.im.modul.bean.UserInfo;
import com.wjc.im.utils.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ${万嘉诚} on 2016/11/1.
 * WeChat：wjc398556712
 * Function：登录界面
 */
public class LoginActivity extends Activity {

    @Bind(R.id.et_login_name)
    EditText etLoginName;
    @Bind(R.id.et_login_pwd)
    EditText etLoginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    // 登录监听
    @OnClick(R.id.bt_login_login)
    void bt_login_login_click(View view){
        // 1 获取输入的用户名和密码
        final String loginName = etLoginName.getText().toString();
        final String loginPwd = etLoginPwd.getText().toString();

        // 2 校验用户名和密码
        if(TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 3 去环信服务器登录
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        UserInfo account = new UserInfo(loginName);
                        // 模型层数据的初始化
                        Model.getInstance().loginSuccess(account);

                        // 保存登录信息
                        Model.getInstance().getUserAccountDao().addAccount(account);

                        // 跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        // 销毁当前页面
                        finish();
                    }

                    @Override
                    public void onError(int i, String s) {
                        LogUtil.e("登录服务器失败---->" + s.toString());
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });


    }

    // 注册监听
    @OnClick(R.id.bt_login_regist)
    void bt_login_regist_click(View view){
        // 1 获取输入的用户名和密码
        final String registName = etLoginName.getText().toString();
        final String registPwd = etLoginPwd.getText().toString();

        // 2 校验用户名和密码
        if (TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3 去环信服务器注册
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 注册
                try {
                    EMClient.getInstance().createAccount(registName,registPwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

}
