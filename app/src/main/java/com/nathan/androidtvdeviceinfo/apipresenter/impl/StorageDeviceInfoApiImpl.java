package com.nathan.androidtvdeviceinfo.apipresenter.impl;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.nathan.androidtvdeviceinfo.apipresenter.StorageDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.storage.StorageDModel;
import com.nathan.androidtvdeviceinfo.storage.StorageManagerRepositoryImpl;
import com.nathan.androidtvdeviceinfo.util.NonSystemUtils;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public final class StorageDeviceInfoApiImpl implements StorageDeviceInfoApi {
    private static final String TAG = "StorageDeviceInfoApiImp";

    @Override
    public String getFlashSize() {
        return NonSystemUtils.getTotalExternalFlashSize();
    }

    @Override
    public String getFlashFreeSize() {
        return NonSystemUtils.getAvailableExternalFlashSize();
    }

    @Override
    public String getFlashUseRate() {
        Log.d("zyf", "getFlashUseRate: enter");
        return NonSystemUtils.getRateExternalFlashSize();
    }

    @Override
    public String getRamSize(Context context) {
        return NonSystemUtils.getTotalMemInfo(context);
    }

    @Override
    public String getRamFreeSize(Context context) {
        return NonSystemUtils.getFreeMemInfo(context);
    }

    @Override
    public String getRamRate(Context context) {
        return NonSystemUtils.getMemUsePercent(context);
    }

    /**
     * @return default for IP952 :800MB TODO
     */
    @Override
    public String getCacheSize() {
        return "800MB";
    }

    @Override
    public String getDataUsedSize() {
        return NonSystemUtils.getUsedExternalFlashSize();
    }

    /**
     * @return
     */
    @Override
    public boolean getUsbEnable() {
        return true;
    }

    @Override
    public List<StorageDModel> getUsbList(Context context) {
        return StorageManagerRepositoryImpl.getInstance(context).getUsbList();
    }


    @Override
    public List<String> getAppListName(Context context) {
        return getPkgListNew(context);
    }

    @Override
    public List<PackageInfo> getAppListInfo(Context context) {
        return getPkgListInfo(context);
    }

    private List<String> getPkgListNew(Context context) {
        List<String> packages = new ArrayList<String>();
        try {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES |
                    PackageManager.GET_SERVICES);
            for (PackageInfo info : packageInfos) {
                String pkg = info.packageName;
                packages.add(pkg);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return packages;
    }

    private List<PackageInfo> getPkgListInfo(Context context) {
        try {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES |
                    PackageManager.GET_SERVICES);
            return packageInfos;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    // 通过packName得到PackageInfo，作为参数传入即可
    private boolean isSystemApp(PackageInfo pi) {
        boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
        boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
        return isSysApp || isSysUpd;
    }


    @Override
    public boolean screencap(String path) {
        if (path == null || path.isEmpty()) {
            Log.d(TAG, "screencap: path can not be null！"+path);
            return false;
        }
        new NonSystemApiImpl().exeShellCmd("screencap -p "+path);
        return true;
    }

    @Override
    public void writeBytesToFile(byte[] data,String path) {
        byte[] bs = data;
        OutputStream out = null;
        InputStream is = null;
        try {
            out = new FileOutputStream(path);
            is = new ByteArrayInputStream(bs);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (is != null){
                try {
                    is.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
            if (out != null){
                try {
                    out.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
    }

}
