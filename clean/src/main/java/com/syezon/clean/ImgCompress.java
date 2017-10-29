package com.syezon.clean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import com.syezon.clean.bean.ImgCompressBean;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 图片压缩工具类
 */
public class ImgCompress {


    private static final String TAG = ImgCompress.class.getName();
    public static List<ImgCompressBean> cameraList = new ArrayList<>();
    public static List<ImgCompressBean> screenshotList = new ArrayList<>();


    /**
     * 通过contentProvider获取图片,运行在子线程中
     * 只要获取拍照图片
     * 需要判断是否有外置SD卡 samsung 照片保存在外置存储卡中
     */
    public static boolean getImages(Context context) {
        //获取截屏文件夹位置
//        File picPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        String screenshotPath = picPath.getAbsolutePath() + File.separator + "Screenshots";
//        Log.e(TAG, "截屏路径：" + screenshotPath);

        List<String> ext = SDCardUtil.getExtSDCardPath();

        String cameraPath = "";
        if(ext.size() > 0) {
            cameraPath = ext.get(0) + File.separator + "DCIM" + File.separator + "Camera";
        }else{
            cameraPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        }
        Log.e(TAG, "照片位置：" + cameraPath);

        cameraList.clear();
        screenshotList.clear();

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
        if (mCursor == null) {
            return false;
        }

        HashMap<String,ArrayList<ImgCompressBean>> map = new HashMap<>();
        while (mCursor.moveToNext()) {
            //获取图片的路径
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            File original = new File(path);


            //获取该图片的父路径名
            String parentName = new File(path).getParentFile().getAbsolutePath();
            ImgCompressBean bean = new ImgCompressBean();
            bean.setParentName(parentName);
            bean.setPath(path);
            bean.setSelected(true);
            if(parentName.equals(cameraPath)){
                cameraList.add(bean);
            }
            Log.e(TAG, path);
        }
        return true;
    }

    public static void compress( String path) {
        File original = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.e(TAG, path + "图片尺寸：" + bitmap.getWidth() + "--" + bitmap.getHeight());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        long size = baos.toByteArray().length;
        Log.e(TAG, "图片初始尺寸：" + Utils.formatSize(original.length()) + "--压缩后尺寸：" + Utils.formatSize(size));
        String temp = path.replace(".jpg", "_");
        File file_target = new File(path + String.valueOf(System.currentTimeMillis()) + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file_target);
            fos.write(baos.toByteArray());
            fos.flush();
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fos.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
