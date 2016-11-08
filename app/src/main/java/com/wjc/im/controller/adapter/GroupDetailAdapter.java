package com.wjc.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wjc.im.R;
import com.wjc.im.modul.bean.MyUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/11/8.
 * WeChat：wjc398556712
 * Function：群详情页面适配器
 */

public class GroupDetailAdapter extends BaseAdapter{

    private Context mContext;
    private boolean mIsCanModify;// 是否允许添加和删除群成员
    private List<MyUserInfo> mUserInfos = new ArrayList<>();
    private boolean mIsDeleteModel;// 删除模式  true：表示可以删除； false:表示不可以删除
    private OnGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context mContext, boolean mIsCanModify,OnGroupDetailListener mOnGroupDetailListener) {
        this.mContext = mContext;
        this.mIsCanModify = mIsCanModify;

        this.mOnGroupDetailListener = mOnGroupDetailListener;
    }

    // 获取当前的删除模式
    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }

    // 设置当前的删除模式
    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }

    // 刷新数据
    public void refresh(List<MyUserInfo> userInfos) {
        
        if(userInfos != null) {
            mUserInfos.clear();

            // 添加加号和减号
            initSign();

            mUserInfos.addAll(0,userInfos);

            notifyDataSetChanged();
        }
    }

    private void initSign() {

        MyUserInfo add = new MyUserInfo("add");
        MyUserInfo delete = new MyUserInfo("delete");

        mUserInfos.add(delete);
        mUserInfos.add(0,add);

    }


    @Override
    public int getCount() {
        return mUserInfos != null ? mUserInfos.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mUserInfos != null ? mUserInfos.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {
            view = View.inflate(mContext, R.layout.item_groupdetail,null);

            viewHolder = new ViewHolder(view);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // 获取当前item数据
        final MyUserInfo userInfo = mUserInfos.get(position);

        // 显示数据
        if(mIsCanModify) {// 群主或开放了群权限

            // 布局的处理
            if(position == getCount() - 1) {//减号处理
                // 删除模式判断
                if(mIsDeleteModel) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);

                    viewHolder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                    viewHolder.delete.setVisibility(View.GONE);
                    viewHolder.name.setVisibility(View.INVISIBLE);
                }
            } else if(position == getCount() -2) {//加号处理
                // 删除模式判断
                if(mIsDeleteModel) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);

                    viewHolder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                    viewHolder.delete.setVisibility(View.GONE);
                    viewHolder.name.setVisibility(View.INVISIBLE);
                }
            } else {//普通群成员
                view.setVisibility(View.VISIBLE);
                viewHolder.name.setText(userInfo.getName());
                viewHolder.photo.setImageResource(R.drawable.em_default_avatar);
                
                if(mIsDeleteModel) {
                    viewHolder.delete.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.delete.setVisibility(View.GONE);
                }
            }
            // 点击事件的处理
            if(position == getCount() - 1) {//减号
                viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!mIsDeleteModel) {
                            mIsDeleteModel = true;
                            notifyDataSetChanged();
                        }
                    }
                });

            } else if(position == getCount() - 2) {//加号
                viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });
                
            } else {//普通群成员
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnGroupDetailListener.onDeleteMember(userInfo);
                    }
                });
            }

        } else {// 普通的群成员
            if(position == getCount() - 1 || position == getCount() - 2) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);

                viewHolder.name.setText(userInfo.getName());
                viewHolder.photo.setImageResource(R.drawable.em_default_avatar);
                viewHolder.delete.setVisibility(View.GONE);

            }

        }

        return view;
    }

    private class ViewHolder {
        private ImageView photo;
        private ImageView delete;
        private TextView name;

        public ViewHolder(View view) {
            photo = (ImageView) view.findViewById(R.id.iv_group_detail_photo);
            delete = (ImageView) view.findViewById(R.id.iv_group_detail_delete);
            name = (TextView) view.findViewById(R.id.tv_group_detail_name);
        }
    }

    public interface OnGroupDetailListener{
        // 添加群成员方法
        void onAddMembers();

        // 删除群成员方法
        void onDeleteMember(MyUserInfo user);
    }

}
