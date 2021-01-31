package com.nathan.androidtvdeviceinfo.util;

import android.os.Build;
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
            result = null;
        }
        if (result == null){
            return null;
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
            result = null;
        }
        if (result == null){
            return null;
        }
        return result.trim();
    }

    // 实时获取CPU当前频率（单位KHZ）
    public static String getCurCpuFreq(int cpuNum) {
        String result = null;
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

    public static String getCurCpuRate(int cpuNum){
        String curFreq = getCurCpuFreq(cpuNum);
        String minFreq = getMinCpuFreq();
        String maxFreq = getMaxCpuFreq();
        long curFreqNum = Integer.parseInt(curFreq.substring(0,curFreq.length()-3));
        long minFreqNum = Integer.parseInt(minFreq.substring(0,minFreq.length()-3));
        long maxFreqNum = Integer.parseInt(maxFreq.substring(0,maxFreq.length()-3));
        Log.d("zyf", "getCurCpuRate: curFreq:"+curFreq+ "substring:"+curFreq.substring(0,curFreq.length()-3));
        Log.d("zyf", "getCurCpuRate: minFreq:"+minFreq+" subString :"+minFreq.substring(0,minFreq.length()-3));
        Log.d("zyf", "getCurCpuRate: maxFreq:"+maxFreq+" substring:"+maxFreq.substring(0,maxFreq.length()-3));
        Log.d("zyf", "getCurCpuRate: curFreqNum:"+curFreqNum);

        Log.d("zyf", "getCurCpuRate: rate:"+(curFreqNum - minFreqNum)*100/maxFreqNum);
        return (curFreqNum - minFreqNum)*100/maxFreqNum+"%";
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
     * need this permission:Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     */
    public static String getSerialNo(){
//        Log.i("zyf","*&********get serial start**********");
//        CommonUtils.CommandResult result = execCmd("getprop ro.serialno", false);
//        String name = "not";
//        if (result.result == 0) {
//            name = result.successMsg;
//            Log.i("zyf","*&*******aaaaa***********"+name);
//        }
//        Log.i("zyf","*&********get serial start end**********"+name);
//        return name;
        Log.i("zyf","*&********get serial start**********"+getSerialNoByBuild());
        return getSerialNoByBuild();
    }

    private static String getSerialNoByBuild(){
        return Build.getSerial();
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
        return null;
    }

    public static String getWifiMacAddress() {
        CommonUtils.CommandResult result = execCmd("cat /sys/class/net/" + "wlan0" + "/address", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && address.length() > 0 && address.length() <18) {
                Log.d("zyf", "getWifiMacAddress orig: "+address);
                return address;
            }
        }
        return null;
    }

    public static String getEthIpAddress() {
        CommonUtils.CommandResult result = CommonUtils.execCmd("ifconfig eth0|grep 'inet addr'|cut -d \":\" -f2|cut -d \" \" -f1", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && isIp(address)) {
                Log.d("zyf", "getEthIpAddress: "+address);
                return address;
            }
        }
        return null;
    }
    public static String getGatewayAddress() {
//        CommonUtils.CommandResult result = CommonUtils.execCmd("ip route show |cut -d \"/\" -f1", false);
//        if (result.result == 0) {
//            String address = result.successMsg;
//            if (address != null && isIp(address)) {
//                Log.d("zyf", "getEthGatewayAddress: "+address);
//                return address;
//            }
//        }
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

    public static String getBcastAddress(boolean ethernet) {
        String type = ethernet ==true?"eth0":"wlan0";
        CommonUtils.CommandResult result = CommonUtils.execCmd("ifconfig "+type+"|grep 'inet addr'|cut -d \":\" -f3|cut -d \" \" -f1", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && isIp(address)) {
                Log.d("zyf", "getEthBcastAddress: "+address);
                return address;
            }
        }
        return null;
    }

    public static String getMaskAddress(boolean ethernet) {
        String type = ethernet ==true?"eth0":"wlan0";
        CommonUtils.CommandResult result = CommonUtils.execCmd("ifconfig "+type+"|grep 'inet addr'|cut -d \":\" -f4|cut -d \" \" -f1", false);
        Log.d("zyf", "getMaskAddress: result:"+result.result);
        Log.d("zyf", "getMaskAddress: result success:"+result.successMsg);
        Log.d("zyf", "getMaskAddress: result error:"+result.errorMsg);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && isIp(address)) {
                Log.d("zyf", "getEthMaskAddress: "+address);
                return address;
            }
        }
        return null;
    }

    public static String getWifiIpAddress() {
        CommonUtils.CommandResult result = CommonUtils.execCmd("ifconfig wlan0|grep 'inet addr'|cut -d \":\" -f2|cut -d \" \" -f1", false);
        if (result.result == 0) {
            String address = result.successMsg;
            if (address != null && isIp(address)) {
                Log.d("zyf", "getWifiIpAddress: "+address);
                return address;
            }
        }
        return null;
    }

    private static boolean isIp(String ip) {
        boolean b1 = ip.trim().matches("(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))");
        Log.d("zyf", "v4: b1 " + b1);
        return b1;
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
        return null;
    }

    public String HS_GetCpuTemp() {
        String cmd = "cat /sys/class/thermal/thermal_zone0/temp";
        String temp =  NonSystemUtils.exeShellCmd(cmd);
        if (temp.contains("N/A") || temp.contains("")){
            return "-1";
        }
        return Integer.parseInt(temp)/1000.0+"";
    }


    private static String getNetSpeedByCmd() {
        CommonUtils.CommandResult result = execCmd("cat /proc/self/net/dev", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null && name.length() > 0) {
                return name;
            }
        }
        return null;
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

