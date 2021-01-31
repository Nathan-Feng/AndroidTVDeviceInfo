package com.nathan.androidtvdeviceinfo.storage;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoyufeng3
 */
public class StorageManagerRepositoryImpl {

    private static final int TYPE_SDCARD = 0;
    private static final int TYPE_USB = 1;
    private Context mContext;
//    private List<DiskInfo> mDiskList = null;
    private StorageReceiver mRecevier = null;
    private String SDCARD_PATH;
    private Map<String,String> mUsbMaps = new IdentityHashMap<>(16);
    List<StorageDModel> mUsbList = new ArrayList<>();
    //private Callback mCallback;
    private List<Callback> mCallbackList = new ArrayList<>(16);
    private String usbPath;
    private static String pvrUsbPath;

    interface Callback{
        void UsbListUpdate(List<StorageDModel> usbList);
        void UsbPlugIn(StorageDModel usbPlugIn);
        void UsbPlugOut(StorageDModel usbPlugOut);
    }

    public void setOnUsbListUpdateCallbackListener(Callback callback){
        //this.mCallback = callback;
        if (EmptyTool.isNotEmpty(callback)){
            mCallbackList.add(callback);
        }
    }

    public void detachUsbListUpdateCallbackListener(Callback callback) {
        if (EmptyTool.isNotEmpty(callback)){
            mCallbackList.remove(callback);
        }
    }

    public void deInit(){
        mContext.unregisterReceiver(mRecevier);
        mContext = null;
        mRecevier = null;
        mUsbList = null;
        mCallbackList = null;
        mService = null;
    }

    private static StorageManagerRepositoryImpl mService = null;
    public static synchronized StorageManagerRepositoryImpl getInstance(Context context){
        if (mService == null){
            mService = new StorageManagerRepositoryImpl(context.getApplicationContext());
        }
        return mService;
    }
    public static StorageManagerRepositoryImpl getInstance(){
        return mService;
    }
    private StorageManagerRepositoryImpl(Context context){
        this.mContext = context;
        mRecevier = new StorageReceiver();
        storageRegister(mContext);
        storageCheckDiskInfo(mContext);
    }


    private void storageRegister(Context context){
        //register attach event
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY-1);
        context.registerReceiver(mRecevier,filter);

        //register mount event to  get file's path;
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter1.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter1.addAction(Intent.ACTION_MEDIA_EJECT);
        filter1.addAction(Intent.ACTION_MEDIA_REMOVED);

        filter1.addDataScheme("file");
        filter1.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY-1);
        context.registerReceiver(mRecevier,filter1);
    }


