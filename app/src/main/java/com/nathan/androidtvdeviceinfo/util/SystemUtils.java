package com.nathan.androidtvdeviceinfo.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.nathan.androidtvdeviceinfo.util.CommonUtils.execCmd;


/**
 * 需要系统权限才能调用的工具类。
 *
 * @author zyf
 */
@SuppressWarnings("all")
public class SystemUtils {

    private static final String LINE_SEP = CommonUtils.LINE_SEP;

    /**
     * @return // 获取CPU最大频率（单位KHZ）
     *     // "/system/bin/cat" 命令行
     *     // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
     */
    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    // 获取CPU最小频率（单位KHZ）
    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    // 实时获取CPU当前频率（单位KHZ）
    public static String getCurCpuFreq(int cpuNum) {
        String result = "N/A";
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu"+cpuNum+"/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @return 还有些问题，待定
     */
    // 获取CPU名字
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @return need System permission and modify selinux
     */
    public static String getSerialNo(){
        Log.i("zyf","*&********get serial start**********");
        CommonUtils.CommandResult result = execCmd("getprop ro.serialno", false);
        String name = "not";
        if (result.result == 0) {
            name = result.successMsg;
            Log.i("zyf","*&*******aaaaa***********"+name);
        }
        Log.i("zyf","*&********get serial start end**********"+name);
        return name;
    }

    /**
     * @return need setenforce 0
     */
    public static String getEthernetMacAddress() {
        CommonUtils.CommandResult result = execCmd("cat /sys/class/net/" + "eth0" + "/address", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && address.length() > 0) {
                return address;
            }
        }
        return "N/A";
    }

    public static String getWifiMacAddress() {
        CommonUtils.CommandResult result = execCmd("cat /sys/class/net/" + "wlan0" + "/address", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && address.length() > 0) {
                return address;
            }
        }
        return "N/A";
    }

    public static String getEthIpAddress() {
        CommonUtils.CommandResult result = execCmd("ifconfig eth0|sed -n '2p'|cut -c21-34", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && address.length() > 0) {
                return address;
            }
        }
        return "N/A";
    }

    public static String getWifiIpAddress() {
        CommonUtils.CommandResult result = execCmd("ifconfig wlan0|sed -n '2p'|cut -c21-34", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && address.length() > 0) {
                return address;
            }
        }
        return "N/A";
    }

    /**
     * @return need setenforce 0
     */
    ///sys/class/devfreq/devfreq0/available_frequencies
    public static String getGpuFreqRange() {
        CommonUtils.CommandResult result = execCmd("cat /sys/class/devfreq/devfreq0/available_frequencies", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && address.length() > 0) {
                return address;
            }
        }
        return "N/A";
    }


    private static String getNetSpeedByCmd() {
        CommonUtils.CommandResult result = execCmd("cat /proc/self/net/dev", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null && name.length() > 0) {
                return name;
            }
        }
        return "N/A";
    }

    /**
     * @return
     * [0][0]==eth0 receive
     * [0][1] == eth0 send
     * [1][0] == wifi receive
     * [1][1] == wifi send
     */
    public static String[][] getAllNetData(){
        String ret = getNetSpeedByCmd();
        InputStreamReader isr= new InputStreamReader(
                new ByteArrayInputStream(ret.getBytes(Charset.forName("utf8"))),
                StandardCharsets.UTF_8);
        BufferedReader bufr = new BufferedReader(isr, 500);
        String line;
        String[] data_temp;
        String[] netData;
        int k=0;
        int j=0;
        // 读取文件，并对读取到的文件进行操作
        try {
            while ((line = bufr.readLine()) != null) {
                data_temp = line.trim().split(":");
                if (line.contains(ETHLINE)) {
                    netData = data_temp[1].trim().split(" ");

                    for (k = 0, j = 0; k < netData.length; k++) {
                        if (netData[k].length() > 0) {
                            ethData[j] = netData[k];
                            j++;
                        }
                    }
                    //Log.i("zyf","zyf:eth re:"+ethData[0]+" tr:"+ethData[8]);
                    allData[0][0] = ethData[0];
                    allData[0][1] = ethData[8];
                } else if (line.contains(WIFILINE)) {
                    netData = data_temp[1].trim().split(" ");
                    for (k = 0, j = 0; k < netData.length; k++) {
                        if (netData[k].length() > 0) {
                            wifiData[j] = netData[k];
                            j++;
                        }
                    }
                    //Log.i("zyf","zyf:wifi re:"+wifiData[0]+" tr:"+wifiData[8]);
                    allData[1][0] = wifiData[0];
                    allData[1][1] = wifiData[8];
                }
            }
            isr.close();
            bufr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allData;
    }

    // 系统流量文件
    private static String DEV_FILE = "/proc/self/net/dev";
    // 以太网
    private static final String ETHLINE = "eth0";
    // wifi
    private static final String WIFILINE = "wlan0";
    //total 15 line:
    private static String[] ethData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0" };
    private static String[] wifiData = { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
            "0", "0", "0", "0", "0", "0" };
    private static String allData[][] = new String[][]{{"0","0"},{"0","0"}};

}

