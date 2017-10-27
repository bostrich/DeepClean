package com.syezon.clean;

import android.util.Log;

import java.io.File;

/**
 * Created by June on 2017/10/25.
 */

public class FileScanUtil {


    /**
     * 获取apk包和临时文件和log文件
     * @param file
     * @param listener
     */
    public static void scanApkAndLog(File file, ScanListener listener, int level){
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File temp_file = files[i];
            if (temp_file.isDirectory()) {
                scanApkAndLog(temp_file, listener, level + 1);
            } else {
                if (temp_file.getName().endsWith(".apk") || temp_file.getName().endsWith(".log") || temp_file.getName().endsWith(".temp")) {
                    if(listener != null) listener.getSuitableFile(temp_file);
                }
            }
        }
        if(listener != null && level == 0) listener.scanFinish();
    }


    public interface ScanListener{
        void getSuitableFile(File file);
        void scanFinish();
    }
}
