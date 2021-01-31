package com.nathan.androidtvdeviceinfo.apipresenter.model;

public final class WifiItemInfo {

    public static final int CONNECTED = 0;

    public static final int CONNECT_MODE = 1;

    public static final int IP_MODE = 2;

    public static final int NETMASK = 3;

    public static final int GATEWAY = 4;

    public static final int DNS1 = 5;

    public static final int DNS2 = 6;

    public static final int LINK_SPEED = 7;

    public static final int WIFI_SSID = 8;

    public static final int WIFI_BSSID = 9;

    public static final int WIFI_RSSID = 10;

    public static final int SECURITY = 11;

    public static final int NETWORK_ID = 12;

    private boolean connected;

    private String connectMode;

    private String ipMode;

    private String netmask;

    private String gateway;

    private String dns1;

    private String dns2;

    private String linkSpeed;

    private String SSID;

    private String BSSID;

    private String RSSID;

    private String security;

    private int networkID;




    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getConnectMode() {
        return connectMode;
    }

    public void setConnectMode(String connectMode) {
        this.connectMode = connectMode;
    }

    public String getIpMode() {
        return ipMode;
    }

    public void setIpMode(String ipMode) {
        this.ipMode = ipMode;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }


    public String getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(String linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public int getNetworkID() {
        return networkID;
    }

    public void setNetworkID(int networkID) {
        this.networkID = networkID;
    }


    public String getRSSID() {
        return RSSID;
    }

    public void setRSSID(String RSSID) {
        this.RSSID = RSSID;
    }

    @Override
    public String toString() {
        return "WifiItemInfo{" +
                "connected=" + connected +
                ", connectMode='" + connectMode + '\'' +
                ", ipMode='" + ipMode + '\'' +
                ", netmask='" + netmask + '\'' +
                ", gateway='" + gateway + '\'' +
                ", dns1='" + dns1 + '\'' +
                ", dns2='" + dns2 + '\'' +
                ", linkSpeed='" + linkSpeed + '\'' +
                ", SSID='" + SSID + '\'' +
                ", BSSID='" + BSSID + '\'' +
                ", RSSID='" + RSSID + '\'' +
                ", security='" + security + '\'' +
                ", networkID=" + networkID +
                '}';
    }
}
