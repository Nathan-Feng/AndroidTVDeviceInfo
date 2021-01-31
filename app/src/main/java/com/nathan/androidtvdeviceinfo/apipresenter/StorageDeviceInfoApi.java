package com.nathan.androidtvdeviceinfo.apipresenter;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.nathan.androidtvdeviceinfo.storage.StorageDModel;

import java.util.List;

public interface StorageDeviceInfoApi {

    /**
     * @return
     */
    String getFlashSize();

    /**
     * @return
     */
    String getFlashFreeSize();

    /**
     * @return
     */
    String getFlashUseRate();


    /**
     * @return
     */
    String getRamSize(Context context);

    /**
     * @return
     */
    String getRamFreeSize(Context context);

    /**
     * @return
     */
    String getRamRate(Context context);

    /**
     * @return
     */
    String getCacheSize();

    /**
     * @return
     */
    String getDataUsedSize();

    /**
     * @return default true
     */
    boolean getUsbEnable();

    /**
     * @return
     */
    List<StorageDModel> getUsbList(Context context);

    List<String> getAppListName(Context context);

    List<PackageInfo> getAppListInfo(Context context);

    boolean screencap(String path);

    void writeBytesToFile(byte[] data,String path);
}
