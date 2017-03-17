package com.lenovo.silentrecognition.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lenovo.silentrecognition.common.GlobalConfig;

/**
 * Created by mary on 2016/11/7.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    private static MyDBHelper helper;

    public MyDBHelper(Context context) {
        super(context, GlobalConfig.DB_NAME, null, GlobalConfig.DB_VERSION);
    }

    public static MyDBHelper getInstance(Context context){
        synchronized (MyDBHelper.class) {
            if (null == helper) {
                helper = new MyDBHelper(context);
            }
        }
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建personInfo表
        db.execSQL(GlobalConfig.CREATE_TABLE_PERSON_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
