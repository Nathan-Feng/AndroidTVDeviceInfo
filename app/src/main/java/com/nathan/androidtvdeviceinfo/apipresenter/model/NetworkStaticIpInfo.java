package com.nathan.androidtvdeviceinfo.apipresenter.model;

public final class NetworkStaticIpInfo {

    private boolean isEthernetMode;

    private boolean isDHCP;

    private String connectMode;

    private String ipv4Address;

    private String netmask;

    private String gateway;

    private String dns1;

    private String dns2;

    private int networkId;

    public boolean isEthernetMode() {
        return isEthernetMode;
    }

    public void setEthernetMode(boolean ethernetMode) {
        isEthernetMode = ethernetMode;
    }

    public boolean isDHCP() {
        return isDHCP;
    }

    public void setDHCP(boolean DHCP) {
        isDHCP = DHCP;
    }

    public String getConnectMode() {
        return connectMode;
    }

    public void setConnectMode(String connectMode) {
        this.connectMode = connectMode;
    }


    public String getIpv4Address() {
        return ipv4Address;
    }

    public void setIpv4Address(String ipAddress) {
        this.ipv4Address = ipAddress;
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

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @Override
    public String toString() {
        return "NetworkStaticIpInfo{" +
                "isEthernetMode=" + isEthernetMode +
                ", isDHCP=" + isDHCP +
                ", connectMode='" + connectMode + '\'' +
                ", ipv4Address='" + ipv4Address + '\'' +
                ", netmask='" + netmask + '\'' +
                ", gateway='" + gateway + '\'' +
                ", dns1='" + dns1 + '\'' +
                ", dns2='" + dns2 + '\'' +
                ", networkId=" + networkId +
                '}';
    }
}
