package com.lenovo.silentrecognition.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.lenovo.silentrecognition.jni.RecognitionNative;
import com.lenovo.silentrecognition.jni.SmartNative;
import com.lenovo.silentrecognition.ui.MainActivity;

/**
 * Created by mary on 2017/3/9.
 */

public class ScreenControlLService extends Service {
    public static final String TAG = "ScreenControlLService";
    private ScreenControlLService.MyBinder mBinder = new ScreenControlLService.MyBinder();

    private MainActivity activity;

    private boolean bIsOpenActivity = false;
    private SensorManager sm;
    private Sensor ligthSensor;
    private Sensor oriListener;
    private PowerManager.WakeLock mWakelock;
    private KeyguardManager.KeyguardLock mKeyLock;
    private PowerManager.WakeLock mCPUWakeLock;
    PowerManager pm ;
    ScreenControlLService.MySensorListener sensorListener;

    @Override
    public void onCreate() {
        super.onCreate();
        SmartNative.init();
        RecognitionNative.jniFaceInit();
        KeyguardManager km= (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        mKeyLock = km.newKeyguardLock("unLock");
        //解锁
        // mKeyLock.disableKeyguard();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);// init powerManager
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|
                PowerManager.SCREEN_DIM_WAKE_LOCK,"SimpleTimer"); // this target for tell OS which app control screen

        mCPUWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUPersist ");// CPU保存运行

//        获取Sensor对象
        ligthSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        oriListener = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);


        sensorListener =new ScreenControlLService.MySensorListener();
        sm.registerListener(sensorListener, ligthSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(sensorListener, oriListener, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public void disconnected()
        {
            sm.unregisterListener(sensorListener);
            mCPUWakeLock.release();
        }
        public void connected()
        {
            sm.registerListener(sensorListener, ligthSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sm.registerListener(sensorListener, oriListener, SensorManager.SENSOR_DELAY_NORMAL);
        }

        public ScreenControlLService getScreenControlLService()
        {
            return ScreenControlLService.this;
        }

    }

    /**
     *
     * @param activity
     * 初始化MainActivity对象
     */
    public void setMainActivity(MainActivity activity) {
        this.activity=activity;
    }

    //
    public class MySensorListener implements SensorEventListener {
        float[] oSensor = new float[3];
        float[] lSensor = new float[3];
        float lightValue = 400;
//        private List<Integer> lightValueList = new ArrayList<Integer>();
//        private final int LIGHT_SENSOR_LIST_MAX_LENGHT = 5;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public void onSensorChanged(SensorEvent e) {
            int type = e.sensor.getType();
            int avgLightValue = 10000;
            switch (type) {
                case Sensor.TYPE_ACCELEROMETER:
                    break;
                case Sensor.TYPE_ORIENTATION:
                    float deltaOriY = (e.values[1] -  oSensor[1]) * (e.values[1] -  oSensor[1]);
                    oSensor[0] = e.values[0];
                    oSensor[1] = e.values[1];
                    oSensor[2] = e.values[2];

                    if(lightValue < 30
                            && oSensor[1]  < -20
                            && oSensor[1]  > -100){
                        mWakelock.acquire();
                        mWakelock.release();
                    }
//                    Log.i("TAG", "light & osensor: " + lightValue  + "  oSensor  " + oSensor[1]);
                    mCPUWakeLock.acquire();
//                    mCPUWakeLock.release();
//
//                    if(deltaOriY > 900){
////                        activity.logInforFunc();
////                        if(activity.logIndex % 6 > 4)
////                        {
//                            mKeyLock.disableKeyguard();
//                            mWakelock.acquire();
//                            mWakelock.release();
////                        }
////                        start a new activity
////                        Intent intent = new Intent(getBaseContext(), TestActivity.class);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                        startActivity(intent);
//
//
//                        //解锁和亮屏
////                            mKeyLock.disableKeyguard();
////                            mWakelock.acquire();
////                            mWakelock.release();
//                    }
//                    Log.i("TAG", "ori: " + oSensor[0] + "[1]" + oSensor[1] + "[2] " + oSensor[2] + "deltaY " +  deltaOriY);
                    break;
                case Sensor.TYPE_LIGHT:
                    float acc = e.accuracy;
                    float lux = e.values[0];
                    lightValue = lux;



//                    float acc = e.accuracy;
//                    float lux = e.values[0];
//                    if(lightValueList.size() < LIGHT_SENSOR_LIST_MAX_LENGHT) {
//                        lightValueList.add((int) lux);
//                    }else
//                    {
//                        lightValueList.remove(0);
//                        lightValueList.add((int) lux);
//                    }
//
//                    if(lightValueList.size() == LIGHT_SENSOR_LIST_MAX_LENGHT)
//                    {
//                        int sumLightValue = 0;
//                        for(int i = 0; i < lightValueList.size(); i++)
//                        {
//                            sumLightValue += lightValueList.get(i);
//                        }
//                        avgLightValue = sumLightValue / LIGHT_SENSOR_LIST_MAX_LENGHT;
////                        if(avgLightValue < 50){
////                            mWakelock.acquire();
////                            mWakelock.release();
////                        }
//                        Log.i("TAG", "lightsensorvalue: " + "avgLightValue" + avgLightValue );
//                    }
//
//                    Log.i("TAG", "lightsensorvalue: " + acc + "lux " + lux );


//                    float acc = e.accuracy;
//                    //获取光线强度
//                    float lux = e.values[0];
//                    if(lux < 50){
//        //                algo.
//
//                        mWakelock.acquire();
//                        mWakelock.release();
//                    }
//                    Log.i("TAG", "lightsensorvalue: " + acc + "lux " + lux );
                    break;
            }


        }

    }
}
