package com.syezon.clean.bean;

import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by June on 2017/9/13.
 */
public class ApkBean {
    private String pkgName;
    private String appName;
    private int versionCode;
    private String versionName;
    private long size;
    private Drawable icon;
    private String state;
    private boolean selected;
    private File file;


    public ApkBean(){}

    public ApkBean(String pkgName, String appName, int versionCode, String versionName, long size, Drawable icon) {
        this.pkgName = pkgName;
        this.appName = appName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.size = size;
        this.icon = icon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
