package com.wjc.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wjc.im.R;
import com.wjc.im.modul.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/11/6.
 * WeChat：wjc398556712
 * Function：
 */

public class PickContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();
    private List<String> mExistMembers = new ArrayList<>();// 保存群中已经存在的成员集合

    public PickContactAdapter(Context context, List<PickContactInfo> picks, List<String> existMembers) {
        this.mContext = context;
        if(picks != null ) {
            mPicks.clear();
            mPicks.addAll(picks);
        }

        // 加载已经存在的成员集合
        if(existMembers != null) {
            mExistMembers.clear();
            mExistMembers.addAll(existMembers);
        }

    }

    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int i) {
        return mPicks == null ? null : mPicks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // 创建或获取viewHolder
        ViewHolder holder  = null;

        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_pick, null);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取当前item数据
        PickContactInfo pickContactInfo = mPicks.get(position);

        // 显示数据
        holder.tv_name.setText(pickContactInfo.getUser().getName());
        holder.cb.setChecked(pickContactInfo.isChecked());

        // 判断
        if(mExistMembers.contains(pickContactInfo.getUser().getHxid())) {
            holder.cb.setChecked(true);
            pickContactInfo.setIsChecked(true);
        }

        return convertView;
    }

    private class  ViewHolder{
        private CheckBox cb;
        private TextView tv_name;

        public ViewHolder(View view) {

            cb = (CheckBox) view.findViewById(R.id.cb_pick);
            tv_name = (TextView) view.findViewById(R.id.tv_pick_name);
        }
    }
}
