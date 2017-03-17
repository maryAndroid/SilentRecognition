package com.lenovo.silentrecognition.common;

import android.hardware.Camera;
import android.provider.MediaStore;

import com.lenovo.silentrecognition.utils.FileUtils;


/**
 * Created by kimmy on 2015/4/12.
 */
public class GlobalConfig {
    //file url
    public static String RawImageStoreUrl = FileUtils.genAbsoluteFolderPath("capture1");

    //msg
    public static final String MSG_RECIEVER_SCREEN_ON = "msg_reciever_screen_on";
    public static final String MSG_RECIEVER_SCREEN_OFF = "msg_reciever_screen_off";

    //camera
    public static Camera mCamera;
    public static int CAMERA_WIDTH = 1280;
    public static int CAMERA_HEIGHT = 720;
    public static boolean _isReadToGo = false;

    //database
    public static final String DB_NAME = "pva.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_PERSON_INFO = "personInfo";
    public static final String CREATE_TABLE_PERSON_INFO = "CREATE TABLE IF NOT EXISTS " +TABLE_PERSON_INFO+
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, recognitionId INTEGER,name text, imagePath text,voicePath text);";

    public static int GridviewColumnWidth;
    public static boolean IsImageEditMode;
    public static String ImageExportUrl;

    // pop msg
    public static String getRandomMsg(String name){
        String[] randomMsg = {name+"，下午3点有个会议",name+",明早9点的航班"};
        int random = (int)(Math.random() * (randomMsg.length));
        return randomMsg[random];
    }
}
