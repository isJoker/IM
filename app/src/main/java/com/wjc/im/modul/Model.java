package com.wjc.im.modul;

import android.content.Context;

import com.wjc.im.modul.bean.MyUserInfo;
import com.wjc.im.modul.dao.UserAccountDao;
import com.wjc.im.modul.db.DBManager;
import com.wjc.im.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ${万嘉诚} on 2016/11/1.
 * WeChat：wjc398556712
 * Function：数据模型层全局类
 */

public class Model {
    private Context mContext;
    private ExecutorService executorService = Executors.newCachedThreadPool();//可被垃圾回收

    // 创建对象
    private  static Model model = new Model();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    // 私有化构造
    private Model() {

    }

    // 获取单例对象
    public static Model getInstance(){
        return model;
    }

    // 初始化的方法
    public void init(Context context){
        mContext = context;//全局的上下文对象

        // 创建用户账号数据库的操作类对象
        userAccountDao = new UserAccountDao(mContext);
        // 开启全局监听
//        EventListener listener = new EventListener(mContext);
    }
    // 获取全局线程池对象
    public ExecutorService getGlobalThreadPool(){
        return executorService;
    }

    // 用户登录成功后的处理方法
    public void loginSuccess(MyUserInfo account) {
        // 校验
        if(account == null) {
            return;
        }

        if(dbManager != null) {
            dbManager.close();
        }

        dbManager = new DBManager(mContext, account.getName());

        LogUtil.e("登录成功");
    }

    // 获取用户账号数据库的操作类对象
    public UserAccountDao getUserAccountDao(){
        return userAccountDao;
    }

}
