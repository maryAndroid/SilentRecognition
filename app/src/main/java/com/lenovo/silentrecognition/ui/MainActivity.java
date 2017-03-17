package com.lenovo.silentrecognition.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.silentrecognition.R;
import com.lenovo.silentrecognition.capture.CameraPreview;
import com.lenovo.silentrecognition.capture.SavePictureTask;
import com.lenovo.silentrecognition.common.GlobalConfig;
import com.lenovo.silentrecognition.data.bean.PersonInfo;
import com.lenovo.silentrecognition.data.db.PersonInfoDao;
import com.lenovo.silentrecognition.event.MessageEvent;
import com.lenovo.silentrecognition.reciever.ScreenReciever;
import com.lenovo.silentrecognition.service.ScreenControlLService;
import com.lenovo.silentrecognition.service.TakePicService;
import com.lenovo.silentrecognition.utils.FileUtils;
import com.lenovo.silentrecognition.utils.LogUtil;
import com.lenovo.silentrecognition.utils.RecieverUtil;
import com.lenovo.splushlib.SplashView;

import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by mary on 2017/3/1.
 */

public class MainActivity extends Activity{
    private boolean isScreenRecieverStart = false;
    private FrameLayout previewContainer;
    private CameraPreview cameraPreview;
    private ScreenReciever mScreenOReceiver;
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePictureTask(new SavePictureTask.SavePicListener() {

                @Override
                public void saveFinish() {
                    //这句是最重要的,必须执行startPreview后,才能再次拍照
                    GlobalConfig.mCamera.startPreview();
                    GlobalConfig._isReadToGo = true;
                }
            }).execute(data);
        }
    };
    private ScreenControlLService.MyBinder myBinder;
    private ScreenControlLService screenControlService;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e("onServiceDisconnected() executed");
            myBinder.disconnected();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (ScreenControlLService.MyBinder) service;
            screenControlService = myBinder.getScreenControlLService(); //获取Myservice对象

            /**
             * 直接把当前对象传给service，这样service就可以随心所欲的调用本activity的各种可用方法
             */
            screenControlService.setMainActivity(MainActivity.this); //把当前对象传递给myservice

            myBinder.connected();
            LogUtil.e("onServiceConnected() executed");
        }
    };
    boolean isTakePicBind = false;
    private TakePicService takePicService;
    private TakePicService.TakePicBinder takePicBinder;
    private ServiceConnection takePicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            takePicBinder = (TakePicService.TakePicBinder) service;
            takePicService= takePicBinder.getTakePicService();
            takePicService.setActivity(MainActivity.this); //把当前对象传递给myservice
            takePicBinder.connected();
            LogUtil.e("Take Picture onServiceConnected() executed");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            takePicBinder.disconnect();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*SplashView.showSplashView(this, 4, R.drawable.ic_launcher, new SplashView.OnSplashViewActionListener() {
            @Override
            public void onSplashImageClick(String actionUrl) {
                Log.d("SplashView", "img clicked. actionUrl: " + actionUrl);
                //Toast.makeText(this, "img clicked.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSplashViewDismiss(boolean initiativeDismiss) {
                Log.d("SplashView", "dismissed, initiativeDismiss: " + initiativeDismiss);
            }
        });*/
        initView();
        mScreenOReceiver = new ScreenReciever();
        EventBus.getDefault().register(this);
        registerScreenReciever();
        startScreenControlService();
        initData();
        readyToGo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        relax();
        stopScreenControlService();
        EventBus.getDefault().unregister(this);
        unRegisterScreenReciever();
    }

    public void onEventAsync(MessageEvent event) {
        String msg = event.getMsg();
        switch (msg){
            case GlobalConfig.MSG_RECIEVER_SCREEN_ON:
                readyToGo();
                LogUtil.e("onEventMainThread mCamera == null ? "+(GlobalConfig.mCamera == null));
                startTakePicService();
                LogUtil.e("MSG_RECIEVER_SCREEN_ON ");
                break;
            case GlobalConfig.MSG_RECIEVER_SCREEN_OFF:
                LogUtil.e("MSG_RECIEVER_SCREEN_OFF ");
                stopTakePicService();
                break;
        }
    }

    public void initView(){
        previewContainer = (FrameLayout) findViewById(R.id.camera_container);
    }
    private void initData() {
//        addPersonInfo(-2,"NoBody",null, null);
//        addPersonInfo(-1,"Unknown",null, null);
        addPersonInfo(0,"Unknown",null, null);
        addPersonInfo(1,"BaiTianXiang",null, null);
        addPersonInfo(2,"Daryl",null, null);
        addPersonInfo(3,"DongPei",null, null);
        addPersonInfo(4,"JanetJin",null, null);
        addPersonInfo(5,"KeHaiBin",null, null);
        addPersonInfo(6,"LiuWei",null, null);
        addPersonInfo(7,"LiuYongHua",null, null);
        addPersonInfo(8,"RuiYong",null, null);
        addPersonInfo(9,"XinChen",null, null);
        addPersonInfo(10,"XuFeng",null, null);
        addPersonInfo(11,"ZhangFan",null, null);
        addPersonInfo(12,"YY",null, null);
        FileUtils.copyDbFile(this);
    }

    private void addPersonInfo(int recognitionId, String name, String imgPath, String voicePath) {
        PersonInfo p = new PersonInfo();
        p.setRecognitionId(recognitionId);
        p.setName(name);
        p.setImagePath(imgPath);
        p.setVoicePath(voicePath);
        PersonInfoDao.getInstance(this).addPersonInfo(p);
    }

    public Camera.Size getBestSize(Camera.Parameters myParams){
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = myParams.getSupportedPictureSizes();
        bestSize = sizeList.get(1);
        for(int i = 1;i<sizeList.size();i++){
            if((sizeList.get(i).width *sizeList.get(i).height) > (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }
        LogUtil.e("bestSize.width:"+bestSize.width+"bestSize.height:"+bestSize.height);
        return bestSize;
    }
    private void setCameraParams() {
        Camera.Parameters parameters = GlobalConfig.mCamera.getParameters();

        int cameraPictureRotation;
        cameraPictureRotation = 270;
//        if (_currentCameraIndex == _back_camera_index) {
//            cameraPictureRotation = 90;
//
//            //set preview to right orientation
//            // _camera.setDisplayOrientation(90);
//        } else {
//            cameraPictureRotation = 270;
//        }

        List<String> focusModesList = parameters.getSupportedFocusModes();

        //增加对聚焦模式的判断
        if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {

            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

//        parameters.setPreviewFormat(ImageFormat.NV21);
//        parameters.setPictureFormat(ImageFormat.NV21);
        parameters.setPictureSize(GlobalConfig.CAMERA_WIDTH,GlobalConfig.CAMERA_HEIGHT);
        parameters.setRotation(cameraPictureRotation);
        GlobalConfig.mCamera.setParameters(parameters);
//        Camera.Size size = getBestSize(parameters);
//        GlobalConfig.CAMERA_WIDTH = size.width;
//        GlobalConfig.CAMERA_HEIGHT = size.height;
        LogUtil.e("width: "+GlobalConfig.CAMERA_WIDTH+"  height: "+GlobalConfig.CAMERA_HEIGHT);
    }

    private void readyToGo() {
        if (GlobalConfig._isReadToGo)
            return;

        LogUtil.e("readyToGo  GlobalConfig.mCamera == null ? "+(GlobalConfig.mCamera == null));
        if (GlobalConfig.mCamera == null) {
            try {
                GlobalConfig.mCamera = Camera.open(1);
                setCameraParams();
            } catch (Exception e) {
                LogUtil.e("warning_camera_not_available");
                Toast.makeText(this, "warning_camera_not_available", Toast.LENGTH_SHORT).show();
                GlobalConfig._isReadToGo = false;
                return;
            }
        }

//        setCameraParams();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cameraPreview = new CameraPreview(getBaseContext(), GlobalConfig.mCamera);
                previewContainer.addView(cameraPreview);
                GlobalConfig._isReadToGo = true;
            }
        });
    }

    private void relax() {

        if (GlobalConfig._isReadToGo == false)
            return;

        if (GlobalConfig.mCamera != null) {
            GlobalConfig.mCamera.stopPreview();
            GlobalConfig.mCamera.release();
            GlobalConfig.mCamera = null;
            LogUtil.e("MainActivity relax(), camera released!");
        }

        previewContainer.removeView(cameraPreview);
        GlobalConfig._isReadToGo = false;
    }

    public void registerScreenReciever(){
        if(!isScreenRecieverStart) {
            String actionOn = "android.intent.action.SCREEN_ON";
            String actionOff = "android.intent.action.SCREEN_OFF";
            IntentFilter filter = new IntentFilter();
            filter.addAction(actionOn);
            filter.addAction(actionOff);
            registerReceiver(mScreenOReceiver,filter);
            isScreenRecieverStart = true;
        }
    }

    public void unRegisterScreenReciever(){
        if (isScreenRecieverStart) {
            unregisterReceiver(mScreenOReceiver);
            isScreenRecieverStart = false;
        }
    }

    public void startTakePicService(){
        if (!isTakePicBind) {
            Intent i = new Intent(this, TakePicService.class);
            startService(i);
            isTakePicBind = bindService(i, takePicConnection, BIND_AUTO_CREATE);
            LogUtil.e("main isTakePicBind"+isTakePicBind);
        }
    }

    public void stopTakePicService(){
        Intent i = new Intent(this, TakePicService.class);
//        stopService(i);
        if(isTakePicBind) {
            unbindService(takePicConnection);
            stopService(i);
            isTakePicBind = false;
        }
        LogUtil.e(" stop take pic service!");
    }
    public void startScreenControlService(){
        Intent startIntent = new Intent(this, ScreenControlLService.class);
        startService(startIntent);
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }

    public void stopScreenControlService(){
        Intent stopIntent = new Intent(this, ScreenControlLService.class);
//        stopService(stopIntent);
        unbindService(connection);
    }

    private Dialog progressDialog;
    private void initProgressDialog(){
        progressDialog = new Dialog(this, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("加载中");
    }

    private void showProgressDialog(){
        if(!progressDialog.isShowing())
            progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog.isShowing())
            progressDialog.cancel();
    }

}
