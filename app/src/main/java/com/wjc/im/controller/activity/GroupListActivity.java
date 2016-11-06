package com.wjc.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.wjc.im.R;
import com.wjc.im.controller.adapter.GroupListAdapter;
import com.wjc.im.modul.Model;

import java.util.List;


public class GroupListActivity extends Activity {

    private ListView lv_grouplist;
    private LinearLayout ll_grouplist;
    private GroupListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initView();

        initData();

        initListener();
    }

    private void initListener() {
        // listview条目点击事件
        lv_grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(position == 0) {
                    return;
                }

                Intent intent = new Intent(GroupListActivity.this,ChatActivity.class);
                // 传递会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);

                // 群id   减掉头视图所占item的条数
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,emGroup.getGroupId());

                startActivity(intent);

            }
        });

        // 跳转到新建群
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);

                startActivity(intent);
            }
        });

    }

    private void initData() {
        //给listView设置适配器
        adapter = new GroupListAdapter(this);
        lv_grouplist.setAdapter(adapter);

        // 从环信服务器获取所有群的信息
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    //从服务器获取群组列表信息 getJoinedGroupsFromServer()
                    List<EMGroup> groupList = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();

                    //保存到本地数据库(由于我们在本地没有创建数据库群组表，我们这里跳过这步，你可自行添加)

                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();

//                            groupListAdapter.refresh(mGroups);
                            // 刷新
                            refresh();
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    // 刷新
    private void refresh() {
        //getAllGroups()从本地获取群组列表信息
        adapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initView() {
        // 获取listview对象
        lv_grouplist = (ListView) findViewById(R.id.lv_grouplist);

        // 添加头布局
        View headerView = View.inflate(this, R.layout.header_grouplist, null);
        lv_grouplist.addHeaderView(headerView);

        ll_grouplist = (LinearLayout)headerView.findViewById(R.id.ll_grouplist);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 刷新页面
        refresh();
    }
}
