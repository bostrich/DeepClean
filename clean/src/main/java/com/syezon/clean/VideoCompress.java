package com.syezon.clean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.syezon.clean.bean.ScanBean;

import java.io.File;
import java.util.List;

/**
 * 视频压缩类
 * 使用contentResolver 获取视频并判断是否是相机保存的路径
 */

public class VideoCompress {

    private static final String TAG = VideoCompress.class.getName();

    /**
     *
     * @param context
     * @param listener
     */
    public static void getVideos(Context context, ImgCompress.ScanListener listener){
        //获取相机文件位置
        List<String> ext = SDCardUtil.getExtSDCardPath();
        String cameraPath = "";
        if(ext.size() > 0) {
            cameraPath = ext.get(0) + File.separator + "DCIM" + File.separator + "Camera";
        }else{
            cameraPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        }
        Log.e(TAG, "照片位置：" + cameraPath);

        Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                null,null, MediaStore.Images.Media.DATE_MODIFIED);
        if(mCursor != null){
            while(mCursor.moveToNext()){
                String path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Video.Media.DATA));
                Log.e(TAG, "视频路径：" + path);
                if(path.contains(cameraPath)){
                    ScanBean bean = new ScanBean();
                    File temp = new File(path);
                    if(temp != null){
                        bean.setFile(temp);
                        bean.setSelected(false);
                        bean.setSize(temp.length());
                        bean.setFileType("mp4");
                        if(listener != null) listener.getFile(bean);
                    }
                }
            }
        }
        if(listener != null) listener.scanFinished();
    }


}
