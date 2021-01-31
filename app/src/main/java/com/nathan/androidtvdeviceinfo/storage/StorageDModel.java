package com.nathan.androidtvdeviceinfo.storage;

public class StorageDModel {

    private String mName;
    private String mPath;
    private String mTotalVolumes;
    private String mFreeVolumes;
    private String mUsedVolumes;
    private String mPvrPath;


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getTotalVolumes() {
        return mTotalVolumes;
    }

    public void setTotalVolumes(String totalVolumes) {
        mTotalVolumes = totalVolumes;
    }

    public String getFreeVolumes() {
        return mFreeVolumes;
    }

    public String getUsedVolumes() {
        return mUsedVolumes;
    }

    public void setUsedVolumes(String usedVolumes) {
        this.mUsedVolumes = usedVolumes;
    }

    @Override
    public String toString() {
        return "StorageDModel{" +
                "mName='" + mName + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mTotalVolumes='" + mTotalVolumes + '\'' +
                ", mFreeVolumes='" + mFreeVolumes + '\'' +
                ", mUsedVolumes='" + mUsedVolumes + '\'' +
                ", mPvrPath='" + mPvrPath + '\'' +
                '}';
    }

    public void setFreeVolumes(String freeVolumes) {
        mFreeVolumes = freeVolumes;
    }

    public String getPvrPath() {
        return mPvrPath;
    }

    public void setPvrPath(String pvrPath) {
        mPvrPath = pvrPath;
    }
}
