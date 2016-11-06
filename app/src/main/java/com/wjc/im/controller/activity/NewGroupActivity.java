package com.wjc.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.wjc.im.R;
import com.wjc.im.modul.Model;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.wjc.im.R.id.cb_newgroup_invite;

/**
 * Created by ${万嘉诚} on 2016/11/6.
 * WeChat：wjc398556712
 * Function：创建新群
 */
public class NewGroupActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    @Bind(R.id.et_newgroup_name)
    EditText etNewgroupName;
    @Bind(R.id.et_newgroup_desc)
    EditText etNewgroupDesc;
    @Bind(R.id.cb_newgroup_public)
    CheckBox cbNewgroupPublic;
    @Bind(cb_newgroup_invite)
    CheckBox cbNewgroupInvite;
    @Bind(R.id.bt_newgroup_create)
    Button btNewgroupCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initListener();
    }

    private void initListener() {
        //点击创建按钮的监听
        btNewgroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到选择联系人页面
                Intent intent = new Intent(NewGroupActivity.this,PickContactActivity.class);
                // 启动一个带结果的Activity
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 成功获取到联系人
        if(resultCode == RESULT_OK) {
            // 创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    private void createGroup(final String[] memberses) {
        //群名称
        final String groupName = etNewgroupName.getText().toString();
        //群描述
        final String groupDesc = etNewgroupDesc.getText().toString();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 去环信服务器创建群
                // 参数一：群名称；参数二：群描述；参数三：群成员；参数四：原因；参数五：参数设置
                EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();
                options.maxUsers = 1000;//群最多容纳多少人
                EMGroupManager.EMGroupStyle groupStyle = null;

                if(cbNewgroupPublic.isChecked()) {//公开
                    if(cbNewgroupInvite.isChecked()) {// 开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }

                } else {
                    if (cbNewgroupInvite.isChecked()) {// 开放群邀请
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    } else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }
                // 创建群的类型
                options.style = groupStyle;

                try {
                    EMClient.getInstance().groupManager().createGroup(groupName, groupDesc, memberses, "申请加入群", options);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群成功", Toast.LENGTH_SHORT).show();

                            // 结束当前页面
                            finish();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "创建群失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_new_group);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
