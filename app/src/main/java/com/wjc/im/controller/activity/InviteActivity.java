package com.wjc.im.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.wjc.im.R;
import com.wjc.im.controller.adapter.InviteAdapter;
import com.wjc.im.modul.Model;
import com.wjc.im.modul.bean.InvitationInfo;
import com.wjc.im.utils.LogUtil;
import com.wjc.im.utils.MyConstants;

import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/11/4.
 * WeChat：wjc398556712
 * Function：邀请信息列表页面
 */

public class InviteActivity extends Activity {
    private ListView lv_invite;
    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        @Override
        public void onAccept(final InvitationInfo invitationInfo) {
            // 通知环信服务器，点击了接受按钮
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invitationInfo.getUser().getHxid());

                        // 数据库更新
                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT,invitationInfo.getUser().getHxid());
                        //界面更新
                        runOnUiThread(new Runnable() {// 页面发生变化
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();
                                // 刷新页面
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }

        @Override
        public void onReject(final InvitationInfo invitationInfo) {

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String hxid = invitationInfo.getUser().getHxid();
                        EMClient.getInstance().contactManager().declineInvitation(hxid);
                        //更新数据库
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);
                        //更新界面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT).show();
                                // 刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }

        // 接受邀请按钮
        @Override
        public void onInviteAccept(final InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 告诉环信服务器接受了邀请
                        LogUtil.e("invitationInfo.getGroup().getGroupId()-------" + invitationInfo.getGroup().getGroupId());
                        LogUtil.e("invitationInfo.getGroup().getInvitePerson()------" + invitationInfo.getGroup().getInvitePerson());
                        EMClient.getInstance().groupManager().acceptInvitation(invitationInfo.getGroup().getGroupId(),invitationInfo.getGroup().getInvitePerson());
                        // 本地数据更新
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        // 内存数据的变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();
                                //刷新
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }

        // 拒绝邀请按钮
        @Override
        public void onInviteReject(final InvitationInfo invitationInfo) {
            LogUtil.e("拒绝邀请按钮--------------------------");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroup().getGroupId(),invitationInfo.getGroup().getInvitePerson(),"拒绝邀请");

                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        // 更新内存的数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT).show();

                                // 刷新页面
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        // 接受申请按钮
        @Override
        public void onApplicationAccept(final InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 告诉环信服务器接受了申请
                        EMClient.getInstance().groupManager().acceptApplication(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson());

                        // 更新数据库
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        // 更新内存
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受申请", Toast.LENGTH_SHORT).show();

                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        // 拒绝申请按钮
        @Override
        public void onApplicationReject(final InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 告诉环信服务器拒绝了申请
                        EMClient.getInstance().groupManager().declineApplication(invitationInfo.getGroup().getGroupId(),invitationInfo.getGroup().getInvitePerson(),"拒绝申请");

                        // 更新本地数据库
                        invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

                        // 更新内存
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请", Toast.LENGTH_SHORT).show();

                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

    };
    private InviteAdapter adapter;
    private LocalBroadcastManager manager;
    private BroadcastReceiver inviteBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        initView();

        initData();
    }

    private void initData() {
        // 设置listview 的适配器
        adapter = new InviteAdapter(this,mOnInviteListener);

        lv_invite.setAdapter(adapter);

        //刷新
        refresh();

        // 注册邀请信息变化的广播
        manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(inviteBroadcastReceiver,new IntentFilter(MyConstants.CONTACT_INVITE_CHANGED));
        manager.registerReceiver(inviteBroadcastReceiver,new IntentFilter(MyConstants.GROUP_INVITE_CHANGED));


    }

    private void refresh() {
        // 获取数据库中的所有邀请信息
        List<InvitationInfo> invitations = Model.getInstance().getDbManager().getInviteTableDao().getInvitations();
        //刷新适配器
        LogUtil.e("invitations------------------------InviteActivity-------refresh()" + + invitations.size()) ;
        adapter.refresh(invitations);
    }

    private void initView() {
        lv_invite = (ListView)findViewById(R.id.lv_invite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //广播接收器解注册
        manager.unregisterReceiver(inviteBroadcastReceiver);
    }
}
