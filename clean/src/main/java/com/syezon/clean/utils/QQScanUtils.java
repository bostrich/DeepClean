package com.syezon.clean.utils;

import android.os.Environment;

import com.syezon.clean.bean.QQCacheBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * QQ 接收的文件所在文件夹 /tencent/QQfile_recv
 * QQ 语音缓存所在文件夹(amr 文件) /tencten/MobileQQ/${QQ号码}/ptt
 * QQ 聊天图片所咋文件夹   /tencent/MobileQQ/photo
 * QQ 小视频    /tencent/MobileQQ/shortvideo    thumbs:缩略图文件夹
 *
 *
 */

public class QQScanUtils {

    public static List<QQCacheBean> getTalkingCacheFile(){
        File storage = Environment.getExternalStorageDirectory();
        File file_qq = new File(storage, "MobileQQ");
        List<QQCacheBean> list = new ArrayList<>();
        if(file_qq.exists()){//判断文件是否存在
            //扫描聊天图片
            File file_talking = new File(file_qq, "photo");
            if(file_talking.exists()){


            }

            //扫描视频文件
            File file_video = new File(file_qq, "shortvideo");
            if(file_video.exists()){
                list.addAll(getFile(file_video, "shortvideo"));
            }

            //扫描语音文件
            File file_voice = new File(file_qq, "ptt");
            if(file_video.exists()){
                list.addAll(getFile(file_voice, "ptt"));
            }
        }
        return list;
    }

    /**
     *
     * @param file
     * @return
     */
    public static List<QQCacheBean> getFile(File file, String type) {
        File[] fileArray = file.listFiles();
        List<QQCacheBean> mFileList = new ArrayList<>();
        if (fileArray != null) {
            for (File f : fileArray) {
                if (f.isFile()) {
                    long size = f.length();
                    switch(type){
                        case "photo":
                            if(f.getName().endsWith(".png")){
                                QQCacheBean bean = new QQCacheBean();
                                bean.setFile(f);
                                bean.setType("phote");
                                bean.setFileType("png");
                                bean.setSize(f.length());
                                mFileList.add(bean);
                            }
                            break;
                        case "ptt":
                            if(f.getName().endsWith(".amr")){
                                QQCacheBean bean = new QQCacheBean();
                                bean.setFile(f);
                                bean.setType("ptt");
                                bean.setFileType("amr");
                                bean.setSize(f.length());
                                mFileList.add(bean);
                            }
                            break;

                        case "shortvideo":
                            if(f.getName().endsWith(".mp4")){
                                QQCacheBean bean = new QQCacheBean();
                                bean.setFile(f);
                                bean.setType("shortvideo");
                                bean.setFileType("mp4");
                                bean.setSize(f.length());
                                mFileList.add(bean);
                            }else if(f.getName().endsWith(".png")){
                                QQCacheBean bean = new QQCacheBean();
                                bean.setFile(f);
                                bean.setType("shortvideo");
                                bean.setFileType("png");
                                bean.setSize(f.length());
                                mFileList.add(bean);
                            }
                            break;
                        default://默认不处理

                            break;
                    }

                } else {
                    List<QQCacheBean> files_tem = getFile(f, type);
                    if (files_tem.size() > 0) mFileList.addAll(files_tem);
                }
            }
        }
        return mFileList;
    }

}
