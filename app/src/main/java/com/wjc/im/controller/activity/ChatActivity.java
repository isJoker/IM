package com.wjc.im.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.wjc.im.R;
import com.wjc.im.utils.LogUtil;
import com.wjc.im.utils.MyConstants;

public class ChatActivity extends FragmentActivity {

    private EaseChatFragment chatFragment;
    private String mHxid;
    private LocalBroadcastManager mLBM;
    private int mChatType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initData();

        initListener();
    }

    private void initListener() {

        chatFragment.setChatFragmentListener(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            @Override
            public void onEnterToChatDetails() {
                Intent intent = new Intent(ChatActivity.this, GroupDetailActivity.class);
                // 群id
                intent.putExtra(MyConstants.GROUP_ID, mHxid);
                LogUtil.e("initListener==>mHxid=========================>" + mHxid);

                startActivity(intent);

                finish();
            }

            @Override
            public void onAvatarClick(String username) {

            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {

            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });

        // 如果当前类型为群聊
        if(mChatType == EaseConstant.CHATTYPE_GROUP) {
            // 注册退群广播
            BroadcastReceiver ExitGroupReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if(mHxid.equals(intent.getStringExtra(MyConstants.GROUP_ID))) {
                        // 结束当前页面
                        finish();
                    }
                }
            };

            mLBM.registerReceiver(ExitGroupReceiver,new IntentFilter(MyConstants.EXIT_GROUP));
        }

    }

    private void initData() {
        // 创建一个会话的fragment
        chatFragment = new EaseChatFragment();

        mHxid = getIntent().getStringExtra(EaseConstant.EXTRA_USER_ID);
        LogUtil.e("initData===>mHxid=============>" + mHxid);

        // 获取聊天类型
        String mChatType = getIntent().getStringExtra(EaseConstant.EXTRA_CHAT_TYPE);
        chatFragment.setArguments(getIntent().getExtras());

        // 替换fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_chat,chatFragment).commit();

        // 获取发送广播的管理者
        mLBM = LocalBroadcastManager.getInstance(this);
    }
}
