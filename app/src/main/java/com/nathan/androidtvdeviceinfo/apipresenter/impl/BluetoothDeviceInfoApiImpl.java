package com.nathan.androidtvdeviceinfo.apipresenter.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.android.tv.settings.connectivity.HsSystemApi;
import com.nathan.androidtvdeviceinfo.apipresenter.BluetoothDeviceInfoApi;
import com.nathan.androidtvdeviceinfo.apipresenter.model.BTScanList;
import com.nathan.androidtvdeviceinfo.apipresenter.model.BondedDevice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class BluetoothDeviceInfoApiImpl implements BluetoothDeviceInfoApi {

    private static final String TAG = "BluetoothDeviceInfoApi";
    private BluetoothAdapter mBTAdapter = null;

    @Override
    public List<BondedDevice> getBtBondedList() {
        if (null == mBTAdapter) {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        List<BondedDevice> btBondedList = new ArrayList<>(8);
        Set<BluetoothDevice> btList = mBTAdapter.getBondedDevices();
        Iterator iterator = btList.iterator();
        while (iterator.hasNext()) {
            BluetoothDevice device = (BluetoothDevice) iterator.next();
            BondedDevice item = new BondedDevice();
            item.setName(device.getName());
            item.setMacAddress(device.getAddress());
            item.setConnectStatus(new HsSystemApi().checkBtStatus(device));
            item.setBondState(checkBondStatus(device.getBondState()));
            item.setType(checkType(device.getType()));
            btBondedList.add(item);
        }
        Log.d(TAG, "getBtBondedList size:"+btBondedList.size());
        if (btBondedList.size() > 0){
            for (int i=0 ; i<btBondedList.size();i++){
                Log.d(TAG, i+" getBtBondedList item:"+btBondedList.get(i));
            }
        }
        return btBondedList;
    }

    @Override
    public String getDeviceBtMac() {
        if (null == mBTAdapter) {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        Log.d(TAG, "getDeviceBtMac: "+(mBTAdapter.isEnabled()?mBTAdapter.getAddress():null));
        return mBTAdapter.isEnabled()?mBTAdapter.getAddress():null;
    }

    @Override
    public String getDeviceBtName() {
        if (null == mBTAdapter) {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        Log.d(TAG, "getDeviceBtName: "+(mBTAdapter.isEnabled()?mBTAdapter.getName():null));
        return mBTAdapter.isEnabled()?mBTAdapter.getName():null;
    }

    private String checkBondStatus(int status){
        switch (status){
            case BondedDevice.BOND_BONDING:
                return "BOND_BONDING";
            case BondedDevice.BOND_BONDED:
                return "BOND_BONDED";
            default:
                return "BOND_NONE";
        }
    }

    private String checkType(int type){
        switch (type){
            case BondedDevice.DEVICE_TYPE_CLASSIC:
                return "DEVICE_TYPE_CLASSIC";
            case BondedDevice.DEVICE_TYPE_DUAL:
                return "DEVICE_TYPE_DUAL";
            case BondedDevice.DEVICE_TYPE_LE:
                return "DEVICE_TYPE_LE";
            default:
                return "DEVICE_TYPE_UNKNOWN";
        }
    }



    /**
     * ****************************************** BT START******************************************************
     */
    private BluetoothCallback mBluetoothCallback;
    public interface BluetoothCallback{
        void btScanListCallback(List<BTScanList> btScanLists);
    }

    public void setBTCallback(BluetoothCallback btCallback){
        this.mBluetoothCallback = btCallback;
    }

    @Override
    public boolean getBtEnabled() {
        if (null == mBTAdapter) {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        boolean flag = mBTAdapter.isEnabled();
        Log.i(TAG, "HS_GetBTEnabled return " + flag);
        return flag;
    }

    @Override
    public boolean setBtEnabled(boolean enable) {
        boolean ret = false;
        if (null == mBTAdapter) {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (enable) {
            ret = mBTAdapter.enable();
        } else {
            ret = mBTAdapter.disable();
        }
        Log.i(TAG, "HS_SetBTEnabled return " + ret);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private final BroadcastReceiver searchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            Log.v(TAG, "action : " + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (null != device) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null){
                        int rssi = bundle.getShort(BluetoothDevice.EXTRA_RSSI);//
                        String name = device.getName();
                        Log.v(TAG, "Name : " + name + " Address: " + device.getAddress() + " Rssi: " + rssi);
                    }

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i(TAG, "---ACTION_DISCOVERY_STARTED------");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "---ACTION_DISCOVERY_FINISHED------");
//                if (mBluetoothCallback!= null){
//                    mBluetoothCallback.btScanListCallback(mBTScanList);
//                }
                if (null != mBTAdapter) {
                    mBTAdapter.cancelDiscovery();//
                }
//                mContext.unregisterReceiver(searchReceiver);
            }
        }
    };

    public List<BTScanList> HS_StartBTScan(int timeOut) {
        if (null == mBTAdapter) {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBTAdapter.getState() != BluetoothAdapter.STATE_ON) {
            mBTAdapter.enable();
        }
        //
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);//
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//
//        mContext.registerReceiver(searchReceiver, intent);


        long startScanTime = System.currentTimeMillis();
//        mStopBTScanFlag = false;
//        mBTScanList = new ArrayList<BTScanList>();
        if (mBTAdapter.startDiscovery()) {
            Log.d(TAG, "---BT startDiscovery ok----");
        } else {
            Log.d(TAG, "---BT startDiscovery fail----");
        }
//        if (null == mBTScanList || 0 == mBTScanList.size()) {
//            Log.i(TAG, "HS_GetBTScanList return null");
//            //mContext.unregisterReceiver(searchReceiver);
//            return null;
//        } else {
//            //mContext.unregisterReceiver(searchReceiver);
//            return mBTScanList;
//        }
        return null;
    }


    public void HS_StopBTScan() {
        mBluetoothCallback = null;
    }

    /**
     * @return getAddress if not system app ,will only get 20:00:00:00
     *
     */
    public String HS_GetBTMac() {
        if (null == mBTAdapter) {
            mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBTAdapter != null) {
            String address =  mBTAdapter.isEnabled()?mBTAdapter.getAddress():null;
            return address;
        }
        return null;
    }
    /**
     * ****************************************** BT END ******************************************************
     */

    @Override
    public boolean resetBt() {
        return new HsSystemApi().resetBt();
    }
}
