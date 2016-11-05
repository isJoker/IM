package com.wjc.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wjc.im.R;
import com.wjc.im.modul.Model;
import com.wjc.im.modul.bean.MyUser;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.utils.LogUtil;
import com.wjc.im.utils.MyConstants;
import com.wjc.im.utils.PreferenceUtils;
import com.wjc.im.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.huawei.android.pushselfshow.richpush.html.HtmlViewer.TAG;

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
    @Bind(R.id.is_password_memory)
    CheckBox isPasswordMemory;
    @Bind(R.id.qq_login)
    ImageView qqLogin; //QQ登录
    @Bind(R.id.sina_login)
    ImageView sinaLogin;//新浪登录
    @Bind(R.id.wx_login)
    ImageView wxLogin; //微信登录
    private String userName;
    private String userPassword;

    private static Tencent mTencent;
    private UserInfo mInfo = null;
    private MyUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

//        EventBus.getDefault().register(this);

        //QQ的初始化
        mTencent = Tencent.createInstance("1105704769", this.getApplicationContext());
        mInfo = new UserInfo(this, mTencent.getQQToken());
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLoginEvent(MyUser myUser){
//        LogUtil.e("EventBus订阅者");
//        String nickname = myUser.getNickname();
//        String password = myUser.getPassword();
//        hxRegist(nickname, password);
//        hxLogin(nickname, nickname);
//    }



    private void initData() {
        userName = PreferenceUtils.getString(this, MyConstants.USER_NAME);
        userPassword = PreferenceUtils.getString(this, MyConstants.USER_PASSWORD);
        etLoginName.setText(userName);
        etLoginPwd.setText(userPassword);
        if (!TextUtils.isEmpty(userPassword)) {//密码不为空
            isPasswordMemory.setChecked(true);
        } else { //密码为空
            isPasswordMemory.setChecked(false);
        }
    }

    // 正常登录监听
    @OnClick(R.id.bt_login_login)
    void bt_login_login_click(View view) {
        // 1 获取输入的用户名和密码
        final String loginName = etLoginName.getText().toString().trim();
        final String loginPwd = etLoginPwd.getText().toString().trim();

        // 2 校验用户名和密码
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 3 去环信服务器登录
        hxLogin(loginName, loginPwd);
    }

    private void hxLogin(final String loginName, final String loginPwd) {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        //保存上一次登录的用户名
                        PreferenceUtils.putString(LoginActivity.this, MyConstants.USER_NAME, loginName);
                        // 判断是否保存用户名的密码
                        if (!isPasswordMemory.isChecked()) {//如果用户没有点击记住密码  那就清除密码
                            PreferenceUtils.putString(LoginActivity.this, MyConstants.USER_PASSWORD, "");
                        } else {
                            PreferenceUtils.putString(LoginActivity.this, MyConstants.USER_PASSWORD, loginPwd);
                        }

                        MyUserInfo account = new MyUserInfo(loginName);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                        });
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
    void bt_login_regist_click(View view) {
        // 1 获取输入的用户名和密码
        final String registName = etLoginName.getText().toString();
        final String registPwd = etLoginPwd.getText().toString();

        // 2 校验用户名和密码
        if (TextUtils.isEmpty(registName) || TextUtils.isEmpty(registPwd)) {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3 去环信服务器注册
        hxRegist(registName, registPwd);
    }

    private void hxRegist(final String registName, final String registPwd) {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 注册
                try {
                    EMClient.getInstance().createAccount(registName, registPwd);
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

    @OnClick(R.id.qq_login)
    void qq_login_click() {
        mTencent.login(this, "all", loginListener);
    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            Log.d(TAG, "ruolanmingyue:" + values);
            Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);

            //下面的这个必须放到这个地方，要不然就会出错   哎，，，，，调整了近一个小时，，，，我是服我自己了
            updateUserInfo();
        }
    };

    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {

                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                    new Thread() {

                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;
                            if (json.has("figureurl")) {
                                Bitmap bitmap = null;
                                try {
                                    bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
                                } catch (JSONException e) {

                                }
//                                Message msg = new Message();
//                                msg.obj = bitmap;
//                                msg.what = 1;
//                                mHandler.sendMessage(msg);
                            }
                        }

                    }.start();
                    finish();
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
//            mUserInfo.setText("");
//            mUserInfo.setVisibility(android.view.View.GONE);
//            mUserLogo.setVisibility(android.view.View.GONE);
        }
    }


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            JSONObject response = (JSONObject) msg.obj;

            String nickname = null;
            String imgurl = null;

            try {
                LogUtil.e("response==========" + response);
                nickname = response.getString("nickname");
                imgurl = response.getString("figureurl_qq_2");
                String password = "123456";

                myUser = new MyUser();
                myUser.setNickname(nickname);
                myUser.setImgurl(imgurl);
                myUser.setPassword(password);

                PreferenceUtils.putString(LoginActivity.this,
                        MyConstants.USER_NAME, nickname);
                PreferenceUtils.putString(LoginActivity.this,
                        MyConstants.USER_PASSWORD, "123456");

                LogUtil.e("nickname=========" + nickname);

                //注册 + 登录
                if(!PreferenceUtils.getBoolean(LoginActivity.this, MyConstants.IS_LOGIN)) {
                    hxRegist(nickname, password);
                }

                hxLogin(nickname, password);

                PreferenceUtils.putBoolean(LoginActivity.this, MyConstants.IS_LOGIN,true);
//                EventBus.getDefault().post(myUser);
                LogUtil.e("EventBus事件发布");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ruolan", "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                return;
            }
            doComplete((JSONObject) response);
        }

        @Override
        public void onError(UiError e) {

        }

        @Override
        public void onCancel() {

        }

        protected void doComplete(JSONObject values) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //进行解注册
        EventBus.getDefault().unregister(this);
//        LogUtil.e("解注册EventBus事件");
    }

}
