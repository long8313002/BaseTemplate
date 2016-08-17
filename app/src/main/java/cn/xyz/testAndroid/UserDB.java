package cn.xyz.testAndroid;

import android.content.ContentValues;
import android.database.Cursor;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.lang.reflect.Constructor;
import java.sql.SQLException;

import cn.xyz.baseproject.database.bean.BaseDbBean;

/**
 * Created by zhangzheng on 2016/8/8.
 */
@DatabaseTable(tableName = "user")
public class UserDB extends BaseDbBean<UserDB> {

    @DatabaseField(generatedId = true,columnName = "userID")
    private int userId;
    @DatabaseField(columnName = "userName")
    private String userName;
    @DatabaseField(columnName = "password")
    private String password;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Object createObject(Constructor construcor, Class dataClass) throws SQLException {
        return new UserDB();
    }

    @Override
    public UserDB fillDataBuCursor(Cursor cursor) {
        int index = cursor.getColumnIndex("userID");
        this.userId =cursor.getInt(index);
        index = cursor.getColumnIndex("userName");
        this.userName = cursor.getString(index);
        index = cursor.getColumnIndex("password");
        this.password = cursor.getString(index);
        return this;
    }

    @Override
    public ContentValues parseContentValues(UserDB bean,ContentValues values) {
        values.put("userName",userName);
        values.put("password",password);
        return values;
    }
}
