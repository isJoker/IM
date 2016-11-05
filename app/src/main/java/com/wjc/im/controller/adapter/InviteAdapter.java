package com.wjc.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wjc.im.R;
import com.wjc.im.modul.bean.InvitationInfo;
import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ${万嘉诚} on 2016/11/4.
 * WeChat：wjc398556712
 * Function：邀请信息列表页面的适配器
 */

public class InviteAdapter extends BaseAdapter {

    private Context mContext;
    List<InvitationInfo> mInvationInfos = new ArrayList<>();

    private OninviteListener mOninviteListener;
    private InvitationInfo info;

    public InviteAdapter(Context mContext ,OninviteListener oninviteListener) {
        this.mContext = mContext;
        mOninviteListener = oninviteListener;
    }

    // 刷新数据的方法
    public void refresh(List<InvitationInfo> invationInfos){
        if(invationInfos != null && invationInfos.size() >= 0) {
            mInvationInfos.clear();
            LogUtil.e("mInvationInfos----------clear" + mInvationInfos.size());
            mInvationInfos.addAll(invationInfos);
            LogUtil.e("mInvationInfos----------addAll" + mInvationInfos.size());
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {
        return mInvationInfos == null ? 0 : mInvationInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mInvationInfos == null ? null : mInvationInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // 1 获取或创建viewhodler
        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = View.inflate(mContext,R.layout.item_invitationinfo,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 2 获取当前item数据
        info = mInvationInfos.get(position);

        // 3 设置显示数据
        MyUserInfo user = info.getUser();
        
        if(user == null) {// 群邀请信息





            
        } else {// 好友邀请
            viewHolder.tv_invite_name.setText(user.getName());

            // 控制接受按钮和拒绝按钮的隐藏和显示
            viewHolder.btn_decline.setVisibility(View.GONE);
            viewHolder.btn_receive.setVisibility(View.GONE);


            if (InvitationInfo.InvitationStatus.NEW_INVITE == info.getStatus()) {// 当前有新邀请
                if(info.getReason() == null) {
                    viewHolder.tv_invite_reason.setText("请求添加好友");
                } else {
                    viewHolder.tv_invite_reason.setText(info.getReason());
                }

                viewHolder.btn_decline.setVisibility(View.VISIBLE);
                viewHolder.btn_receive.setVisibility(View.VISIBLE);

            } else if (InvitationInfo.InvitationStatus.INVITE_ACCEPT == info.getStatus()) {// 接受邀请
                if(info.getReason() == null) {
                    viewHolder.tv_invite_reason.setText("接受邀请");
                } else {
                    viewHolder.tv_invite_reason.setText(info.getReason());
                }

            } else if (InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER == info.getStatus()) {// 邀请被接受
                if(info.getReason() == null) {
                    viewHolder.tv_invite_reason.setText("邀请被接受");
                } else {
                    viewHolder.tv_invite_reason.setText(info.getReason());
                }
            }

            // 拒绝按钮的点击事件处理
            viewHolder.btn_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOninviteListener.onReject(info);
                }
            });
            // 接受按钮的点击事件处理
            viewHolder.btn_receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOninviteListener.onAccept(info);
                }
            });


        }
        // 4 返回View
        return convertView;
    }

    class ViewHolder{

        @Bind(R.id.tv_invite_name)
        TextView tv_invite_name;
        @Bind(R.id.tv_invite_reason)
        TextView tv_invite_reason;
        @Bind(R.id.btn_receive)
        Button btn_receive;
        @Bind(R.id.btn_decline)
        Button btn_decline;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }

    public interface OninviteListener{
        // 联系人接受按钮的点击事件
        void onAccept(InvitationInfo invitationInfo);

        // 联系人拒绝按钮的点击事件
        void onReject(InvitationInfo invitationInfo);
    }
}
