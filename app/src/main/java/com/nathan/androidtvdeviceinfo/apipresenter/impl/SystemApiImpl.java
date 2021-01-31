package com.nathan.androidtvdeviceinfo.apipresenter.impl;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.tv.settings.connectivity.HsSystemApi;
import com.nathan.androidtvdeviceinfo.apipresenter.SystemDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.util.CommonUtils;
import com.nathan.androidtvdeviceinfo.util.SystemUtils;
import com.nathan.androidtvdeviceinfo.util.SystemUtils;
import com.nathan.androidtvdeviceinfo.ShellCmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.Context.WIFI_SERVICE;
public final class SystemApiImpl implements SystemDeviceInfoApi {

    private static final String TAG = "SystemApiImpl_zyf";

    @Override
    public String getMaxCpuFreq() {
        return SystemUtils.getMaxCpuFreq();
    }

    @Override
    public String getMinCpuFreq() {
        return SystemUtils.getMinCpuFreq();
    }

    @Override
    public String getCurCpuFreq(int cpuNum) {
        return SystemUtils.getCurCpuFreq(cpuNum);
    }

    @Override
    public String getCurCpuRate(int cpuNum) {
        return SystemUtils.getCurCpuRate(cpuNum);
    }

    @Override
    public String getCpuName() {
        return SystemUtils.getCpuName();
    }

    @Override
    public String getSerialNo() {
        return SystemUtils.getSerialNo();
    }

    @Override
    public String getEthernetMacAddress() {
        return SystemUtils.getEthernetMacAddress();
    }

    @Override
    public String getWifiMacAddress() {
        return SystemUtils.getWifiMacAddress();
    }

    @Override
    public String getEthIpAddress() {
        return SystemUtils.getEthIpAddress();
    }

    @Override
    public String getWifiIpAddress() {
        return SystemUtils.getWifiIpAddress();
    }

    @Override
    public String getGpuFreqRange() {
        return SystemUtils.getGpuFreqRange();
    }

    @Override
    public String[][] getAllNetData() {
        return SystemUtils.getAllNetData();
    }


    private int cpuTotalTime = 0;
    private int idleTotalTime = 0;
    private int count = 0;
    private String percent = "0";

