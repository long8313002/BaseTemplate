package cn.xyz.testAndroid;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import cn.xyz.baseproject.database.DatabaseHelper;
import cn.xyz.baseproject.database.bean.BaseDbBean;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testfastDBInsertTransaction();
        testDBInsertTransactionSql();
    }

    private void testDBInsert() {
        RecodeTimeManager.init("插入一条数据用时");
        UserDB user = new UserDB();
        user.setUserName("zhanzheng");
        user.setPassword("123456");
        user.insertNoException();
        RecodeTimeManager.print();
    }

    private void testDBInsertTransaction() {
        RecodeTimeManager.init("插入一万条数据用时");
        UserDB user = new UserDB();
        user.openTransaction();
        for (int i = 0; i < 10000; i++) {
            user.setUserName("zhanzheng" + i);
            user.setPassword("123456" + i);
            user.insertNoException();
        }
        user.commitTransaction();
        RecodeTimeManager.print();
    }

    private void testfastDBInsertTransaction() {
        RecodeTimeManager.init("插入一万条数据用时");
        UserDB user = new UserDB();
        user.openTransaction();
        for (int i = 0; i < 10000; i++) {
            user.setUserName("zhanzheng" + i);
            user.setPassword("123456" + i);
            user.fastInsert();
        }
        user.commitTransaction();
        RecodeTimeManager.print();
    }

    private void testDBInsertTransactionSql() {
        RecodeTimeManager.init("插入一万条数据用时");
        SQLiteDatabase db = DatabaseHelper.getSQLiteDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (int i = 0; i < 10000; i++) {
            values.clear();
            values.put("userName","zzzz");
            values.put("password","ppppp");
            db.insert("user",null,values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        RecodeTimeManager.print();
    }

    private void testDBInsertTransaction3() {
        RecodeTimeManager.init("插入一万条数据用时");
        RecodeDB user = new RecodeDB();
        user.openTransaction();
        for (int i = 0; i < 10000; i++) {
            user.setDate("zhanzheng" + i);
            user.setContent("123456" + i);
            UserDB userDB = new UserDB();
            userDB.setUserId(i);
            user.setUserID(userDB);
            user.insertNoException();
        }
        user.commitTransaction();
        RecodeTimeManager.print();
    }

    private void testDBInsertTransaction2() throws Exception {
        RecodeTimeManager.init("插入一万条数据用时");
        Dao<BaseDbBean, Integer> dbDao = DatabaseHelper.getDbDao(UserDB.class);
        TransactionManager.callInTransaction(dbDao.getConnectionSource(), new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                UserDB user = new UserDB();
                for (int i = 0; i < 10000; i++) {
                    user.setUserName("zhanzheng");
                    user.setPassword("123456");
                    user.insertNoException();
                }
                RecodeTimeManager.print();
                return null;
            }
        });
    }

    private void selectAll() {
        RecodeTimeManager.init("查询所有数据用时");
        UserDB userDB = new UserDB();
        List list = userDB.queryNoException();
        RecodeTimeManager.print();
        RecodeTimeManager.print("查询出：" + list.size());
    }

    private void select(){
        RecodeTimeManager.init("查询所有数据用时");
        UserDB userDB = new UserDB();
        userDB.setUserName("@{zhanzheng11||zhanzheng22}");
        userDB.setPassword("@{? like 123456%}");
        List list = userDB.queryNoException();
        RecodeTimeManager.print();
        RecodeTimeManager.print("查询出：" + list.size());
    }

    private void selectSQL() {
        RecodeTimeManager.init("查询所有数据用时");
        UserDB userDB = new UserDB();
        List list = userDB.querySqlNoException("userName = %s", "zhanzheng");
        RecodeTimeManager.print();
        RecodeTimeManager.print(list.size() + "");
    }

    private void deleteAll() {
        RecodeTimeManager.init("删除数据用时");
        new UserDB().deleteNoException();
        RecodeTimeManager.print();
    }

    private void delete() {
        RecodeTimeManager.init("删除数据用时");
        UserDB userDB = new UserDB();
        userDB.setUserName("@{zhanzheng2||zhanzheng3}");
        userDB.deleteNoException();
        RecodeTimeManager.print();
    }

    private void update() {
        RecodeTimeManager.init("更新数据用时");
        UserDB userDb = new UserDB();
        userDb.setUserName("@{zhanzheng4||zhanzheng5}");

        UserDB updateDb = new UserDB();
        updateDb.setPassword("ZZZZZZZZZZZZZZZZZZ");

        int i = userDb.updateNoException(updateDb);
        RecodeTimeManager.print();
        RecodeTimeManager.print("更新成功:" + i);
    }

    private void otherExecuteSql() {
        try {
            DatabaseHelper.getDbDao(UserDB.class).executeRaw("delete from user where userID=20");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void otherExecuteQuery() {
        RecodeTimeManager.init("查询数据用时");
        Cursor cursor = DatabaseHelper.getSQLiteDatabase().rawQuery("select * from user ,recode where user.userId = recode.userID", null);
        List<UserDB> passwords = new ArrayList<>();
        cursor.move(-1);
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndex("password");
            String password =cursor.getString(index);
            UserDB userDB = new UserDB();
            userDB.setPassword(password);
            passwords.add(userDB);
        }
        cursor.close();
        RecodeTimeManager.print();
        RecodeTimeManager.print("查询出："+passwords.size());
    }
}
