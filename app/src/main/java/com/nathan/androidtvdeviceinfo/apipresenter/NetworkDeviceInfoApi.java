package com.nathan.androidtvdeviceinfo.apipresenter;

import android.content.Context;

import com.nathan.androidtvdeviceinfo.apipresenter.model.EthernetInfo;
import com.nathan.androidtvdeviceinfo.apipresenter.model.NetworkStaticIpInfo;
import com.nathan.androidtvdeviceinfo.apipresenter.model.WifiItemInfo;

public interface NetworkDeviceInfoApi {

    EthernetInfo getEthernetInfo(Context context);

    EthernetInfo getEthernetInfoByType(Context context, int type);

    WifiItemInfo getWifiItemInfo(Context context);

    WifiItemInfo getWifiItemInfoByType(Context context, int type);

    boolean setNetworkStaticIpInfo(Context context, NetworkStaticIpInfo staticIpInfo);

    /**
     * ***************************************WIFI**************************************
     */

    boolean getWifiEnabled(Context context);

    boolean setWifiEnabled(Context context,boolean enable);

    boolean resetWifi(Context context);

    boolean disconnectWifi(Context context);
}
