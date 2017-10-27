package com.syezon.clean.bean;

import java.io.File;

/**
 * Created by June on 2017/10/27.
 */

public class QQCacheBean {
    private File file;
    private String type;//来源：sns,image,image2,video,voice2等
    private boolean selected;//是否选择
    private long size;//文件大小
    private String fileType;//文件类型，图片,视频还是语音

    public QQCacheBean(){

    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
