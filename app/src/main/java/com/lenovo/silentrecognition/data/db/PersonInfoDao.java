package com.lenovo.silentrecognition.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lenovo.silentrecognition.common.GlobalConfig;
import com.lenovo.silentrecognition.data.bean.PersonInfo;

import java.util.List;

/**
 * Created by mary on 2016/11/7.
 */
public class PersonInfoDao {
    private Context context;
    private MyDBHelper helper;
    private SQLiteDatabase db;
    private static PersonInfoDao personInfoDao;

    public PersonInfoDao(Context context){
        this.context = context;
        helper = MyDBHelper.getInstance(context);
    }

    public static PersonInfoDao getInstance(Context context){
        synchronized (PersonInfoDao.class){
            if(null == personInfoDao){
                    personInfoDao = new PersonInfoDao(context);
            }
            return personInfoDao;
        }
    }

    /**
     * 增加PersonInfo
     * @param personInfo
     * @return
     */
    public boolean addPersonInfo(PersonInfo personInfo){
        synchronized (helper){
            db = helper.getWritableDatabase();
            Cursor cursor = db.query(GlobalConfig.TABLE_PERSON_INFO,new String[]{"id"},"recognitionId=?",new String[]{String.valueOf(personInfo.getRecognitionId())},null,null,null);
            if(!cursor.moveToNext()){
                db.execSQL("insert into "+GlobalConfig.TABLE_PERSON_INFO+" (recognitionId,name,imagePath,voicePath) values(?,?,?,?)",
                        new Object[]{personInfo.getRecognitionId(),personInfo.getName(),personInfo.getImagePath(),personInfo.getVoicePath()});
            }
            closeDateBase(db);
            cursor.close();
        }
        return true;
    }

    /**
     * 通过recognitionId获取PersonInfo
     * @param recognitionId
     * @return
     */
    public PersonInfo findPersonInfoByRecId(int recognitionId){
        synchronized (helper){
            PersonInfo personInfo = null;
            db = helper.getReadableDatabase();
            if(db.isOpen()){
                Cursor cursor = db.query(GlobalConfig.TABLE_PERSON_INFO,new String[]{"name", "imagePath","voicePath"},
                        "recognitionId=?",new String[]{String.valueOf(recognitionId)},null,null,null);
                if(cursor.moveToNext()){
                    personInfo = new PersonInfo();
                    personInfo.setRecognitionId(recognitionId);
                    personInfo.setName(cursor.getString(0));
                    personInfo.setImagePath(cursor.getString(1));
                    personInfo.setVoicePath(cursor.getString(2));
                }
                cursor.close();
            }
            closeDateBase(db);
            return personInfo;
        }
    }

    public List<PersonInfo> findAllPersonInfo(List<PersonInfo> personInfos){
        personInfos.clear();
        synchronized (helper){
            db = helper.getReadableDatabase();
            Cursor cursor = db.query(GlobalConfig.TABLE_PERSON_INFO,new String[]{"recognitionId", "name", "imagePath","voicePath"},null,null,null,null,"id desc");
            while (cursor.moveToNext()){
                PersonInfo personInfo = new PersonInfo();
                personInfo.setRecognitionId(cursor.getInt(0));
                personInfo.setName(cursor.getString(1));
                personInfo.setImagePath(cursor.getString(2));
                personInfo.setVoicePath(cursor.getString(3));
                personInfos.add(personInfo);
            }
            closeDateBase(db);
            cursor.close();
            return personInfos;
        }
    }
    public boolean updatePersonInfo(PersonInfo personInfo){
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen() && null != personInfo) {
            ContentValues values = new ContentValues();
            values.put("recognitionId",personInfo.getRecognitionId());
            values.put("name",personInfo.getName());
            values.put("imagePath",personInfo.getImagePath());
            values.put("voicePath", personInfo.getVoicePath());
            db.update(GlobalConfig.TABLE_PERSON_INFO, values,
                    "recognitionId=?", new String[]{String.valueOf(personInfo.getRecognitionId())});
            db.close();
            return true;
        }
        return false;
    }

    public void deletePersonInfoByRecId(int recognitionId){
        synchronized (helper){
            db = helper.getWritableDatabase();
            db.delete(GlobalConfig.TABLE_PERSON_INFO,"recognitionId=?",new String[]{String.valueOf(recognitionId)});
        }
        closeDateBase(db);
    }

    public boolean checkPersonExist(int recognitionId){
        synchronized (helper){
            db = helper.getWritableDatabase();
            Cursor cursor = db.query(GlobalConfig.TABLE_PERSON_INFO,null,"recognitionId=?",new String[]{String.valueOf(recognitionId)},null,null,null);
            if(cursor.moveToNext()){
                cursor.close();
                return true;
            }
            cursor.close();
            return false;
        }
    }

    public void closeDateBase(SQLiteDatabase db){
        if(db!=null){
            db.close();
        }
    }
}
