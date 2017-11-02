package com.syezon.clean.utils;

import android.os.Environment;

import com.syezon.clean.FileScanUtil;
import com.syezon.clean.bean.QQCacheBean;
import com.syezon.clean.bean.WxCacheBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * QQ 接收的文件所在文件夹 /tencent/QQfile_recv
 * QQ 语音缓存所在文件夹(amr 文件) /tencten/MobileQQ/${QQ号码}/ptt
 * QQ 聊天图片所咋文件夹   /tencent/MobileQQ/photo
 * QQ 小视频    /tencent/MobileQQ/shortvideo    thumbs:缩略图文件夹
 *
 */

public class QQScanUtils {

    public static void getTalkingCacheFile(QQScanListener listener){
        File storage = Environment.getExternalStorageDirectory();
        File file_tencent = new File(storage, "tencent");
        List<QQCacheBean> list = new ArrayList<>();
        if(file_tencent.exists()){
            //获取接收文件
            File file_rec = new File(file_tencent, "QQfile_recv");
            if(file_rec.exists()){
                File[] files = file_rec.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File temp = files[i];
                    if(temp.isDirectory()){
                        List<File> allFiles = FileScanUtil.getAllFile(temp);
                        for (int j = 0; j < allFiles.size(); j++) {
                            File tem = allFiles.get(j);
                            QQCacheBean bean = new QQCacheBean();
                            bean.setFile(tem);
                            bean.setSelected(false);
                            bean.setSize(tem.length());
                            bean.setType("recvive");
                            if(listener != null) listener.getFile(bean);
                        }
                    }else{
                        QQCacheBean bean = new QQCacheBean();
                        bean.setFile(temp);
                        bean.setSelected(false);
                        bean.setSize(temp.length());
                        bean.setType("recvive");
                        if(listener != null) listener.getFile(bean);
                    }

                }
            }

            File file_qq = new File(file_tencent, "MobileQQ");
            if(file_qq.exists()) {//判断文件是否存在
                //扫描聊天图片(.jpg)
                File file_talking = new File(file_qq, "photo");
                if (file_talking.exists()) {
                    List<QQCacheBean> photos = getFile(file_talking, "photo");
                    for (int i = 0; i < photos.size(); i++) {
                        QQCacheBean bean = photos.get(i);
                        if(listener != null) listener.getFile(bean);
                    }
                }

                //扫描视频文件和缩略图文件（.jpg）
                File file_video = new File(file_qq, "shortvideo");
                if (file_video.exists()) {
                    List<QQCacheBean> shortvideo = getFile(file_video, "shortvideo");
                    for (int i = 0; i < shortvideo.size(); i++) {
                        QQCacheBean bean = shortvideo.get(i);
                        if(listener != null) listener.getFile(bean);
                    }
                }
                //获取语音文件
                List<QQCacheBean> voices = getFile(file_qq, "ptt");
                for (int i = 0; i < voices.size(); i++) {
                    QQCacheBean bean = voices.get(i);
                    if(listener != null) listener.getFile(bean);
                }
            }
        }
        if(listener != null) listener.scanFinished();
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
                            if(f.getName().endsWith(".jpg") || f.getName().endsWith(".png")){
                                QQCacheBean bean = new QQCacheBean();
                                bean.setFile(f);
                                bean.setType("photo");
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
                            }else if(f.getName().endsWith(".png") || f.getName().endsWith(".jpg")){
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

    public interface QQScanListener{
        void getFile(QQCacheBean bean);
        void scanFinished();
    }

}
