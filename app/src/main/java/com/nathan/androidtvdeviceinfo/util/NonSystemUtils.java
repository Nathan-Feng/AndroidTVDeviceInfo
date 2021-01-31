package com.nathan.androidtvdeviceinfo.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 不需要签名就可以调用的工具类。
 *
 * @author zyf
 */
@SuppressWarnings("all")
public class NonSystemUtils {

    private static final String LINE_SEP = CommonUtils.LINE_SEP;

    /**
     * @return cpu 信息
     */
    public static int getCpuNumber(){
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * @return cpu 详细信息如下
     */
    public static String getCpuInfo() {
        ProcessBuilder processBuilder;
        String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
        InputStream inputStream;
        Process process;
        byte[] byteArry;
        String cpuInfo = "";
        byteArry = new byte[1024];
        try {
            processBuilder = new ProcessBuilder(DATA);
            process = processBuilder.start();
            inputStream = process.getInputStream();
            while (inputStream.read(byteArry) != -1) {
                cpuInfo = cpuInfo + new String(byteArry);
            }
            inputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cpuInfo ;
    }


    /**
     * 获取内存信息。
     *
     * @param context 上下文
     * @return 内存信息
     */
    public static String getFreeMemInfo(Context context) {
        ActivityManager.MemoryInfo memoryInfo = getMemInfo(context);

        String strMemInfo =CommonUtils.getFileSizeDescription(memoryInfo.availMem);// API 16

        return strMemInfo;
    }

    public static String getTotalMemInfo(Context context) {

        ActivityManager.MemoryInfo memoryInfo = getMemInfo(context);

        String strMemInfo =CommonUtils.getFileSizeDescription(memoryInfo.totalMem);

        return strMemInfo;
    }

    public static String getMemUsePercent(Context context){
        long totalMem = getMemInfo(context).totalMem;
        long availMem = getMemInfo(context).availMem;
        long ret = (availMem*100/totalMem);
        Log.d("zyf", "getMemUsePercent:totalMem: "+totalMem+" aviMem:"+availMem+" ret:"+ret);
        return (availMem*100/totalMem)+"%";
    }

    private static ActivityManager.MemoryInfo getMemInfo(Context context){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }


    /**
     * 获取系统相关的信息。
     *
     * @return 系统信息。
     */
    public static String getSysVersionInfo(Context context) {
        String sysVersionInfo = "SERIAL: " + "\n" +
                "品牌: " + Build.BRAND + "\n" +
                "型号: " + Build.MODEL + "\n" +
                "SDK:  " + Build.VERSION.SDK + "\n" +
                "屏幕分辨率: " + getScreenWidth(context) + "*" + getScreenHeight(context) + "\n" +
                "DPI: " + getScreenDensityDpi() + "\n" +
                "系统版本: Android " + Build.VERSION.RELEASE + "\n";
        return sysVersionInfo;
    }



    /**
     * Return the width of screen, in pixel.
     *
     * @return the width of screen, in pixel
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    /**
     * Return the height of screen, in pixel.
     *
     * @return the height of screen, in pixel
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }

    /**
     * Return the density of screen.
     *
     * @return the density of screen
     */
    public static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Return the screen density expressed as dots-per-inch.
     *
     * @return the screen density expressed as dots-per-inch
     */
    public static int getScreenDensityDpi() {
        return Resources.getSystem().getDisplayMetrics().densityDpi;
    }


    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取总空间大小
     *
     * @return 总大小，字节为单位
     */
    public static String getTotalExternalFlashSize() {
        if (isSDCardEnable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return CommonUtils.getFileSizeDescription(totalBlocks * blockSize) ;
        } else {
            return null;
        }
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getAvailableExternalFlashSize() {
        if (isSDCardEnable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize, availableBlocks;
            availableBlocks = stat.getAvailableBlocksLong();
            blockSize = stat.getBlockSizeLong();
            return CommonUtils.getFileSizeDescription(availableBlocks * blockSize) ;
        } else {
            return null;
        }
    }

    public static String getRateExternalFlashSize() {
        Log.d("zyf", "getRateExternalFlashSize: enter");
        if (isSDCardEnable()) {
            Log.d("zyf", "getRateExternalFlashSize: sdcard enable");
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long  availableBlocks;
            availableBlocks = stat.getAvailableBlocksLong();
            //blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCount();
            double rate = 1.0-((double)availableBlocks/totalBlocks);
            DecimalFormat df = new DecimalFormat("0.00%");
            String ret = df.format(rate);
//            String percent = String.format("%.2f",rate);
//            int ret = (int)Long.parseLong(percent)*100;
            Log.d("zyf", "getRateExternalFlashSize: sdcard ret:"+ret);
            return ret;
        } else {
            Log.d("zyf", "getRateExternalFlashSize: sdcard not enable");
            return null;
        }
    }

    public static String getUsedExternalFlashSize() {
        if (isSDCardEnable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long  blockSize, availableBlocks;
            availableBlocks = stat.getAvailableBlocksLong();
            blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCount();
            return CommonUtils.getFileSizeDescription((totalBlocks-availableBlocks) * blockSize) ;
        } else {
            return null;
        }
    }


    public static String exeShellCmd(String cmd){
        CommonUtils.CommandResult result = CommonUtils.execCmd(cmd.trim(), false);
        String name = "N/A";
        if (result.result == 0) {
            name = result.successMsg;
        } else {
            name = result.errorMsg;
        }
        return name;
    }

    /**
     * @return getprop ,数量有限，一些属性需要系统签名才能获取。
     * 注意：如何直接打印返回值会出现log不全的情况，请分段打印，logcat有容量限制：4*1024
     */
    public static String getProp(){
        CommonUtils.CommandResult result = CommonUtils.execCmd("getprop", false);
        String name = null;
        if (result.result == 0) {
            name = result.successMsg;
        }
        return name;
    }



    /**
     * @param context 查看当前有线加wifi的所有流量合，单位为KB
     *                如果想单独了解有线和wifi各自的流量，请查看其它API
     * @return
     */
    public static long getTotalNetworkRxBytes(Context context) {
        int uid = getApplicationUid(context,"");
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    /**
     * @param context 获取app的uid，如果包名为空，默认返回context所在的app的uid
     * @param packageName 应用的包名，不要留空格
     * @return
     */
    private static int getApplicationUid(Context context,String packageName){
        if (packageName == null || packageName.isEmpty()){
            return context.getApplicationInfo().uid;
        }
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName.trim(), PackageManager.GET_ACTIVITIES);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(int dp, Context context){
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(int px, Context context){
        return px / (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}

