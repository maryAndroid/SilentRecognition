package com.lenovo.silentrecognition.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lenovo.silentrecognition.R;
import com.lenovo.silentrecognition.utils.LogUtil;

/**
 * Created by mary on 2017/3/2.
 */

public class FloatActivity extends Activity {
    private TextView textView;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        Window win = getWindow();
        lp.alpha=0.3f;
        win.setAttributes(lp);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_float);
        textView = (TextView) findViewById(R.id.activity_float_text);
        intent = getIntent();
        String name = intent.getStringExtra("name");
        LogUtil.e("showPopView floatac name:"+name);
        textView.setText(name);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String name = intent.getStringExtra("name");
        LogUtil.e("showPopView floatac new intent name:"+name);
        textView.setText(name);
    }

    public void clickQuit(View view){
        finish();
    }
}
