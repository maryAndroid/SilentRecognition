package com.lenovo.silentrecognition.jni;

import android.graphics.Bitmap;

/**
 * Created by mary on 2016/11/3.
 */
public class SmartNative {
    static {
        System.loadLibrary("smart_kit");
    }
    public static native int facesDetectDlib(byte[] data,int width , int height , byte[] pixelBuf);
    public static native int facesDetectDLibBmp(Bitmap bitmapImage , byte[] pixelBuf);

    public static void init(){
    }

}