    @Override
    public String getCpuUseRate() {
        String cmd = "cat /proc/stat";
        int cpuTotalTemp = 0;
        int idleTotalTemp = 0;
        try {
            //Log.i(TAG, "getCpuRate runtime begin");
            Process p = Runtime.getRuntime().exec(cmd);
            //Log.i(TAG, "getCpuRate runtime end");
            int status = p.waitFor();
            //Log.i(TAG, "getCpuRate waitFor end");
            if (0 == status) {
                BufferedReader bufread = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                int index = 0 ;
                while (null != (line = bufread.readLine())) {
                    //Log.i(TAG, index+++"getCpuRate out= " + line);
                    if (line.trim().length() < 1) {
                        //continue;
                    } else if (line.trim().contains("cpu")){
                        String[] targetLine = line.split(" ");
                        for (int i= 2 ; i< targetLine.length;i++) {
                            //Log.i(TAG, index+"  "+" index:"+i+" getCpuLine = " + targetLine[i]);
                            if (i == 5) {
                                idleTotalTemp = Integer.parseInt(targetLine[5].trim());
                                // Log.i(TAG, "idleTotalTemp" + idleTotalTemp);
                            }
                            cpuTotalTemp = cpuTotalTemp + Integer.parseInt(targetLine[i].trim());
                        }
                        //Log.i(TAG, "cpuTotalTemp" + cpuTotalTemp);
                        count = count + 1;
                        break;
                    }
                }
                bufread.close();
                p.destroy();
            } else {
                p.destroy();
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (count != 2) {
            cpuTotalTime = cpuTotalTemp ;
            idleTotalTime = idleTotalTemp;
            try {//
                Thread.sleep(50);
                percent = getCpuUseRate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //Log.i(TAG, "getCpuRate 1= " + ((idleTotalTemp - idleTotalTime)*100/(cpuTotalTemp- cpuTotalTime)));
            percent = (100 - (idleTotalTemp - idleTotalTime)*100/(cpuTotalTemp - cpuTotalTime))+"";
            //Log.i(TAG, "getCpuRate 2= " + (percent));
            cpuTotalTime = 0;
            idleTotalTime = 0;
            count = 0;
        }
        Log.i(TAG, "getCpuRate = "+percent);
        return percent+"";
    }




    /**
     * ************************************************************
     */


    @Override
    public boolean forceSystemWakeUp(Context context) {
        new HsSystemApi().wakeUp(context,SystemClock.uptimeMillis(),"wakeUp");
        return true;
    }

    @Override
    public boolean forceSystemStandby(Context context) {
        new HsSystemApi().goToSleep(context, SystemClock.uptimeMillis());
        return true;
    }

    @Override
    public boolean forceSystemReboot(Context context) {
        new HsSystemApi().reboot(context,"tr069");
        return true;
    }

    @Override
    public boolean isSystemStandby(Context context) {
        return ! new HsSystemApi().isScreenOn(context);
    }

    @Override
    public void factoryReset(Context context) {
        new HsSystemApi().factoryReset(context);
    }

    @Override
    public String[] getSystemProcessPid(String processName) {
        Log.d(TAG, "getSystemProcessPid   processName:"+processName);
        String cmd = "ps -A | grep "+processName;
        String result = NonSystemUtils.exeShellCmd(cmd);
        Log.d(TAG, "processName PIDs:"+result);
        if (result.contains(processName)){
            String[] allItem = result.replaceAll("[ ]{2,}", " ").split(" ");
            //String processPID = allItem[1].trim();
            for (int i=0;i< allItem.length;i++){
                Log.d(TAG,"get every pid:"+allItem[i]+" index:"+i);
            }
        }
        return new String[0];
    }

    @Override
    public boolean killSystemProcessPid(int pid){
        Log.d(TAG,"zyf killSystemProcessPid pid:  "+pid);
        String ret = NonSystemUtils.exeShellCmd("kill -9 "+pid);
        Log.d(TAG,"zyf killSystemProcessPid return  "+ret);
        return !ret.contains("N/A");
    }

    @Override
    public String[] getRootProcessPid(String processName) {
        String cmd = "ps -A | grep "+processName;
        String result = new ShellCmd().hsInvokeJni(cmd,1);
        Log.d(TAG, "zyf get processName PIDs:"+result);
        String[] list = result.split("\n");
        String[] pids = new String[list.length];
        for (int i = 0; i < list.length; i++){
            String[] item = list[i].replaceAll("[ ]{2,}", " ").split(" ");
            //String processPID = allItem[1].trim();
            pids[i] =  item[1];
            for (int j = 0; j < item.length; j++){
                Log.d(TAG,"zyf get every pid:"+item[i]+" index:"+i);
            }
        }
        return pids;
    }

    @Override
    public boolean killRootProcessPid(int pid){
        Log.d(TAG,"killProcessPid pid:  "+pid);
        String cmd = "kill -9 "+pid;
        String result = new ShellCmd().hsInvokeJni(cmd,1);
        Log.d(TAG,"killProcessPid return  "+result);
        return result.contains(ShellCmd.OK);
    }

    @Override
    public void uninstallPkg(Context context,@NonNull String pkgName) {
        Log.d(TAG,"uninstallPkg pkgName:  "+pkgName);
        Intent intent = new Intent(context, context.getClass());
        PendingIntent sender = PendingIntent.getActivity(context, 0, intent, 0);
        PackageInstaller mPackageInstaller = context.getPackageManager().getPackageInstaller();
        mPackageInstaller.uninstall(pkgName, sender.getIntentSender());
        Log.d(TAG,"uninstallPkg out");
    }
}
