package com.wjc.im.controller.fragment;

import android.content.Intent;
import android.graphics.Bitmap;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.wjc.im.controller.activity.ChatActivity;
import com.wjc.im.modul.LoginEvent;
import com.wjc.im.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/11/2.
 * WeChat：wjc398556712
 * Function： 会话列表页面
 */

public class ChatFragment extends EaseConversationListFragment {

    private static Bitmap leftHeaderImage;//必须是静态

    @Override
    protected void initView() {
        super.initView();
        //粘性事件注册
        EventBus.getDefault().register(this);

        //设置头像显示
        titleBar.setLeftImageBitmap(leftHeaderImage);
        LogUtil.e("leftHeaderImage============>" + leftHeaderImage);

        //方法一：通过LoginActivity的getImageBitmap()获取
//        titleBar.setLeftImageBitmap(LoginActivity.getImageBitmap());

        // 点击item跳转到会话详情页面
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                // 传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.conversationId());

                // 是否是否群聊
                if(conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                }

                startActivity(intent);
            }
        });

        // 请空集合数据
        conversationList.clear();//解决从其他页面切换到会话页面时集合数据的重复增多
        // 监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(emMesageListener);
    }


    /**
     * 方法二：一般的EventBus事件
     * 订阅事件，有人会问，你的EventBus注册事件去哪儿了，在IMApplication里面
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLoginEvent(LoginEvent loginEvent){
//        LogUtil.e("leftHeaderImage----------------------------->" + loginEvent.getBitmap());
//        leftHeaderImage = loginEvent.getBitmap();
//        LogUtil.e("leftHeaderImage--->>>--->" + leftHeaderImage);
//    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onLoginEvent(LoginEvent loginEvent){
        LogUtil.e("leftHeaderImage----------------------------->" + loginEvent.getBitmap());
        leftHeaderImage = loginEvent.getBitmap();
        LogUtil.e("leftHeaderImage--->>>--->" + leftHeaderImage);
    }




    private EMMessageListener emMesageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            // 设置数据
            EaseUI.getInstance().getNotifier().onNewMesg(list);
            // 刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解注册EventBus
        EventBus.getDefault().unregister(this);
    }
}
