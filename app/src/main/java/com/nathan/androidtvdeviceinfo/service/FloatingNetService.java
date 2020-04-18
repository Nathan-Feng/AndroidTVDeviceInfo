package com.nathan.androidtvdeviceinfo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nathan.androidtvdeviceinfo.R;
import com.nathan.androidtvdeviceinfo.apipresenter.NonSystemDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.apipresenter.SystemDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.apipresenter.impl.NonSystemApiImpl;
import com.nathan.androidtvdeviceinfo.apipresenter.impl.SystemApiImpl;
import com.nathan.androidtvdeviceinfo.util.WeakHandler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FloatingNetService extends Service {
    public static boolean isStarted = false;
    private static final String MB = " MB";
    private static final String KBs = " KB/s";
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private int[][] oldAllNetSpeed = new int[2][2];
    private static long ret1=0;

    private WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 100){
                String[][] temp = (String[][])msg.obj;
                int newEth0ReSpeed = Integer.parseInt(temp[0][0])/1024;
                int newEth0TrSpeed = Integer.parseInt(temp[0][1])/1024;

                int newWifiReSpeed = Integer.parseInt(temp[1][0])/1024;
                int newWifiTrSpeed = Integer.parseInt(temp[1][1])/1024;
                Log.d("zyf", "newEth0ReSpeed: "+newEth0ReSpeed);
                Log.d("zyf", "oldAllNetSpeed[0][0]: "+oldAllNetSpeed[0][0]);
                if (oldAllNetSpeed[0][0] == 0 ){
                    oldAllNetSpeed[0][0]= newEth0ReSpeed;
                    oldAllNetSpeed[0][1]= newEth0TrSpeed;

                }else {
                    int currRe = newEth0ReSpeed- oldAllNetSpeed[0][0];
                    Log.d("zyf", "newEth0ReSpeed: "+newEth0ReSpeed);
                    int currTr = newEth0TrSpeed- oldAllNetSpeed[0][1];
                    oldAllNetSpeed[0][0]= newEth0ReSpeed;
                    oldAllNetSpeed[0][1]= newEth0TrSpeed;
                    ethReSpeed.setText(currRe+KBs);
                    ethTrSpeed.setText(currTr+KBs);
                }

                if (oldAllNetSpeed[1][0] == 0){
                    oldAllNetSpeed[1][0]= newWifiReSpeed;
                    oldAllNetSpeed[1][1]= newWifiTrSpeed;
                }else {
                    int currRe = newWifiReSpeed- oldAllNetSpeed[1][0];
                    int currTr = newWifiTrSpeed- oldAllNetSpeed[1][1];
                    oldAllNetSpeed[1][0]= newWifiReSpeed;
                    oldAllNetSpeed[1][1]= newWifiTrSpeed;
                    wifiReSpeed.setText(currRe+KBs);
                    wifiTrSpeed.setText(currTr+KBs);
                }

                mWeakHandler.postDelayed(runnable,1000);

                long ret3 = nonSystemApi.getTotalNetworkRxBytes(context);
                Log.i("zyf", "onCreate1: "+(ret3-ret1));
                ret1 = ret3;
            }
            return false;
        }
    });

    private TextView ethReSpeed,ethTrSpeed ;
    private TextView wifiReSpeed,wifiTrSpeed ;
    private Context context;
    private NonSystemDeviceInfoApi nonSystemApi;
    public SystemDeviceInfoApi systemApi;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        context = this;
        nonSystemApi = new NonSystemApiImpl();
        systemApi = new SystemApiImpl();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.END | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = nonSystemApi.convertDpToPixel(200,this);
        layoutParams.height = nonSystemApi.convertDpToPixel(170,this);
        layoutParams.x = nonSystemApi.convertDpToPixel(50,this);
        layoutParams.y = nonSystemApi.convertDpToPixel(50,this);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        mWeakHandler.postDelayed(runnable,1000);
        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String[][] netData = systemApi.getAllNetData();
            Message message = Message.obtain();
            message.what = 100;
            message.obj = netData;
            mWeakHandler.sendMessage(message);
        }
    };

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.net_float_window, null);
            ethReSpeed = view.findViewById(R.id.tv_eth_re_text);
            ethTrSpeed = view.findViewById(R.id.tv_eth_tr_text);
            wifiReSpeed = view.findViewById(R.id.tv_wifi_re_text);
            wifiTrSpeed = view.findViewById(R.id.tv_wifi_tr_text);
            windowManager.addView(view, layoutParams);
            final ImageView dismiss = view.findViewById(R.id.net_close);
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    windowManager.removeViewImmediate(view);
                }
            });
            view.setOnTouchListener(new FloatingOnTouchListener());
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
