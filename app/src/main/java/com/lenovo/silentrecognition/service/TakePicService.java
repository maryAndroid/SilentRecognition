package com.lenovo.silentrecognition.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lenovo.silentrecognition.common.GlobalConfig;
import com.lenovo.silentrecognition.data.bean.PersonInfo;
import com.lenovo.silentrecognition.data.db.PersonInfoDao;
import com.lenovo.silentrecognition.jni.RecognitionNative;
import com.lenovo.silentrecognition.jni.SmartNative;
import com.lenovo.silentrecognition.ui.FloatActivity;
import com.lenovo.silentrecognition.utils.BitmapUtil;
import com.lenovo.silentrecognition.utils.LogUtil;

import java.io.File;

/**
 * Created by mary on 2017/3/2.
 */

public class TakePicService extends Service {
    public static boolean isTakePicRecieverStarted = false;
    private Activity activity;
    private TakePicService.TakePicBinder binder = new TakePicBinder();
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            LogUtil.e("pictureCallback onPictureTaken");
            exeRecognition(data);
            GlobalConfig.mCamera.startPreview();
            GlobalConfig._isReadToGo = true;

        }
    };
    private BroadcastReceiver takePicReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.lenovo.silent.takepic.action")){
                LogUtil.e("takePicReciever start");
                if(GlobalConfig.mCamera != null){
                    LogUtil.e("takePicReciever mCamera == null ? "+(GlobalConfig.mCamera == null)+"width: "+GlobalConfig.CAMERA_WIDTH+"height:"+GlobalConfig.CAMERA_HEIGHT+"\nGlobalConfig._isReadToGo: "+GlobalConfig._isReadToGo);
                    if (GlobalConfig._isReadToGo)
                        GlobalConfig.mCamera.takePicture(null, null, pictureCallback);
                    GlobalConfig._isReadToGo = false;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e("TakePicService start");
        registerTakePicReciever();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.e("TakePicService onBind");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("TakePicService onDestroy");
        unRegisterTakePicReciever();
    }
    private void registerTakePicReciever(){
        if(!isTakePicRecieverStarted) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.lenovo.silent.takepic.action");
            registerReceiver(takePicReciever, filter);
            isTakePicRecieverStarted = true;
        }
    }

    private void unRegisterTakePicReciever(){
        if(isTakePicRecieverStarted){

            unregisterReceiver(takePicReciever);
            isTakePicRecieverStarted = false;
        }
    }

    public void exeRecognition(byte[] data){
        LogUtil.e("SmartNative  start");
        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
//        Bitmap bitmap = BitmapUtil.byteToBitmap(data);
        final byte[] pixelBuf = new byte[ 112 * 96 * 3];
        int detect_faces_res = SmartNative.facesDetectDLibBmp(bitmap,pixelBuf) ;
        bitmap.recycle();
        bitmap = null;
//        showPopView("detect_faces_res"+detect_faces_res);
        LogUtil.e("SmartNative detect_faces_res = "+detect_faces_res);
        if ( detect_faces_res > 0 ){
            LogUtil.e("Detect one face");
            int id = RecognitionNative.jniFaceDetectRefine(112, 96,pixelBuf);
            LogUtil.e("exeRecognitonProcess:  id == " +id);
            if(id == 0){
                showPopView("我认不出来，能再来一次吗？");
                return;
            }
//            showPopView("id == "+id);
            PersonInfoDao dao = PersonInfoDao.getInstance(TakePicService.this);
            boolean isPersonExist = dao.checkPersonExist(id);
            LogUtil.e("exeRecognitonProcess:  isPersonExist == " +isPersonExist);
            PersonInfo p = dao.findPersonInfoByRecId(id);
            if(p != null) {
                String popMsg = GlobalConfig.getRandomMsg(p.getName());
                showPopView(popMsg);
                /*new SavePictureTask(new SavePictureTask.SavePicListener() {
                    @Override
                    public void saveFinish() {
                        LogUtil.e("picture saved success.");
                        update2localAlbum();
                    }
                }).execute(data);*/
            }else {
                showPopView("no data in local");
            }
        }else{
            LogUtil.e("Face not detected");
            activateTakePicReciever();
        }
    }

    public void activateTakePicReciever(){
        LogUtil.e("activateTakePicReciever");
        Intent it = new Intent();
        it.setAction("com.lenovo.silent.takepic.action");
        sendBroadcast(it);
    }

    static int i = 0;
    public void showPopView(String name){
        Intent mIntent = new Intent(this, FloatActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra("name",name);
        LogUtil.e("showPopView name:"+name);
        startActivity(mIntent);
    }

    public class TakePicBinder extends Binder{
        public void disconnect(){
            LogUtil.e("TakePicBinder disconnect");
        }
        public void connected(){
            LogUtil.e("TakePicBinder connect");
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            registerTakePicReciever();
            activateTakePicReciever();
        }
        public TakePicService getTakePicService(){
            return TakePicService.this;
        }
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    private void update2localAlbum(){
        Intent ite = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(GlobalConfig.RawImageStoreUrl));
        ite.setData(uri);
        this.sendBroadcast(ite);
    }
}
