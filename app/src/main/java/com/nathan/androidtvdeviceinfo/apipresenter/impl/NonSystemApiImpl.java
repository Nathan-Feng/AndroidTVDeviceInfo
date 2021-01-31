package com.nathan.androidtvdeviceinfo.apipresenter.impl;


import android.content.Context;
import android.util.Log;

import com.nathan.androidtvdeviceinfo.apipresenter.NonSystemDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.util.NonSystemUtils;


public final class NonSystemApiImpl implements NonSystemDeviceInfoApi {

    private static final String TAG = "NonSystemApiImpl_zyf";

    @Override
    public int getCpuNumber() {
        return NonSystemUtils.getCpuNumber();
    }

    @Override
    public String getCpuInfo() {
        return NonSystemUtils.getCpuInfo();
    }

    @Override
    public String getFreeMemInfo(Context context) {
        return NonSystemUtils.getFreeMemInfo(context.getApplicationContext());
    }

    @Override
    public String getTotalMemInfo(Context context) {
        return NonSystemUtils.getTotalMemInfo(context.getApplicationContext());
    }

    @Override
    public String getPercentMemInfo(Context context) {
        return NonSystemUtils.getMemUsePercent(context.getApplicationContext());
    }

    @Override
    public int getScreenWidth(Context context) {
        return NonSystemUtils.getScreenWidth(context.getApplicationContext());
    }

    @Override
    public int getScreenHeight(Context context) {
        return NonSystemUtils.getScreenHeight(context.getApplicationContext());
    }

    @Override
    public float getScreenDensity() {
        return NonSystemUtils.getScreenDensity();
    }

    @Override
    public int getScreenDensityDpi() {
        return NonSystemUtils.getScreenDensityDpi();
    }

    @Override
    public boolean isSDCardEnable() {
        return NonSystemUtils.isSDCardEnable();
    }

    @Override
    public String getTotalExternalFlashSize() {
        return NonSystemUtils.getTotalExternalFlashSize();
    }

    @Override
    public String getAvailableExternalFlashSize() {
        return NonSystemUtils.getAvailableExternalFlashSize();
    }

    @Override
    public String exeShellCmd(String cmd) {
        return NonSystemUtils.exeShellCmd(cmd);
    }

    @Override
    public String getProp() {
        return NonSystemUtils.getProp();
    }

    @Override
    public long getTotalNetworkRxBytes(Context context) {
        return NonSystemUtils.getTotalNetworkRxBytes(context.getApplicationContext());
    }

    @Override
    public int convertDpToPixel(int dp, Context context) {
        return NonSystemUtils.convertDpToPixel(dp,context.getApplicationContext());
    }

    @Override
    public int convertPixelsToDp(int px, Context context) {
        return NonSystemUtils.convertPixelsToDp(px,context.getApplicationContext());
    }

    @Override
    public String[] getSystemLogcatPids() {
        Log.d(TAG, "getSystemLogcatPids in");
        String cmd = "ps -A | grep logcat";
        String result = NonSystemUtils.exeShellCmd(cmd);
        Log.d(TAG, "get processName PIDs:"+result);
        if (result == null || result.isEmpty() || !result.contains("logcat")){
            Log.e(TAG, "getSystemLogcatPids = null:");
            return new String[0];
        }
        String[] list = result.split("\n");
        String[] pids = new String[list.length];
        for (int j = 0; j <list.length; j++){
            String[] allItem = list[j].replaceAll("[ ]{2,}", " ").split(" ");
            //String processPID = allItem[1].trim();
            for (int i=0;i< allItem.length;i++){
                Log.d(TAG,"zyf get every pid:"+allItem[i]+" index:"+i);
            }
            pids[j] =  allItem[1];
        }
        return pids;
    }
}
