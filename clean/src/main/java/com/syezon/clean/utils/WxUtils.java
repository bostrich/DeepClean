package com.syezon.clean.utils;

import android.os.Environment;
import android.util.Log;

import com.syezon.clean.FileScanUtil;
import com.syezon.clean.bean.WxCacheBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描微信文件夹
 * tencent/MicroMsg/文件夹下
 * 32为数字和字母文件夹为微信用户缓存文件夹
 * sns 为朋友圈图片和视频
 * 微信下载文件夹位置： /tencent/MicroMsg/Download
 */

public class WxUtils {

    public static void getGarbageAndCache(FileScanUtil.ScanListener listener){
        File file = Environment.getExternalStorageDirectory();
        File file_wx = new File(file, "tencent/MicroMsg");
        File wx = null;
        if (file_wx.exists()) {
            File[] files = file_wx.listFiles();
            for (int i = 0; i < files.length; i++) {
                File target_file = files[i];
                if(target_file.isDirectory() && target_file.getName().length() != 32){
                    FileScanUtil.scanApkAndLog(target_file, listener, 1);
                }
            }
        }
        listener.scanFinish();
    }

    public static List<WxCacheBean> getTalkingFile(WxScanListener listener){
        File file = Environment.getExternalStorageDirectory();
        File file_wx = new File(file, "tencent/MicroMsg");
        List<WxCacheBean> wxCacheFiles = new ArrayList<>();
        if (file_wx.exists()) {//微信用户信息文件名由32位数字和字母组成
            File[] files = file_wx.listFiles();
            for (int j = 0; j < files.length; j++) {
                File temp = files[j];
                List<File> fileImgs = new ArrayList<>();
                if(temp.isDirectory() && temp.getName().length() == 32){
                    File file_sns = new File(temp, "sns");
                    if(file_sns.exists() && file_sns.isDirectory()){//朋友圈图片和视频
                        fileImgs.addAll(getFile(file_sns, "sns"));
                        for (int i = 0; i < fileImgs.size(); i++) {
                            WxCacheBean bean = new WxCacheBean();
                            bean.setFile(fileImgs.get(i));
                            bean.setType("sns");
                            bean.setSelected(false);
                            bean.setSize(fileImgs.get(i).length());
                            if(bean.getFile().getName().startsWith("sight")){
                                bean.setFileType("mp4");
                            }else{
                                bean.setFileType("img");
                            }
                            wxCacheFiles.add(bean);
                            if(listener != null) listener.getFile(bean);
                        }
                    }

                    File target2 = new File(temp, "image");
                    fileImgs.clear();
                    if (target2.exists()) {
                        //遍历获取图片内容
                        if (target2.isDirectory()) {
                            fileImgs.addAll(getFile(target2, "image"));
                        }
                        for (int i = 0; i < fileImgs.size(); i++) {
                            WxCacheBean bean = new WxCacheBean();
                            bean.setFile(fileImgs.get(i));
                            bean.setType("image");
                            bean.setFileType("img");
                            bean.setSelected(false);
                            bean.setSize(fileImgs.get(i).length());
                            wxCacheFiles.add(bean);
                            if(listener != null) listener.getFile(bean);
                        }
                    }

                    File target3 = new File(temp, "image2");
                    fileImgs.clear();
                    if (target3.exists()) {
                        //遍历获取图片内容
                        if (target3.isDirectory()) {
                            fileImgs.addAll(getFile(target3, "image2"));
                        }
                        for (int i = 0; i < fileImgs.size(); i++) {
                            WxCacheBean bean = new WxCacheBean();
                            bean.setFile(fileImgs.get(i));
                            bean.setType("image2");
                            bean.setFileType("img");
                            bean.setSelected(false);
                            bean.setSize(fileImgs.get(i).length());
                            wxCacheFiles.add(bean);
                            if(listener != null) listener.getFile(bean);
                        }
                    }

                    File target4 = new File(temp, "video");
                    fileImgs.clear();
                    if (target4.exists()) {
                        //遍历获取图片内容
                        if (target4.isDirectory()) {
                            fileImgs.addAll(getFile(target4, "video"));
                        }
                        for (int i = 0; i < fileImgs.size(); i++) {
                            WxCacheBean bean = new WxCacheBean();
                            bean.setFile(fileImgs.get(i));
                            if(bean.getFile().getName().endsWith("mp4")){
                                bean.setFileType("mp4");
                            }else if(bean.getFile().getName().endsWith("jpg")){
                                bean.setFileType("jpg");
                            }
                            bean.setType("video");
                            bean.setSelected(false);
                            bean.setSize(fileImgs.get(i).length());
                            wxCacheFiles.add(bean);
                            if(listener != null) listener.getFile(bean);
                        }
                    }

                    //获取聊天语音文件
                    File target_voice = new File(temp, "voice2");
                    fileImgs.clear();
                    if(target_voice.exists()){
                        if(target_voice.isDirectory()){
                            fileImgs.addAll(getFile(target_voice, "voice2"));
                        }
                        for (int i = 0; i < fileImgs.size(); i++) {
                            WxCacheBean bean = new WxCacheBean();
                            bean.setFile(fileImgs.get(i));
                            bean.setType("voice2");
                            bean.setFileType("amr");
                            bean.setSelected(false);
                            bean.setSize(fileImgs.get(i).length());
                            wxCacheFiles.add(bean);
                            if(listener != null) listener.getFile(bean);
                        }
                    }
                }else if(temp.getName().equals("Download") && temp.isDirectory()){
                    File[] downloadFiles = temp.listFiles();
                    for (int i = 0; i < downloadFiles.length; i++) {
                        WxCacheBean bean = new WxCacheBean();
                        bean.setFile(downloadFiles[i]);
                        bean.setType("download");
                        bean.setFileType("file");
                        bean.setSelected(false);
                        bean.setSize(downloadFiles[i].length());
                        wxCacheFiles.add(bean);
                        if(listener != null) listener.getFile(bean);
                    }
                }
            }
        }
        if(listener != null) listener.scanFinished();
        return wxCacheFiles;
    }

    /**
     *
     * @param file
     * @return
     */
    public static List<File> getFile(File file, String type) {
        File[] fileArray = file.listFiles();
        List<File> mFileList = new ArrayList<>();
        if (fileArray != null) {
            for (File f : fileArray) {
                if (f.isFile()) {
                    long size = f.length();
                    switch(type){
                        case "video":
                            if(f.getName().endsWith("mp4")){
                                mFileList.add(f);
                            }else if(f.getName().endsWith("jpg")){
                                mFileList.add(f);
                            }

                            break;
                        case "voice2":
                            if(f.getName().endsWith(".amr")){
                                mFileList.add(f);
                            }
                            break;
                        default:
                            if (size > 1024) {
                                mFileList.add(f);
                            }
                            break;
                    }

                } else {
                    List<File> files_tem = getFile(f, type);
                    if (files_tem.size() > 0) mFileList.addAll(files_tem);
                }
            }
        }
        return mFileList;
    }


    public interface WxScanListener{
        void getFile(WxCacheBean bean);
        void scanFinished();
    }
}
