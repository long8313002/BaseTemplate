package cn.xyz.testAndroid;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import cn.xyz.baseproject.database.bean.BaseDbBean;

/**
 * Created by zhangzheng on 2016/8/10.
 */
@DatabaseTable(tableName = "recode")
public class RecodeDB extends BaseDbBean<RecodeDB> {

    @DatabaseField(generatedId = true,columnName = "recodeID")
    private int recodeID;
    @DatabaseField(columnName = "date")
    private String date;
    @DatabaseField(columnName = "content")
    private String content;
    @DatabaseField(foreign = true,columnName = "userID",canBeNull = false)
    private UserDB userID;

    public int getRecodeID() {
        return recodeID;
    }

    public void setRecodeID(int recodeID) {
        this.recodeID = recodeID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserDB getUserID() {
        return userID;
    }

    public void setUserID(UserDB userID) {
        this.userID = userID;
    }
}
