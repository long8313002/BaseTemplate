package cn.xyz.testAndroid;

import cn.xyz.baseproject.BaseXYZApplication;
import cn.xyz.baseproject.database.bean.DBInfo;

/**
 * Created by zhangzheng on 2016/8/8.
 */
public class XYZApplication extends BaseXYZApplication {
    @Override
    public DBInfo getDBInfo() {
        return new DBInfo(1,"chat.db");
    }
}
