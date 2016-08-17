package cn.xyz.baseproject.database.factory;

import android.content.ContentValues;

/**
 * Created by zhangzheng on 2016/8/10.
 */
public interface InsetFactory<T> {

    public ContentValues parseContentValues(T bean,ContentValues values);
}
