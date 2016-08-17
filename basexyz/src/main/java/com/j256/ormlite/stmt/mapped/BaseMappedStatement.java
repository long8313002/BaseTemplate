package com.j256.ormlite.stmt.mapped;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.table.TableInfo;

/**
 * Abstract mapped statement which has common statements used by the subclasses.
 * 
 * @author graywatson
 */
public abstract class BaseMappedStatement<T, ID> {

	protected static Logger logger = LoggerFactory.getLogger(BaseMappedStatement.class);

	protected final TableInfo<T, ID> tableInfo;
	protected final Class<T> clazz;
	protected final FieldType idField;
	protected final String statement;
	protected final FieldType[] argFieldTypes;

	protected BaseMappedStatement(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes) {
		this.tableInfo = tableInfo;
		this.clazz = tableInfo.getDataClass();
		this.idField = tableInfo.getIdField();
		this.statement = statement;
		this.argFieldTypes = argFieldTypes;
	}

	public TableInfo<T, ID> getTableInfo() {
		return tableInfo;
	}

	/**
	 * Return the array of field objects pulled from the data object.
	 */
	protected Object[] getFieldObjects(Object data) throws SQLException {
		Object[] objects = new Object[argFieldTypes.length];
		for (int i = 0; i < argFieldTypes.length; i++) {
			FieldType fieldType = argFieldTypes[i];
			if (fieldType.isAllowGeneratedIdInsert()) {
				objects[i] = fieldType.getFieldValueIfNotDefault(data);
			} else {
				objects[i] = fieldType.extractJavaFieldToSqlArgValue(data);
			}
			if (objects[i] == null) {
				// NOTE: the default value could be null as well
				objects[i] = fieldType.getDefaultValue();
			}
		}
		return objects;
	}

	protected List<String> getEmptyDataName(Object data) throws SQLException{
		List<String> values = new ArrayList<>();
		for (int i = 0; i < argFieldTypes.length; i++) {
			FieldType fieldType = argFieldTypes[i];
			Object va =null;
			if (fieldType.isAllowGeneratedIdInsert()) {
				 va = fieldType.getFieldValueIfNotDefault(data);
			} else {
				 va = fieldType.extractJavaFieldToSqlArgValue(data);
			}
			va =idFieldIsEmpty(data,fieldType)?null:va;
			if(va==null){
				values.add(fieldType.getColumnName());
			}
		}
		return values;
	}

	protected boolean idFieldIsEmpty(Object data,FieldType fieldType){
		if(fieldType.isGeneratedId()){
			try {
				Object va = fieldType.getField().get(data);
				if(va instanceof Integer&&(Integer)va==0){
					return true;
				}
			} catch (IllegalAccessException ignore) {
			}
		}
		return false;
	}

	/**
	 * Return a field object converted from an id.
	 */
	protected Object convertIdToFieldObject(ID id) throws SQLException {
		return idField.convertJavaFieldToSqlArgValue(id);
	}

	private static final String AND =" AND ";
	static void appendWhereFieldEq(DatabaseType databaseType, FieldType fieldType, StringBuilder sb,
			List<FieldType> fieldTypeList) {
		sb.append("WHERE ");
		for (FieldType type:fieldTypeList) {
			appendFieldColumnName(databaseType, sb, type,fieldTypeList);
			sb.append("= ?");
			sb.append(AND);
		}
		sb.delete(sb.length()-AND.length(),sb.length());
	}

	static void appendTableName(DatabaseType databaseType, StringBuilder sb, String prefix, String tableName) {
		if (prefix != null) {
			sb.append(prefix);
		}
		databaseType.appendEscapedEntityName(sb, tableName);
		sb.append(' ');
	}

	static void appendFieldColumnName(DatabaseType databaseType, StringBuilder sb, FieldType fieldType,List<FieldType> fieldTypeList) {
		databaseType.appendEscapedEntityName(sb, fieldType.getColumnName());
		sb.append(' ');
	}

	@Override
	public String toString() {
		return "MappedStatement: " + statement;
	}
}
