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


public class FloatingMemService extends Service {
    public static boolean isStarted = false;
    private static final String MB = "MB";
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 100){
                freeMem.setText((String)msg.obj+MB);
                mWeakHandler.postDelayed(runnable,1000);
            }
            return false;
        }
    });

    private TextView freeMem ;
    private TextView totalMem ;
    private NonSystemDeviceInfoApi nonSystemApi;
    private SystemDeviceInfoApi systemApi;
    private Context context;

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
        layoutParams.gravity = Gravity.START | Gravity.BOTTOM;
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
            String freeMem = nonSystemApi.getFreeMemInfo(context);
            Message message = Message.obtain();
            message.what = 100;
            message.obj = freeMem;
            mWeakHandler.sendMessage(message);
        }
    };

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.mem_float_window, null);
            freeMem = view.findViewById(R.id.tv_free_text);
            totalMem = view.findViewById(R.id.tv_total_text);
            totalMem.setText(nonSystemApi.getTotalMemInfo(context)+MB);
            windowManager.addView(view, layoutParams);
            final ImageView dismiss = view.findViewById(R.id.mem_close);
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
