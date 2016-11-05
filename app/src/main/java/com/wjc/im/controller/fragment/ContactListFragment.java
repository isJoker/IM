package com.wjc.im.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.wjc.im.IMApplication;
import com.wjc.im.R;
import com.wjc.im.controller.activity.AddContactActivity;
import com.wjc.im.controller.activity.InviteActivity;
import com.wjc.im.utils.LogUtil;
import com.wjc.im.utils.MyConstants;
import com.wjc.im.utils.PreferenceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${万嘉诚} on 2016/11/2.
 * WeChat：wjc398556712
 * Function：联系人列表页面
 */

public class ContactListFragment extends EaseContactListFragment {

    @Bind(R.id.iv_contact_red)
    ImageView iv_contact_red;
    @Bind(R.id.ll_contact_invite)
    LinearLayout ll_contact_invite;
    @Bind(R.id.ll_contact_group)
    LinearLayout ll_contact_group;

    private Context mContext ;

    private LocalBroadcastManager mLBM;

    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED, true);
        }
    };

    public ContactListFragment() {
        mContext = IMApplication.getGlobalApplication();
    }

    @Override
    protected void initView() {
        super.initView();

        // 布局显示加号
        titleBar.setRightImageResource(R.drawable.em_add);
        // 添加头布局
        View headerView = View.inflate(getActivity(), R.layout.header_fragment_contact, null);

//        ButterKnife.bind(getActivity(), headerView);
//
        iv_contact_red = (ImageView) headerView.findViewById(R.id.iv_contact_red);
        ll_contact_invite = (LinearLayout) headerView.findViewById(R.id.ll_contact_invite);

        LogUtil.e("iv_contact_red----------------------------" + iv_contact_red);

        listView.addHeaderView(headerView);

        // 设置listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {

            }
        });


    }

    @Override
    protected void setUpView() {
        super.setUpView();

        // 添加按钮的点击事件处理
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });

        // 初始化红点显示
        boolean isNewInvite = PreferenceUtils.getBoolean(mContext, MyConstants.IS_NEW_INVITE_RED);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);

        // 邀请信息条目点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 红点处理
                iv_contact_red.setVisibility(View.GONE);
                PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED, false);

                //跳转到邀请界面
                Intent intent = new Intent(mContext, InviteActivity.class);
                startActivity(intent);
            }
        });

        // 注册广播
        mLBM = LocalBroadcastManager.getInstance(mContext);
        mLBM.registerReceiver(ContactInviteChangeReceiver, new IntentFilter(MyConstants.CONTACT_INVITE_CHANGED));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
