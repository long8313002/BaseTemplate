package cn.xyz.baseproject.database;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;

import cn.xyz.baseproject.BaseXYZApplication;
import cn.xyz.baseproject.database.bean.BaseDbBean;
import cn.xyz.baseproject.database.bean.DBInfo;

/**
 * Created by zhangzheng on 2016/8/3.
 */
public class DatabaseHelper<T extends BaseDbBean> extends OrmLiteSqliteOpenHelper {

    private static final DBInfo dbInfo = BaseXYZApplication.get().getDBInfo();
    private static HashMap<String, DatabaseHelper> tables = new HashMap<>();

    private Class<T> dbBeanClass;
    private Dao<T, Integer> dao;
    private static SQLiteDatabase db;

    private DatabaseHelper(Class<T> dbBeanClass) {
        this();
        this.dbBeanClass = dbBeanClass;
    }

    private DatabaseHelper() {
        super(BaseXYZApplication.get(), dbInfo.getDbName(), null, dbInfo.getVerson());
    }

    public static <T extends BaseDbBean> Dao<T, Integer> getDbDao() {
        return getDbDao(BaseDbBean.class);
    }

    public static SQLiteDatabase getSQLiteDatabase() {
        if (db != null && db.isOpen()) {
            return db;
        }
        return getSQLiteDatabase( new DatabaseHelper());
    }

    public static SQLiteDatabase getSQLiteDatabase(DatabaseHelper help) {
        if (db == null || !db.isOpen()) {
            db = help.getWritableDatabase();
        }
        return db;
    }

    public static <T extends BaseDbBean> Dao<T, Integer> getDbDao(Class<? extends BaseDbBean> tClass) {
        DatabaseHelper help;
        String key = tClass.getSimpleName();
        if (tables.containsKey(key)) {
            help = tables.get(key);
        } else {
            help = new DatabaseHelper<>(tClass);
            help.onCreate(getSQLiteDatabase(help));
            tables.put(key, help);
        }
        return help.getDao();
    }

    private Dao<T, Integer> getDao() {
        if (dao == null) {
            try {
                dao = getDao(dbBeanClass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dao;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        if (dbBeanClass == null) {
            return;
        }
        try {
            TableUtils.createTableIfNotExists(connectionSource, dbBeanClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase
            , ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
