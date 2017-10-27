package com.syezon.clean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 视频压缩类
 */

public class VideoCompress {

    private static final String TAG = VideoCompress.class.getName();

    public static void getVideos(Context context){
        Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                null,null, MediaStore.Images.Media.DATE_MODIFIED);
        if(mCursor != null){

            while(mCursor.moveToNext()){
                String path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Video.Media.DATA));
                Log.e(TAG, "视频路径：" + path);

            }
        }
    }


}
