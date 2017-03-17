package com.lenovo.silentrecognition.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/**
 * Created by mary on 2017/3/1.
 */

public class RecieverUtil {
    public static void registerReciever(Context context, BroadcastReceiver receiver,String action){
        /* 注册屏幕唤醒时的广播 */
        IntentFilter mFilter = new IntentFilter(action);
        context.registerReceiver(receiver, mFilter);
    }
}
