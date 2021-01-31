package com.nathan.androidtvdeviceinfo.apipresenter;

import com.nathan.androidtvdeviceinfo.apipresenter.model.BondedDevice;

import java.util.List;

public interface BluetoothDeviceInfoApi {
    /**
     * @return
     */
    List<BondedDevice> getBtBondedList();

    String getDeviceBtName();

    String getDeviceBtMac();

    boolean getBtEnabled();

    boolean setBtEnabled(boolean enable);

    boolean resetBt();

}
