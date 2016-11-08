package com.wjc.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.wjc.im.R;
import com.wjc.im.controller.adapter.GroupDetailAdapter;
import com.wjc.im.modul.Model;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.utils.LogUtil;
import com.wjc.im.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${万嘉诚} on 2016/11/6.
 * WeChat：wjc398556712
 * Function：群详情界面
 */
public class GroupDetailActivity extends Activity {

    private static final int REQUEST_CODE = 2;
    @Bind(R.id.gv_groupdetail)
    GridView gvGroupdetail;
    @Bind(R.id.bt_groupdetail_out)
    Button btGroupdetailOut;
    private EMGroup mGroup;
    private GroupDetailAdapter adapter;
    private List<MyUserInfo> mUsers;

    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        // 添加群成员
        @Override
        public void onAddMembers() {
            // 跳转到选择联系人页面
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);

            // 传递群id
            intent.putExtra(MyConstants.GROUP_ID, mGroup.getGroupId());

            startActivityForResult(intent, REQUEST_CODE);
        }

        // 删除群成员方法
        @Override
        public void onDeleteMember(final MyUserInfo user) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    // 从环信服务器中删除此人
                    try {
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(),user.getHxid());

                        // 更新页面
                        getMembersFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode == RESULT_OK) {
            // 获取返回的准备邀请的群成员信息
            final String[] memberses = data.getStringArrayExtra("members");

            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    // 去环信服务器，发送邀请信息
                    try {
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(),memberses);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (final HyphenateException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();

        getData();

        initData();

        initListener();
    }

    private void initListener() {
        gvGroupdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        // 判断当前是否是删除模式,如果是删除模式
                        if(adapter.ismIsDeleteModel()) {
                            // 切换为非删除模式
                            adapter.setmIsDeleteModel(false);
                            // 刷新页面
                            adapter.notifyDataSetChanged();
                        }
                
                        break;
                }
                return false;
            }
        });

    }

    private void initData() {
        // 初始化button显示
        initButtonDisplay();

        // 初始化gridview
        initGridview();

        // 从环信服务器获取所有的群成员
        getMembersFromHxServer();

    }

    private void getMembersFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                // 从环信服务器获取所有的群成员信息
                try {
                    EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());
                    List<String> members = emGroup.getMembers();

                    if(members != null) {
                        mUsers = new ArrayList<>();

                        // 转换
                        for (String member : members) {
                            MyUserInfo  myUserInfo = new MyUserInfo(member);
                            mUsers.add(myUserInfo);
                        }
                    }
                    // 更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 刷新适配器
                            adapter.refresh(mUsers);
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群信息失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void initGridview() {
        // 当前用户是群组 || 群公开了
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();

        //设置gvGroupdetail的适配器
        adapter = new GroupDetailAdapter(this,isCanModify,mOnGroupDetailListener);

        gvGroupdetail.setAdapter(adapter);

    }

    private void initButtonDisplay() {
        // 判断当前用户是否是群主
        if(EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {//是群主
            btGroupdetailOut.setText("解散群");
            btGroupdetailOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());

                                // 发送退群的广播
                                exitGroupBroatCast();

                                // 更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();

                                        // 结束当前页面
                                        finish();
                                    }
                                });

                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                    });
                }
            });

        } else {// 群成员
            btGroupdetailOut.setText("退群");
            btGroupdetailOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        // 告诉环信服务器退群
                        EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());

                        // 发送退群广播
                        exitGroupBroatCast();

                        // 更新页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();

                                finish();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "退群失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    // 发送退群和解散群广播
    private void exitGroupBroatCast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(MyConstants.EXIT_GROUP);
        intent.putExtra(MyConstants.GROUP_ID,mGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    private void initView() {
        ButterKnife.bind(this);
    }

    // 获取传递过来的数据
    private void getData() {
//        Intent intent = new Intent();//不能用new 只能getIntent()
        Intent intent = getIntent();

        String groupId = intent.getStringExtra(MyConstants.GROUP_ID);

        LogUtil.e("groupId======>" + groupId);

        if (groupId == null) {
            return;
        } else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);

            LogUtil.e("mGroup=========>" + mGroup);
        }
    }
}
