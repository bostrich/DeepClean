package com.syezon.clean.bean;

import java.util.List;

/**
 * Created by June on 2017/9/22.
 */

public class ImgCompressFileBean {
    private String parentName;
    private List<ImgCompressBean> list;


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<ImgCompressBean> getList() {
        return list;
    }

    public void setList(List<ImgCompressBean> list) {
        this.list = list;
    }
}
