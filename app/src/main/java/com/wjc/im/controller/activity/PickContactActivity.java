package com.wjc.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.wjc.im.R;
import com.wjc.im.controller.adapter.PickContactAdapter;
import com.wjc.im.modul.Model;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.modul.bean.PickContactInfo;
import com.wjc.im.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${万嘉诚} on 2016/11/6.
 * WeChat：wjc398556712
 * Function：选择联系人界面
 */
public class PickContactActivity extends Activity {


    @Bind(R.id.tv_pick_save)
    TextView tvPickSave;
    @Bind(R.id.lv_pick)
    ListView lvPick;
    private List<String> mExistMembers;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iniView();

        initData();

        initListener();
    }

    private void initListener() {
        // listview条目点击事件
        lvPick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // checkbox的切换
                CheckBox cb_pick = (CheckBox) view.findViewById(R.id.cb_pick);
                cb_pick.setChecked(!cb_pick.isChecked());

                // 修改数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setIsChecked(cb_pick.isChecked());

                // 刷新页面
                adapter.notifyDataSetChanged();
            }
        });

        // 保存按钮的点击事件
        tvPickSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取到已经选择的联系人
                List<String> names = adapter.getPickContacts();
                // 给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members",names.toArray(new String[0]));

                // 设置返回的结果码
                setResult(RESULT_OK,intent);

                // 结束当前页面
                finish();
            }
        });

    }

    private void initData() {
        // 获取传递过来的数据
        getData();

        // 从本地数据库中获取所有的联系人信息
        List<MyUserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();
        mPicks = new ArrayList<>();
        if (contacts != null && contacts.size() >= 0) {
            // 转换
            for (MyUserInfo contact : contacts) {
                PickContactInfo pickContactInfo = new PickContactInfo(contact, false);
                mPicks.add(pickContactInfo);
            }

        }
        // 给listview设置适配器
        adapter = new PickContactAdapter(this,mPicks,mExistMembers);
        lvPick.setAdapter(adapter);

    }

    private void getData() {
        String groupId = getIntent().getStringExtra(MyConstants.GROUP_ID);

        if (mExistMembers == null) {
            mExistMembers = new ArrayList<>();
        }

        if(groupId != null) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            // 获取群中已经存在的所有群成员
            mExistMembers = group.getMembers();
        }
    }

    private void iniView() {
        setContentView(R.layout.activity_pick_contact);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
