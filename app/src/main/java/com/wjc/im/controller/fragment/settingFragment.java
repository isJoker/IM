package com.wjc.im.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.wjc.im.R;
import com.wjc.im.controller.activity.LoginActivity;
import com.wjc.im.modul.Model;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ${万嘉诚} on 2016/11/2.
 * WeChat：wjc398556712
 * Function：
 */

public class SettingFragment extends Fragment {


    @Bind(R.id.bt_setting_out)
    Button bt_setting_out;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    private void initData() {
        // 在button上显示当前用户名称
        bt_setting_out.setText("退出登录 ( " + EMClient.getInstance().getCurrentUser() + " )");
    }

    @OnClick(R.id.bt_setting_out)
    void bt_setting_out_click(){
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 登录环信服务器退出登录
                EMClient.getInstance().logout(false, new EMCallBack() {//false表示不销毁EMPushHelper的实例
                    @Override
                    public void onSuccess() {
                        // 关闭DBHelper

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();

                                // 回到登录页面
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);

                                getActivity().finish();

                            }
                        });
                    }

                    @Override
                    public void onError(int i, final String s) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "退出失败" + s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
