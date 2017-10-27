package com.syezon.clean.utils;

import android.os.Environment;

import java.io.File;

/**
 * QQ 接收的文件所在文件夹 /tencent/QQfile_recv
 * QQ 语音缓存所在文件夹(amr 文件) /tencten/MobileQQ/${QQ号码}/ptt
 * QQ 聊天图片所咋文件夹   /tencent/MobileQQ/photo
 * QQ 小视频    /tencent/MobileQQ/shortvideo    thumbs:缩略图文件夹
 *
 *
 */

public class QQScanUtils {

    public static void getTalkingCacheFile(){
        File storage = Environment.getExternalStorageDirectory();
        File file_qq = new File(storage, "MobileQQ");
        if(file_qq.exists()){//判断文件是否存在





        }
    }

}
