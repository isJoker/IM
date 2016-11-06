package com.wjc.im.controller.activity;

import android.app.Activity;
import android.os.Bundle;
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
