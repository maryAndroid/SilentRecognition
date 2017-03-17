package com.lenovo.silentrecognition.jni;



import com.lenovo.silentrecognition.common.GlobalConfig;
import com.lenovo.silentrecognition.data.bean.PersonInfo;
import com.lenovo.silentrecognition.data.db.PersonInfoDao;
import com.lenovo.silentrecognition.utils.LogUtil;

import java.nio.ByteBuffer;

import de.greenrobot.event.EventBus;

/**
 * Created by mary on 2016/11/4.
 */
public class RecognitionNative {
    static {
        System.loadLibrary("caffe");
        System.loadLibrary("caffe_jni");
        LogUtil.i("load Recognition library");
    }

    public static native int jniFaceInit();

    /**
     *  Recognition 算法(新)
     * @param width
     * @param height
     * @param buffer  byte[ 112 * 96 ]
     * @return
     */
    public static native int jniFaceDetectRefine(int width , int height , byte[] buffer);

}
