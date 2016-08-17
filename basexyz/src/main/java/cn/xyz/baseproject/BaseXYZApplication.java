package cn.xyz.baseproject;

import android.app.Application;

import cn.xyz.baseproject.database.bean.DBInfo;

/**
 * Created by zhangzheng on 2016/8/8.
 */
public abstract class BaseXYZApplication extends Application {

    private static BaseXYZApplication application;

    public static BaseXYZApplication get(){
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BaseXYZApplication.application = this;
    }

    /**
     * 定义数据库信息
     * @return 数据库信息
     */
    public abstract DBInfo getDBInfo();
}
