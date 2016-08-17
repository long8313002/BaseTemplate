package com.j256.ormlite.stmt.mapped;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;

/**
 * A mapped statement for deleting an object.
 *
 * @author graywatson
 */
public class MappedDelete<T, ID> extends BaseMappedStatement<T, ID> {

    private MappedDelete(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes) {
        super(tableInfo, statement, argFieldTypes);
    }

    public static <T, ID> MappedDelete<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo)
            throws SQLException {
        FieldType idField = tableInfo.getIdField();
        if (idField == null) {
            throw new SQLException("Cannot delete from " + tableInfo.getDataClass()
                    + " because it doesn't have an id field");
        }
        StringBuilder sb = new StringBuilder(64);
        appendTableName(databaseType, sb, "DELETE FROM ", tableInfo.getTableName());
        List<FieldType> fieldTypes = new ArrayList<>();
        fieldTypes.addAll(Arrays.asList(tableInfo.getFieldTypes()));
        appendWhereFieldEq(databaseType, idField, sb, fieldTypes);
        return new MappedDelete<T, ID>(tableInfo, sb.toString(), tableInfo.getFieldTypes());
    }

    /**
     * 该方法被重构
     * Delete the object from the database.
     */
    public int delete(DatabaseConnection databaseConnection, T data, ObjectCache objectCache) throws SQLException {
        try {
            Object[] args = getDeleteFieldObjects(data);
            List<String> emptyDataName = getEmptyDataName(data);
            String statement = this.statement;
            List<FieldType> fieldTypes = new ArrayList<>();
            fieldTypes.addAll(Arrays.asList(argFieldTypes));
            for (String emptyName : emptyDataName) {
                statement = statement.replace("`" + emptyName + "`" + " = ? AND ", "");
                statement = statement.replace("`" + emptyName + "`" + " = ? ", "");
                for (FieldType type : argFieldTypes) {
                    if (type.getColumnName().equals(emptyName)) {
                        fieldTypes.remove(type);
                    }
                }
            }
            if (isNeedDeleteAll(data)) {
                String tableName = argFieldTypes[0].getTableName();
                statement = "DELETE FROM " + "'" + tableName + "'";
                args = new Object[0];
            }
            int rowC = databaseConnection.delete(statement, args, fieldTypes.toArray(new FieldType[fieldTypes.size()]));
            logger.debug("delete data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
            if (args.length > 0) {
                // need to do the (Object) cast to force args to be a single object
                logger.trace("delete arguments: {}", (Object) args);
            }
            if (rowC > 0 && objectCache != null) {
                Object id = idField.extractJavaFieldToSqlArgValue(data);
                objectCache.remove(clazz, id);
            }
            return rowC;
        } catch (SQLException e) {
            throw SqlExceptionUtil.create("Unable to run delete stmt on object " + data + ": " + statement, e);
        }
    }

    protected Object[] getDeleteFieldObjects(Object data) throws SQLException {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < argFieldTypes.length; i++) {
            FieldType fieldType = argFieldTypes[i];
            if (idFieldIsEmpty(data, fieldType)) {
                continue;
            }
            if (fieldType.isAllowGeneratedIdInsert()) {
                Object va = fieldType.getFieldValueIfNotDefault(data);
                if (va != null) {
                    values.add(va);
                }
            } else {
                Object va = fieldType.extractJavaFieldToSqlArgValue(data);
                if (va != null) {
                    values.add(va);
                }
            }
        }

        return values.toArray();
    }

    private boolean isNeedDeleteAll(T data) {
        if (argFieldTypes.length == 1) {
            if (idFieldIsEmpty(data, argFieldTypes[0])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Delete the object from the database.
     */
    public int deleteById(DatabaseConnection databaseConnection, ID id, ObjectCache objectCache) throws SQLException {
        try {
            Object[] args = new Object[]{convertIdToFieldObject(id)};
            int rowC = databaseConnection.delete(statement, args, argFieldTypes);
            logger.debug("delete data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
            if (args.length > 0) {
                // need to do the (Object) cast to force args to be a single object
                logger.trace("delete arguments: {}", (Object) args);
            }
            if (rowC > 0 && objectCache != null) {
                objectCache.remove(clazz, id);
            }
            return rowC;
        } catch (SQLException e) {
            throw SqlExceptionUtil.create("Unable to run deleteById stmt on id " + id + ": " + statement, e);
        }
    }
}
