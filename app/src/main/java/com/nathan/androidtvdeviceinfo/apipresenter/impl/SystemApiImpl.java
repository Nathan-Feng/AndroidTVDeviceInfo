package com.nathan.androidtvdeviceinfo.apipresenter.impl;


import com.nathan.androidtvdeviceinfo.apipresenter.SystemDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.util.SystemUtils;

public final class SystemApiImpl implements SystemDeviceInfoApi {


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
}
