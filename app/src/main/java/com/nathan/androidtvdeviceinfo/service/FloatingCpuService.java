package com.nathan.androidtvdeviceinfo.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
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


public class FloatingCpuService extends Service {
    public static boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 100){
                mWeakHandler.postDelayed(runnable,1000);
                cpu.setText((String)msg.obj);
            }
            return false;
        }
    });

    private int cpuNumber;
    private TextView cpu ;
    private TextView cpuFreq;
    private NonSystemDeviceInfoApi nonSystemApi;
    private SystemDeviceInfoApi systemApi;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
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
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = nonSystemApi.convertDpToPixel(250,this);
        layoutParams.height = nonSystemApi.convertDpToPixel(200,this);
        layoutParams.x = nonSystemApi.convertDpToPixel(50,this);
        layoutParams.y = nonSystemApi.convertDpToPixel(50,this);

        cpuNumber = Runtime.getRuntime().availableProcessors();
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
            Message message = Message.obtain();
            message.what = 100;
            String cpuInfo = "";
            for (int i = 0 ;i < cpuNumber ; i++){
                cpuInfo = cpuInfo+"CPU"+i+":"+Integer.parseInt(systemApi.getCurCpuFreq(i))/1000+" MHz\n";
            }
            message.obj = cpuInfo;
            mWeakHandler.sendMessage(message);

        }
    };

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View cpuView = layoutInflater.inflate(R.layout.cpu_float_window, null);
            cpu = cpuView.findViewById(R.id.tv_cpu_text);
            cpuFreq = cpuView.findViewById(R.id.tv_cpu_freq_text);
            String freq = Integer.parseInt(systemApi.getMinCpuFreq())/1000 + "MHz~"
                    + Integer.parseInt(systemApi.getMaxCpuFreq())/1000 + "MHz";
            cpuFreq.setText(freq);
            final ImageView dismiss = cpuView.findViewById(R.id.cpu_close);
            dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    windowManager.removeViewImmediate(cpuView);
                }
            });
            windowManager.addView(cpuView, layoutParams);

            cpuView.setOnTouchListener(new FloatingOnTouchListener());
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
