package com.wjc.im.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.wjc.im.R;
import com.wjc.im.modul.Model;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.utils.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${万嘉诚} on 2016/11/2.
 * WeChat：wjc398556712
 * Function：添加联系人页面
 */

public class AddContactActivity extends Activity {


    @Bind(R.id.tv_add_find)
    TextView tvAddFind;
    @Bind(R.id.et_add_name)
    EditText etAddName;
    @Bind(R.id.iv_add_photo)
    ImageView ivAddPhoto;
    @Bind(R.id.tv_add_name)
    TextView tvAddName;
    @Bind(R.id.bt_add_add)
    Button btAddAdd;
    @Bind(R.id.rl_add)
    RelativeLayout rlAdd;
    private MyUserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);

        initView();

        initListener();
    }

    private void initListener() {
        // 查找按钮的点击事件处理
        tvAddFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                find();
            }
        });
        // 添加按钮的点击事件处理
        btAddAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.e("add----------------------------click");
                add();
            }
        });

    }

    // 添加按钮处理
    private void add() {
        LogUtil.e("add----------------------------");

        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(userInfo.getName(),"我是阿诚");
                    LogUtil.e("添加好友邀请成功");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "添加好友邀请成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, "添加好友邀请失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    // 查找按钮的处理
    private void find() {
        // 获取输入的用户名称
        final String name = etAddName.getText().toString();

        //  校验输入的名称
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(AddContactActivity.this, "输入的用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 去服务器判断当前用户是否存在(联网分线程)
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去服务器判断当前查找的用户是否存在（没有服务器，直接默认存在此用户）
                userInfo = new MyUserInfo(name);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rlAdd.setVisibility(View.VISIBLE);
                        tvAddName.setText(userInfo.getName());
                    }
                });
            }
        });


    }

    private void initView() {
        ButterKnife.bind(this);
    }
}
