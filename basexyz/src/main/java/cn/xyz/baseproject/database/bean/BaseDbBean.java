package cn.xyz.baseproject.database.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.ObjectFactory;
import com.j256.ormlite.table.TableInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;

import cn.xyz.baseproject.database.DatabaseHelper;
import cn.xyz.baseproject.database.factory.InsetFactory;

/**
 * Created by zhangzheng on 2016/8/3.
 */
public class BaseDbBean<T extends BaseDbBean> extends BaseDaoEnabled<T, Integer> implements ObjectFactory, InsetFactory<T> {

    private Savepoint savepoint;
    private OrderByType orderbyType;
    private ContentValues contentValues;

    public BaseDbBean() {
        setDao((Dao<T, Integer>) DatabaseHelper.getDbDao(getClass()));
    }

    public void setOrderbyType(OrderByType orderbyType) {
        this.orderbyType = orderbyType;
    }

    public OrderByType getOrderbyType() {
        return orderbyType;
    }

    public boolean openTransaction() {

        DatabaseConnection databaseConnection = getDatabaseConnection();
        if (databaseConnection == null) {
            return false;
        }
        try {
            savepoint = databaseConnection.setSavePoint(getTableName());
            return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    public boolean commitTransaction() {
        DatabaseConnection databaseConnection = getDatabaseConnection();
        if (databaseConnection == null || savepoint == null) {
            return false;
        }
        try {
            databaseConnection.commit(savepoint);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getTableName() {
        return getDao().getTableName();
    }

    public DatabaseConnection getDatabaseConnection() {
        try {
            return getDao().getConnectionSource().getReadWriteConnection(getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insertNoException() {
        try {
            return insert();
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public int insert() throws SQLException {
        return super.create();
    }

    public long fastInsert(InsetFactory<T> factory,boolean needClear) {
        SQLiteDatabase db = DatabaseHelper.getSQLiteDatabase();
        if(contentValues == null){
            contentValues = new ContentValues();
        }
        if(needClear){
            contentValues.clear();
        }
        contentValues = factory.parseContentValues((T) this,contentValues);
        return db.insert(getTableName(), null, contentValues);
    }

    public long fastInsert(InsetFactory<T> factory){
        return fastInsert(factory,false);
    }

    public long fastInsert(boolean needClear) {
        return fastInsert(this,needClear);
    }

    public long fastInsert(){
        return fastInsert(this,false);
    }

    public int refreshNoException() {
        try {
            return super.refresh();
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public int updateNoException(T match) {
        try {
            return update(match);
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public int update(T match) throws SQLException {
        return getDao().updateMatch((T) this, match);
    }

    public int updateIdNoException(int newId) {
        try {
            return super.updateId(newId);
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public int deleteNoException() {
        try {
            return delete();
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public int delete() throws SQLException {
        return getDao().deleteMatch((T) this);
    }

    public Object extractIdNoException() {
        try {
            return super.extractId();
        } catch (SQLException ignored) {
        }
        return -1;
    }

    public boolean objectsEqualNoException(T other) {
        try {
            return super.objectsEqual(other);
        } catch (SQLException ignored) {
        }
        return false;
    }

    public TableInfo<T, Integer> getTableInfo() {
        BaseDaoImpl<T, Integer> dao = (BaseDaoImpl<T, Integer>) getDao();
        return dao.getTableInfo();
    }

    public List<T> query() throws SQLException {
        Dao<T, Integer> dao = getDao();
        return dao.queryForMatching((T) this);
    }

    public List<T> queryNoException() {
        try {
            return query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<T> querySql(String where, String... arguments) throws SQLException {
        ArrayList<T> valuesObject = new ArrayList<>();
        TableInfo<T, Integer> tableInfo = getTableInfo();
        if (arguments != null && arguments.length > 0) {
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = "'" + arguments[i] + "'";
            }
            where = String.format(where, arguments);
            arguments = new String[0];
        }
        GenericRawResults<String[]> values = getDao().queryRaw("select * from " + tableInfo.getTableName() + "  where  " + where, arguments);
        List<String[]> results = values.getResults();
        for (String[] sv : results) {
            T instance = createInstenceBySqlResult(sv);
            if (instance == null) {
                continue;
            }
            valuesObject.add(instance);
        }
        return valuesObject;
    }

    protected T createInstenceBySqlResult(String[] results) {
        T object = getTableInfo().createObject();
        if (object == null) {
            return null;
        }
        FieldType[] fieldTypes = getTableInfo().getFieldTypes();
        for (int i = 0; i < results.length; i++) {
            try {
                Field field = fieldTypes[i].getField();
                object.setFileValue(field, results[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    protected void setFileValue(Field field, Object value) throws Exception {
        Class<?> type = field.getType();
        if (value.getClass() == type) {
            field.set(this, value);
            return;
        }
        if (type == int.class || type == Integer.class) {
            value = Integer.valueOf((String) value);
        }
        if (type == Float.class || type == float.class) {
            value = Float.valueOf((String) value);
        }
        if (type == Double.class || type == double.class) {
            value = Double.valueOf((String) value);
        }
        field.set(this, value);

    }

    public List<T> querySqlNoException(String query, String... arguments) {
        try {
            return querySql(query, arguments);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public Object createObject(Constructor construcor, Class dataClass) throws SQLException {
        try {
            return construcor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public T fillDataBuCursor(Cursor cursor) {
        return null;
    }

    @Override
    public ContentValues parseContentValues(T bean, ContentValues contentValues) {
        TableInfo<T, Integer> tableInfo = getTableInfo();
        FieldType[] fieldTypes = tableInfo.getFieldTypes();
        for (FieldType fieldType : fieldTypes) {
            try {
                String columnName = fieldType.getColumnName();
                Object value = fieldType.getFieldValueIfNotDefault(bean);
                if (value == null) {
                    continue;
                }
                contentValuesPutObject(contentValues, columnName, value);
            } catch (SQLException e) {
            }
        }
        return contentValues;
    }

    private void contentValuesPutObject(ContentValues contentValues, String columnName, Object value) {
        if (value instanceof Integer) {
            contentValues.put(columnName, (Integer) value);
        }
        if (value instanceof String) {
            contentValues.put(columnName, (String) value);
        }
        if (value instanceof Double) {
            contentValues.put(columnName, (Double) value);
        }
        if (value instanceof Float) {
            contentValues.put(columnName, (Float) value);
        }
        if (value instanceof Boolean) {
            contentValues.put(columnName, (Boolean) value);
        }
    }

}
