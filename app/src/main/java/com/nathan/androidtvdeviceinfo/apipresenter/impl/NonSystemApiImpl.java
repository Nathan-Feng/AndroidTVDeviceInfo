package com.nathan.androidtvdeviceinfo.apipresenter.impl;


import android.content.Context;

import com.nathan.androidtvdeviceinfo.apipresenter.NonSystemDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.util.NonSystemUtils;


public final class NonSystemApiImpl implements NonSystemDeviceInfoApi {


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
        return NonSystemUtils.getFreeMemInfo(context);
    }

    @Override
    public String getTotalMemInfo(Context context) {
        return NonSystemUtils.getTotalMemInfo(context);
    }

    @Override
    public int getScreenWidth(Context context) {
        return NonSystemUtils.getScreenWidth(context);
    }

    @Override
    public int getScreenHeight(Context context) {
        return NonSystemUtils.getScreenHeight(context);
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
        return NonSystemUtils.getTotalNetworkRxBytes(context);
    }

    @Override
    public int convertDpToPixel(int dp, Context context) {
        return NonSystemUtils.convertDpToPixel(dp,context);
    }

    @Override
    public int convertPixelsToDp(int px, Context context) {
        return NonSystemUtils.convertPixelsToDp(px,context);
    }
}
