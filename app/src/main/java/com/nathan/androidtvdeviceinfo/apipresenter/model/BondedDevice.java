package com.nathan.androidtvdeviceinfo.apipresenter.model;

public class BondedDevice {

    public static final int DEVICE_TYPE_CLASSIC = 1;
    public static final int DEVICE_TYPE_LE = 2;
    public static final int DEVICE_TYPE_DUAL = 3;
    public static final int DEVICE_TYPE_UNKNOWN = 0;

    public static final int BOND_NONE = 10;
    public static final int BOND_BONDING = 11;
    public static final int BOND_BONDED = 12;


    private String name;
    private String macAddress;
    private boolean connectStatus;
    private String bondState;
    private String type;

    public BondedDevice() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public boolean getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(boolean connectStatus) {
        this.connectStatus = connectStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBondState() {
        return bondState;
    }

    public void setBondState(String bondState) {
        this.bondState = bondState;
    }

    @Override
    public String toString() {
        return "BondedDevice{" +
                "name='" + name + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", connectStatus=" + connectStatus +
                ", bondState='" + bondState + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