//    private Handler mHandler = new Handler();
//    private Runnable mRunnable = new Runnable() {
//        @Override
//        public void run() {
//            mUsbMaps.clear();
//            storageCheckDiskInfo(mContext);
//            getSdcardPath();
//        }
//    };

    private void recheckStorage(){
        mUsbMaps.clear();
        storageCheckDiskInfo(mContext);
        getSdcardPath();
    }


    private class StorageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String path = "";
            Log.i("zyf","zyf onReceive action :"+action);
            if (action!= null && action.contains(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
                UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            }
            if (action!= null && action.contains(Intent.ACTION_MEDIA_MOUNTED)){
                Log.i("zyf","zyf onReceive path :"+intent.getData().getPath());
                path = intent.getData().getPath();
                usbPath = path;
                recheckStorage();
                sendUsbPlugInCallback();
            }else if (action!= null && action.contains(Intent.ACTION_MEDIA_EJECT)){
                Log.i("zyf","zyf onReceive path :"+intent.getData().getPath());
                path = intent.getData().getPath();
                usbPath = path;
                sendUsbPlugOutCallback();
            }else if (action!= null && action.contains(Intent.ACTION_MEDIA_UNMOUNTED)){
                recheckStorage();
            } else if (action!= null && action.contains(Intent.ACTION_MEDIA_REMOVED)){
                //recheckStorage();
            }
        }
    }


    private void storageCheckDiskInfo(Context context){
        StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz = null;
        Method getBestVolumeDescription = null;
        Method isMountedReadable = null;
        Method getType = null;
        Method getVolumes = null;
        Method getPath = null;
        List<?> volumes = null;
        try {
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            getBestVolumeDescription = StorageManager.class.
                    getMethod("getBestVolumeDescription",volumeInfoClazz);
            getVolumes = StorageManager.class.getMethod("getVolumes");
            isMountedReadable = volumeInfoClazz.getMethod("isMountedReadable");
            getType = volumeInfoClazz.getMethod("getType");
            getPath = volumeInfoClazz.getMethod("getPath");
            volumes = (List<?>) getVolumes.invoke(manager);
            for (Object vol : volumes){
                File path = (File) getPath.invoke(vol);
                String storageName = (String) getBestVolumeDescription.invoke(manager,vol);
                if (path != null){
                    String storagePath = path.getPath();
                    Log.i("zyf","zyf storageName:"+storageName+ " storagePath:"+storagePath);
                    if (!("/storage/emulated".equals(storagePath) || "/data".equals(storagePath)
                            || "Internal shared storage".equals(storagePath))){
                        Log.i("zyf","!storagePath.contains");
                        mUsbMaps.put(storageName,storagePath);
                    }
                }
            }
            if (!mUsbMaps.isEmpty()){
                List<StorageDModel> modelList = convertUsbInfoList(mUsbMaps);
                if (EmptyTool.isNotEmpty(mCallbackList)){
                    for (Callback callback:mCallbackList){
                        callback.UsbListUpdate(modelList);
                    }
                }
            } else {
                if (EmptyTool.isNotEmpty(mCallbackList)){
                    for (Callback callback:mCallbackList){
                        callback.UsbListUpdate(null);
                    }
                }
            }
        }catch (Exception ex){
            Log.i("zyf","zyf failed to get DiskInfo "+ex.toString());
            ex.printStackTrace();
        }
    }


    private String getSdcardPath(){
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        Log.i("zyf","zyf StorageCheckSdcard "+sdPath);
        return sdPath;
    }

    private String getFileSize(long length){
        String fileSize = "";
        DecimalFormat format = new DecimalFormat("#.##");

        if (0 ==(length / 1024)){
            float size = (float) length;
            fileSize = format.format(size) + "B";
        } else if (0 == (length / 1024 * 1024)){
            float size = (float) length/1024;
            fileSize = format.format(size) + "K";
        } else if (0 == (length / 1024 * 1024 *1024)){
            float size = (float) length/(1024*1024);
            fileSize = format.format(size) + "M";
        } else {
            float size = (float) length/(1024*1024*1024);
            fileSize = format.format(size) + "G";
        }
        return fileSize;

    }

    private String getTotalVolumes(String path){
        File file = new File(path);
        if (null != file && file.isDirectory()){
            return getFileSize(file.getTotalSpace());
        }
        return "0";
    }
    private String getFreeVolumes(String path){
        File file = new File(path);
        if (null != file && file.isDirectory()){
            return getFileSize(file.getFreeSpace());
        }
        return "0";
    }

    private String getUsedVolumes(String path){
        File file = new File(path);
        if (null != file && file.isDirectory()){
            return getFileSize(file.getTotalSpace()-file.getFreeSpace());
        }
        return "0";
    }


    private List<StorageDModel> convertUsbInfoList(Map<String,String> mMaps){
        if (mMaps.isEmpty()){
            return null;
        }
        mUsbList.clear();
        for (Map.Entry<String,String> entry :mMaps.entrySet()){
            StorageDModel model = new StorageDModel();
            model.setName(entry.getKey());
            model.setPath(entry.getValue());
            model.setFreeVolumes(getFreeVolumes(entry.getValue()));
            model.setTotalVolumes(getTotalVolumes(entry.getValue()));
            model.setUsedVolumes(getUsedVolumes(entry.getValue()));
            model.setPvrPath(entry.getValue()+pvrUsbPath);
            mUsbList.add(model);
        }
        return mUsbList;
    }

    private void sendUsbPlugInCallback(){
        StorageDModel item =  checkUsbPlugStatus(mUsbList,usbPath);
        if (item != null){
            //mCallback.UsbPlugIn(item);
            if (EmptyTool.isNotEmpty(mCallbackList)){
                for (Callback callback:mCallbackList){
                    callback.UsbPlugIn(item);
                }
            }
        }
    }

    private void sendUsbPlugOutCallback(){
        StorageDModel item =  checkUsbPlugStatus(mUsbList,usbPath);
        if (item != null){
            //mCallback.UsbPlugOut(item);
            if (EmptyTool.isNotEmpty(mCallbackList)){
                for (Callback callback:mCallbackList){
                    callback.UsbPlugOut(item);
                }
            }
        }
    }


    private StorageDModel checkUsbPlugStatus(List<StorageDModel> usbList,String path){
        if (EmptyTool.isNotEmpty(usbList)){
            for (StorageDModel item:usbList){
                if (item.getPath().contains(path)){
                    return item;
                }
            }
        }
        return null;
    }



    public StorageDModel getSdcardInfo(){
        StorageDModel model = new StorageDModel();
        model.setName("SDCARD");
        String path =getSdcardPath();
        model.setPath(path);
        model.setFreeVolumes(getFreeVolumes(path));
        model.setTotalVolumes(getTotalVolumes(path));
        model.setUsedVolumes(getUsedVolumes(path));
        return model;
    }

    public List<StorageDModel> getUsbList(){
        return mUsbList;
    }


}
