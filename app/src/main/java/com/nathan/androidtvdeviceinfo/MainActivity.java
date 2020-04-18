package com.nathan.androidtvdeviceinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nathan.androidtvdeviceinfo.apipresenter.BuildInfo;
import com.nathan.androidtvdeviceinfo.apipresenter.impl.NonSystemApiImpl;
import com.nathan.androidtvdeviceinfo.apipresenter.impl.SystemApiImpl;
import com.nathan.androidtvdeviceinfo.service.FloatingCpuService;
import com.nathan.androidtvdeviceinfo.service.FloatingMemService;
import com.nathan.androidtvdeviceinfo.service.FloatingNetService;
import com.nathan.androidtvdeviceinfo.util.NonSystemUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_flow);
        Button buttonCpu = findViewById(R.id.bt_open_cpu);
        Button buttonMem = findViewById(R.id.bt_open_mem);
        Button buttonNet = findViewById(R.id.bt_open_net);
        Button buttonAll = findViewById(R.id.bt_open_all);
        buttonCpu.setOnClickListener(this);
        buttonMem.setOnClickListener(this);
        buttonNet.setOnClickListener(this);
        buttonAll.setOnClickListener(this);

        mContext = this;
        Log.i("zyf", "ttt:"+ NonSystemUtils.getProp().length());
        Log.i("zyf", "getCpuInfo "+ new NonSystemApiImpl().getCpuInfo());
        Log.i("zyf", "cat /system/build.prop "+ new NonSystemApiImpl().exeShellCmd("cat /system/build.prop"));
    }


    @Override
    public void onClick(View v) {
        startService(v);
    }

    private void startService(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //提示用户，让用户授权
                Toast.makeText(getApplicationContext(),"Please open Settings->App->Special app access\n" +
                        "Display over other apps\n " +
                        "Check Allowed",Toast.LENGTH_LONG).show();
            } else {
                switch (v.getId()){
                    case R.id.bt_open_cpu:
                        Log.i("zyf","open cpu");
                        Intent intent = new Intent(MainActivity.this, FloatingCpuService.class);
                        startService(intent);
                        ((MainActivity)mContext).finish();
                        break;
                    case R.id.bt_open_mem:
                        Log.i("zyf","open mem");
                        intent = new Intent(MainActivity.this, FloatingMemService.class);
                        startService(intent);
                        ((MainActivity)mContext).finish();
                        break;
                    case R.id.bt_open_net:
                        Log.i("zyf","bt_open_net");
                        intent = new Intent(MainActivity.this, FloatingNetService.class);
                        startService(intent);
                        ((MainActivity)mContext).finish();
                        break;
                    case R.id.bt_open_all:
                        Log.i("zyf","open all");
                        intent = new Intent(MainActivity.this, FloatingCpuService.class);
                        startService(intent);
                        intent = new Intent(MainActivity.this, FloatingMemService.class);
                        startService(intent);
                        intent = new Intent(MainActivity.this, FloatingNetService.class);
                        startService(intent);
                        ((MainActivity)mContext).finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
