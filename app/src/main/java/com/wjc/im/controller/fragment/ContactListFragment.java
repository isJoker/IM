package com.wjc.im.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.wjc.im.IMApplication;
import com.wjc.im.R;
import com.wjc.im.controller.activity.AddContactActivity;
import com.wjc.im.controller.activity.ChatActivity;
import com.wjc.im.controller.activity.GroupListActivity;
import com.wjc.im.controller.activity.InviteActivity;
import com.wjc.im.modul.Model;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.utils.LogUtil;
import com.wjc.im.utils.MyConstants;
import com.wjc.im.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String mHxid;

    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 更新红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            PreferenceUtils.putBoolean(mContext, MyConstants.IS_NEW_INVITE_RED, true);
        }
    };

    private BroadcastReceiver ContactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 刷新页面
            refreshContact();
        }
    };

    private BroadcastReceiver GroupChangeReceiver = new BroadcastReceiver() {
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
        titleBar.setTitle("联系人");
        // 添加头布局
        View headerView = View.inflate(getActivity(), R.layout.header_fragment_contact, null);

//        ButterKnife.bind(headerView);
        iv_contact_red = (ImageView) headerView.findViewById(R.id.iv_contact_red);
        ll_contact_invite = (LinearLayout) headerView.findViewById(R.id.ll_contact_invite);


        listView.addHeaderView(headerView);

        // 设置listview条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if(user == null) {//当点击listview头布局时user为空
                    return;
                }

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                // 传递参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());

                startActivity(intent);

            }
        });

        // 跳转到群组列表页面
        LinearLayout ll_contact_group = (LinearLayout) headerView.findViewById(R.id.ll_contact_group);
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);

                startActivity(intent);
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
        mLBM.registerReceiver(ContactChangeReceiver,new IntentFilter(MyConstants.CONTACT_CHANGED));
        mLBM.registerReceiver(GroupChangeReceiver,new IntentFilter(MyConstants.GROUP_INVITE_CHANGED));

        // 从环信服务器获取所有的联系人信息
        getContactFromHxServer();

        // 绑定listview和contextmenu
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // 获取环信id
        int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;

        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        mHxid = easeUser.getUsername();

        // 添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete,menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.contact_delete) {
            // 执行删除选中的联系人操作
            deleteContact();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * // 执行删除选中的联系人操作
     */
    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //在服务器上删除联系人
                    EMClient.getInstance().contactManager().deleteContact(mHxid);

                    // 本地数据库的更新
                    Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(mHxid);

                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // toast提示
                            Toast.makeText(getActivity(), "删除" + mHxid + "成功", Toast.LENGTH_SHORT).show();

                            // 刷新页面
                            refreshContact();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();

                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "删除" + mHxid + "失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    /**
     *  从环信服务器获取所有的联系人信息
     */
    private void getContactFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取到所有的好友的环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    LogUtil.e("hxids----------" + hxids.size());

                    // 校验
                    if (hxids != null && hxids.size() >= 0) {
                        List<MyUserInfo> contacts = new ArrayList<MyUserInfo>();

                        // 转换
                        for (String hxid : hxids) {
                            MyUserInfo contact = new MyUserInfo(hxid);
                            contacts.add(contact);
                        }

                        // 保存好友信息到本地数据库
                        LogUtil.e("contacts-----------------" + contacts);
                        Model.getInstance().getDbManager().getContactTableDao().saveContacts(contacts,true);
                        
                        if(getActivity() == null) {//切换到别的Tab时getActivity() == null
                            return;
                        }

                        // 刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新页面的方法
                                refreshContact();
                            }
                        });

                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    // 刷新页面
    private void refreshContact() {
        // 获取数据
        List<MyUserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        LogUtil.e("contacts---------" + contacts);

        // 校验
        if (contacts != null && contacts.size() >= 0) {
            // 设置数据
            Map<String,EaseUser> contactsMap = new HashMap<>();
            // 转换
            for (MyUserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());

                contactsMap.put(contact.getHxid(),easeUser);
            }

            setContactsMap(contactsMap);
            LogUtil.e("contactsMap-----------" + contactsMap.size());

            // 刷新页面
            LogUtil.e("refresh-----------");
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLBM.unregisterReceiver(ContactInviteChangeReceiver);
        mLBM.unregisterReceiver(ContactChangeReceiver);
        mLBM.unregisterReceiver(GroupChangeReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
