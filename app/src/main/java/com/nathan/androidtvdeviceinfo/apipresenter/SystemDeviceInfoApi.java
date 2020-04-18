package com.nathan.androidtvdeviceinfo.apipresenter;

public interface SystemDeviceInfoApi {

    /**
     * @return unit KHZ
     */
    String getMaxCpuFreq();

    /**
     * @return KHZ
     */
    String getMinCpuFreq();

    /**
     * @param cpuNum from 0 to cpuMax
     * @return
     */
    String getCurCpuFreq(int cpuNum);

    /**
     * @return TODO
     */
    String getCpuName();

    /**
     * @return ro.serialno
     */
    String getSerialNo();

    /**
     * @return IPV4
     */
    String getEthernetMacAddress();

    /**
     * @return IPV4 such as 00:10:20:30:e2:d9
     */
    String getWifiMacAddress();

    /**
     * @return
     */
    String getEthIpAddress();

    /**
     * @return such as 192.168.xxx.xxx
     */
    String getWifiIpAddress();

    /**
     * @return from xxx to yyy
     */
    String getGpuFreqRange();

    /**
     * @return * [0][0]==eth0 receive unit KB
     *      * [0][1] == eth0 send
     *      * [1][0] == wifi receive
     *      * [1][1] == wifi send
     */
    String[][] getAllNetData();
}
