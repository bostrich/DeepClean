package com.syezon.clean.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by June on 2017/9/14.
 */
public class AppCacheBean {
    private String pkgName;
    private String appName;
    private long size;
    private Drawable icon;
    private boolean selected;


    public AppCacheBean(){}

    public AppCacheBean(String pkgName, String appName, long size) {
        this.pkgName = pkgName;
        this.appName = appName;
        this.size = size;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
