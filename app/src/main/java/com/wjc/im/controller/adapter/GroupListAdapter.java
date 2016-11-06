package com.wjc.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.wjc.im.R;
import com.wjc.im.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/11/6.
 * WeChat：wjc398556712
 * Function：群组列表的适配器
 */

public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EMGroup> emGroups = new ArrayList<>();

    public GroupListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    // 刷新方法、得到群列表数据
    public void refresh(List<EMGroup> groups) {
        if(groups != null && groups.size() >= 0) {
            emGroups.clear();

            emGroups.addAll(groups);

            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return emGroups != null ? emGroups.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return emGroups != null ? emGroups.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        LogUtil.e("convertView---------------------------------->" + convertView);

        if(convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_grouplist,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EMGroup emGroup = emGroups.get(i);

        viewHolder.tv_grouplist_name.setText(emGroup.getGroupName());

        return convertView;
    }

    class ViewHolder{
        TextView tv_grouplist_name;

        public ViewHolder(View view) {
            tv_grouplist_name = (TextView) view.findViewById(R.id.tv_grouplist_name);
        }
    }
}
