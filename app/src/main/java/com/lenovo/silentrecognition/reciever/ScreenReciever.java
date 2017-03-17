package com.lenovo.silentrecognition.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.lenovo.silentrecognition.common.GlobalConfig;
import com.lenovo.silentrecognition.event.MessageEvent;
import com.lenovo.silentrecognition.utils.LogUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by mary on 2017/3/1.
 */

public class ScreenReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.intent.action.SCREEN_ON")) {
            LogUtil.e("—— SCREEN_ON ——");
            EventBus.getDefault().post(new MessageEvent(GlobalConfig.MSG_RECIEVER_SCREEN_ON));
        } else if (action.equals("android.intent.action.SCREEN_OFF")) {
            LogUtil.e("—— SCREEN_OFF ——");
            EventBus.getDefault().post(new MessageEvent(GlobalConfig.MSG_RECIEVER_SCREEN_OFF));
        }
    }
}
