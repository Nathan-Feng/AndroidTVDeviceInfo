package com.nathan.androidtvdeviceinfo.apipresenter.impl;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.android.tv.settings.connectivity.HsSystemApi;
import com.android.tv.settings.connectivity.util.AdvancedOptionsFlowUtil;
import com.nathan.androidtvdeviceinfo.apipresenter.NetworkDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.apipresenter.model.EthernetInfo;
import com.nathan.androidtvdeviceinfo.apipresenter.model.NetworkStaticIpInfo;
import com.nathan.androidtvdeviceinfo.apipresenter.model.WifiItemInfo;
import com.nathan.androidtvdeviceinfo.util.CommonUtils;
import com.nathan.androidtvdeviceinfo.util.NonSystemUtils;
import com.nathan.androidtvdeviceinfo.util.SystemUtils;

import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public final class NetworkDeviceInfoApiImpl implements NetworkDeviceInfoApi {

    private WifiManager wifiManager = null;
    private static final String TAG = "NetworkDeviceInfoApiImp";


    @Override
    public EthernetInfo getEthernetInfo(Context context) {
        EthernetInfo ethernetInfo = new EthernetInfo();
        ethernetInfo.setConnected(getEthConnectStatus(context.getApplicationContext()));
        ethernetInfo.setConnectMode(getEthConnectMode(context.getApplicationContext()));
        ethernetInfo.setIpMode(getEthIpMode());
        ethernetInfo.setNetmask(getEthNetmask());
        ethernetInfo.setGateway(getEthGatewayDefault());
        ethernetInfo.setDns1(getEthDns1(context.getApplicationContext()));
        ethernetInfo.setDns2(getEthDns2(context.getApplicationContext()));
        return ethernetInfo;
    }

    @Override
    public EthernetInfo getEthernetInfoByType(Context context, int type) {
        Log.d(TAG, "getEthernetInfoByType: type:"+type);
        Context mContext = context.getApplicationContext();
        EthernetInfo ethernetInfo = new EthernetInfo();
        if (type == EthernetInfo.CONNECTED){
            ethernetInfo.setConnected(getEthConnectStatus(mContext));
        } else if (type == EthernetInfo.CONNECT_MODE) {
            ethernetInfo.setConnectMode(getEthConnectMode(mContext));
        } else if (type == EthernetInfo.IP_MODE) {
            ethernetInfo.setIpMode(getEthIpMode());
        } else if (type == EthernetInfo.GATEWAY) {
            ethernetInfo.setGateway(getEthGatewayDefault());
        } else if (type == EthernetInfo.NETMASK) {
            ethernetInfo.setNetmask(getEthNetmask());
        } else if (type == EthernetInfo.DNS1) {
            ethernetInfo.setDns1(getEthDns1(mContext));
        } else if (type == EthernetInfo.DNS2) {
            ethernetInfo.setDns2(getEthDns2(mContext));
        }
        Log.d(TAG, type+" getEthernetInfoByType: "+ethernetInfo.toString());
        return ethernetInfo;
    }


    private boolean getEthConnectStatus(Context context) {
        String result = NonSystemUtils.exeShellCmd("cat /sys/class/net/eth0/carrier");
        Log.d(TAG, "getEthConnectStatus: "+result.contains("1"));
        return result.contains("1");
    }

    /**
     * @return PPPoE/DHCP/Manual TODO
     */
    private String getEthConnectMode(Context context) {
        Log.d(TAG, "getEthConnectMode: enter");

        if (AdvancedOptionsFlowUtil.isIpStatic(context,true,-1)){
            Log.d(TAG, "getEthConnectMode: manual");
            return "Manual";
        } else {
            Log.d(TAG, "getEthConnectMode: dhcp");
            return "DHCP";
        }
    }

    /**
     * @return IPV4/IPV6/IPV4+IPV6  TODO
     */
    private String getEthIpMode() {
        return "IPV4";
    }

    private String getEthNetmask() {
        return SystemUtils.getMaskAddress(true);
    }

    private String getEthGateway() {//TODO
        CommonUtils.CommandResult result = CommonUtils.execCmd("ip route show ", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null ) {
                String[] list = address.split("\n");
                for (String item:list){
                    if (item != null && item.contains("eth0")) {
                        String[] gateway = item.split("/");
                        if (address != null && isIp(gateway[0])) {
                            return gateway[0];
                        }
                    }
                    if (item != null && item.contains("wlan0")) {
                        String[] gateway = item.split("/");
                    }
                }
            }
        }
        return null;
    }

    private String getEthGatewayDefault() {//TODO
        CommonUtils.CommandResult result = CommonUtils.execCmd("ip route list table 0", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null ) {
                String[] list = address.split("\n");
                for (String item:list){
                    if (item != null && item.contains("eth0") && item.contains("default via")) {
                        String[] gateway = item.split(" ");
                        if (isIp(gateway[2])) {
                            return gateway[2];
                        }
                    }
//                    if (item != null && item.contains("wlan0") && item.contains("default via")) {
//                        String[] gateway = item.split(" ");
//                    }
                }
            }
        }
        return null;
    }

    private static boolean isIp(String ip) {
        boolean b1 = ip.trim().matches("(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))");
        return b1;
    }

    private String getEthDns1(Context context) {
        if (getEthConnectStatus(context)) {
            return AdvancedOptionsFlowUtil.getProp("net.dns1");
        }
        return null;
    }

    private String getEthDns2(Context context) {
        if (getEthConnectStatus(context)) {
            return AdvancedOptionsFlowUtil.getProp("net.dns2");
        }
        return null;
    }


    @Override
    public WifiItemInfo getWifiItemInfo(Context context) {
        WifiItemInfo wifiItemInfo = new WifiItemInfo();
        wifiItemInfo.setConnected(getWifiConnectStatus());
        wifiItemInfo.setConnectMode(getWifiConnectMode(context.getApplicationContext()));
        wifiItemInfo.setIpMode(getWifiIpMode());
        wifiItemInfo.setNetmask(getWifiNetmask());
        wifiItemInfo.setGateway(getWifiGatewayDefault());
        wifiItemInfo.setDns1(getWifiDns1());
        wifiItemInfo.setDns2(getWifiDns2());
        wifiItemInfo.setBSSID(getWifiBSSID(context.getApplicationContext()));
        wifiItemInfo.setSSID(getWifiSSID(context.getApplicationContext()));
        wifiItemInfo.setLinkSpeed(getWifiLinkSpeed(context.getApplicationContext()));
        wifiItemInfo.setNetworkID(getCurrentWifiNetworkId(context.getApplicationContext()));
        wifiItemInfo.setSecurity(getWifiSecurity(context.getApplicationContext()));
        wifiItemInfo.setRSSID(getWifiRssid(context.getApplicationContext()));
        return wifiItemInfo;
    }

    @Override
    public WifiItemInfo getWifiItemInfoByType(Context context, int type) {

        WifiItemInfo wifiItemInfo = new WifiItemInfo();
        if (type == WifiItemInfo.CONNECTED){
            wifiItemInfo.setConnected(getWifiConnectStatus());
        } else if (type == WifiItemInfo.CONNECT_MODE) {
            wifiItemInfo.setConnectMode(getWifiConnectMode(context.getApplicationContext()));
        } else if (type == WifiItemInfo.IP_MODE) {
            wifiItemInfo.setIpMode(getWifiIpMode());
        } else if (type == WifiItemInfo.GATEWAY) {
            wifiItemInfo.setGateway(getWifiGatewayDefault());
        } else if (type == WifiItemInfo.NETMASK) {
            wifiItemInfo.setNetmask(getWifiNetmask());
        } else if (type == WifiItemInfo.DNS1) {
            wifiItemInfo.setDns1(getWifiDns1());
        } else if (type == WifiItemInfo.DNS2) {
            wifiItemInfo.setDns2(getWifiDns2());
        } else if (type == WifiItemInfo.LINK_SPEED) {
            wifiItemInfo.setLinkSpeed(getWifiLinkSpeed(context.getApplicationContext()));
        } else if (type == WifiItemInfo.WIFI_SSID) {
            wifiItemInfo.setSSID(getWifiSSID(context.getApplicationContext()));
        } else if (type == WifiItemInfo.WIFI_BSSID) {
            wifiItemInfo.setBSSID(getWifiBSSID(context.getApplicationContext()));
        } else if (type == WifiItemInfo.WIFI_RSSID) {
            wifiItemInfo.setRSSID(getWifiRssid(context.getApplicationContext()));
        } else if (type == WifiItemInfo.SECURITY) {
            wifiItemInfo.setSecurity(getWifiSecurity(context.getApplicationContext()));
        } else if (type == WifiItemInfo.NETWORK_ID) {
            wifiItemInfo.setNetworkID(getCurrentWifiNetworkId(context.getApplicationContext()));
        }
        Log.d(TAG, type+"getWifiItemInfoByType: "+wifiItemInfo.toString());
        return wifiItemInfo;
    }

    private String getWifiRssid(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            return info.getRssi()+"";
        }
        return null;
    }

    private boolean getWifiConnectStatus() {
        String result = NonSystemUtils.exeShellCmd("cat /sys/class/net/wlan0/carrier");
        Log.d(TAG, "getWifiConnectStatus: "+result.contains("1"));
        return result.contains("1");
    }

    private String getWifiConnectMode(Context context) {
        if (AdvancedOptionsFlowUtil.isIpStatic(context,false,getCurrentWifiNetworkId(context))){
            Log.d(TAG, "getWifiConnectMode: manual");
            return "Manual";
        } else {
            Log.d(TAG, "getWifiConnectMode: DHCP ");
            return "DHCP";
        }
    }

    private String getWifiIpMode() {
        Log.d(TAG, "getWifiIpMode: IPV4");
        return "IPV4";
    }

    private String getWifiNetmask() {
        return SystemUtils.getMaskAddress(false);
    }

    private String getWifiGateway() {
        CommonUtils.CommandResult result = CommonUtils.execCmd("ip route show ", false);
        Log.d(TAG, "getWifiGateway: "+result.result);
        Log.d(TAG, "getWifiGateway: success "+result.successMsg);
        Log.d(TAG, "getWifiGateway: error "+result.errorMsg);

        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null ) {
                String[] list = address.split("\n");
                for (String item:list){
                    if (item != null && item.contains("wlan0")) {
                        String[] gateway = item.split("/");
                        Log.d(TAG, "getWifiGateway:gateway "+gateway[0]);
                        if (isIp(gateway[0])) {
                            Log.d(TAG, "getWifiGateway: isIP:"+gateway[0]);
                            return gateway[0];
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getWifiGatewayDefault() {
        CommonUtils.CommandResult result = CommonUtils.execCmd("ip route list table 0", false);
        Log.d(TAG, "getWifiGateway: "+result.result);
        Log.d(TAG, "getWifiGateway: success "+result.successMsg);
        Log.d(TAG, "getWifiGateway: error "+result.errorMsg);

        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null ) {
                String[] list = address.split("\n");
                for (String item:list){
                    if (item != null && item.contains("wlan0") && item.contains("default via")) {
                        String[] gateway = item.split(" ");
                        Log.d(TAG, "getWifiGateway:gateway "+gateway[2]);
                        if (isIp(gateway[2])) {
                            Log.d(TAG, "getWifiGateway: isIP:"+gateway[0]);
                            return gateway[0];
                        }
                    }
                }
            }
        }
        return null;
    }

    private String getWifiDns1() {
        if (getWifiConnectStatus()) {
            return AdvancedOptionsFlowUtil.getProp("net.dns1");
        }
        return null;
    }

    private String getWifiDns2() {
        if (getWifiConnectStatus()) {
            return AdvancedOptionsFlowUtil.getProp("net.dns2");
        }
        return null;
    }

    private String getWifiLinkSpeed(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            int speed = wifiInfo.getLinkSpeed();
            // 链接速度单位
            String units = WifiInfo.LINK_SPEED_UNITS;
            // 链接信号强度
            int strength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
            return  speed + units;
        }
        return null;
    }

    private String getWifiSSID(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }
        }
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            return info.getSSID();
        }
        return null;
    }

    private String getWifiBSSID(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            return info.getBSSID();
        }
        return null;
    }

    public String getWifiSecurity(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }
        }
        List<WifiConfiguration> wifiConfigList ;
        try {
            wifiConfigList = wifiManager.getConfiguredNetworks();
        } catch (SecurityException se){
            return null;
        }
        for (WifiConfiguration wifiConfiguration : wifiConfigList) {
            if (getWifiSSID(context) !=null && wifiConfiguration.SSID !=null &&
                    wifiConfiguration.SSID.contains(getWifiSSID(context))){
                if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
                    return "WPA_PSK";
                }
                if (wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
                    return "WPA_EAP";
                }
                return (wifiConfiguration.wepKeys[0] != null) ? "WEP" : "NONE";
            }
        }
        return null;
    }

    private int getCurrentWifiNetworkId(Context context){
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return -1;
            }
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            return info.getNetworkId();
        }
        return -1;
    }


    @Override
    public boolean setNetworkStaticIpInfo(Context context, NetworkStaticIpInfo staticIpInfo) {
        int ret ;
        if (staticIpInfo.isEthernetMode()){
            if (staticIpInfo.isDHCP()){
                ret = AdvancedOptionsFlowUtil.processIpSettings(context.getApplicationContext(),true,false,staticIpInfo.getIpv4Address()
                        ,getStrCount(staticIpInfo.getNetmask(),"255")*8,staticIpInfo.getGateway(),staticIpInfo.getDns1(),staticIpInfo.getDns2());
            } else {
                ret = AdvancedOptionsFlowUtil.processIpSettings(context.getApplicationContext(),true,true,staticIpInfo.getIpv4Address()
                        ,getStrCount(staticIpInfo.getNetmask(),"255")*8,staticIpInfo.getGateway(),staticIpInfo.getDns1(),staticIpInfo.getDns2());
            }
        } else {
            if (staticIpInfo.isDHCP()){
                ret = AdvancedOptionsFlowUtil.processWifiIpSettings(context.getApplicationContext(),staticIpInfo.getNetworkId(),false,staticIpInfo.getIpv4Address()
                        ,getStrCount(staticIpInfo.getNetmask(),"255")*8,staticIpInfo.getGateway(),staticIpInfo.getDns1(),staticIpInfo.getDns2());
            } else {
                ret = AdvancedOptionsFlowUtil.processWifiIpSettings(context.getApplicationContext(),staticIpInfo.getNetworkId(),true,staticIpInfo.getIpv4Address()
                        ,getStrCount(staticIpInfo.getNetmask(),"255")*8,staticIpInfo.getGateway(),staticIpInfo.getDns1(),staticIpInfo.getDns2());
            }
        }
        return ret == 0;
    }


    private static int getStrCount(String string, String subString) {
        int index = 0;
        int count = 0;
        if (string == null || string.isEmpty()){
            string = "255.255.255.0";
        }
        if (subString == null || subString.isEmpty()){
            string = "255";
        }
        while ((index = string.indexOf(subString, index)) != -1) {
//在循环控制的条件中将获得的索引值赋给index,不等于-1是因为.在JDK中规定了indexOf查找不到子字符串时就返回-1.在我们这里也就是跳出循环的意思
            index++;//得到索引后,从本位置开始进行下一次循环,所以字符串的索引加一
            count++;//计数器统计出现的次数
        }
        return count;
    }

        @Override
    public boolean getWifiEnabled(Context context) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return false;
            }
        }
        return wifiManager.isWifiEnabled();
    }

    @Override
    public boolean setWifiEnabled(Context context,boolean enable) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            if (wifiManager == null) {
                return false;
            }
        }
        return wifiManager.setWifiEnabled(enable);
    }

    @Override
    public boolean resetWifi(Context context) {
        new HsSystemApi().resetWifi(context.getApplicationContext());
        return true;
    }

    @Override
    public boolean disconnectWifi(Context context) {
        int networkId = getCurrentWifiNetworkId(context.getApplicationContext());
        if (networkId != -1) {
            new HsSystemApi().wifiForgetNetwork(context.getApplicationContext(),networkId);
        }
        return true;
    }

}
