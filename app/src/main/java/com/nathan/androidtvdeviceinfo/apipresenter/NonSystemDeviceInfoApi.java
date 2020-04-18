package com.nathan.androidtvdeviceinfo.apipresenter;

import android.content.Context;

public interface NonSystemDeviceInfoApi {
    /**
     * get total Cpu number
     * @return int value
     */
    int getCpuNumber();

    /**
     * @return same as cat /proc/cpuinfo
     */
    String getCpuInfo();

    /**
     * @param context
     * @return unit:MB
     */
    String getFreeMemInfo(Context context);

    /**
     * @param context
     * @return unit:MB
     */
    String getTotalMemInfo(Context context);

    int getScreenWidth(Context context);
    int getScreenHeight(Context context);
    float getScreenDensity();
    int getScreenDensityDpi();
    boolean isSDCardEnable();

    /**
     * @return sdcard total size:
     */
    String getTotalExternalFlashSize();

    /**
     * @return sdcard available size:
     */
    String getAvailableExternalFlashSize();

    /**
     * @param cmd such as ls,cat, etc..
     * @return
     */
    String exeShellCmd(String cmd);

    /**
     * @return same as su:getprop
     */
    String getProp();

    /**
     * @param context
     * @return unit :KB
     */
    long getTotalNetworkRxBytes(Context context);

    int convertDpToPixel(int dp, Context context);

    int convertPixelsToDp(int px, Context context);
}
