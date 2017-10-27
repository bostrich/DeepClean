package com.syezon.clean.bean;

/**
 * Created by June on 2017/9/21.
 */

public class ImgCompressBean {
    private String parentName;
    private String path;
    private boolean selected;
    private long saveSize;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getSaveSize() {
        return saveSize;
    }

    public void setSaveSize(long saveSize) {
        this.saveSize = saveSize;
    }
}
