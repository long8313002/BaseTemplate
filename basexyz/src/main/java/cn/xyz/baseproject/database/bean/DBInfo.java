package cn.xyz.baseproject.database.bean;

/**
 * 数据库信息
 * Created by zhangzheng on 2016/8/10.
 */
public class DBInfo {
    private int verson;//数据库版本
    private String dbName;//数据库名称

    public DBInfo(int verson, String dbName) {
        this.verson = verson;
        this.dbName = dbName;
    }

    public DBInfo(){}

    public int getVerson() {
        return verson;
    }

    public void setVerson(int verson) {
        this.verson = verson;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
