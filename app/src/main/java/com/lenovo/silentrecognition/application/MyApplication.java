package com.lenovo.silentrecognition.application;

import android.app.Application;

import com.lenovo.silentrecognition.jni.RecognitionNative;

/**
 * Created by mary on 2017/3/2.
 */

public class MyApplication extends Application {
    /*static {
        System.loadLibrary("smart_kit");
    }*/
    @Override
    public void onCreate() {
        super.onCreate();
//        RecognitionNative.jniFaceInit();
    }

}

