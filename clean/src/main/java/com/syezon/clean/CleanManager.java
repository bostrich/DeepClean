package com.syezon.clean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;


import com.syezon.clean.bean.ApkBean;
import com.syezon.clean.bean.AppCacheBean;
import com.syezon.clean.bean.WxCacheBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 清理功能管理类
 */
public class CleanManager {

    private static final String TAG = CleanManager.class.getName();


    public static List<WxCacheBean> searchWx(final Context context) {
        File file = Environment.getExternalStorageDirectory();
        File file_wx = new File(file, "tencent/MicroMsg");
        File wx = null;
        if (file_wx.exists()) {//文件夹存在遍历文件夹获取名字最长的文件夹
            File[] files = file_wx.listFiles();
            for (int i = 0; i < files.length; i++) {
                File temp = files[i];
                if (wx == null) {
                    wx = temp;
                } else {
                    if (wx.getName().length() < temp.getName().length()) {
                        wx = temp;
                    }
                }
            }
        }

        List<WxCacheBean> wxCacheFiles = new ArrayList<>();
        if (wx != null) {
            Log.e(TAG, wx.getAbsolutePath());
            List<File> fileImgs = new ArrayList<>();
            File target = new File(wx, "sns");
            if (target.exists()) {
                //遍历获取图片内容
                if (target.isDirectory()) {
                    fileImgs.addAll(getFile(target, "sns"));
                }
                for (int i = 0; i < fileImgs.size(); i++) {
                    WxCacheBean bean = new WxCacheBean();
                    bean.setFile(fileImgs.get(i));
                    bean.setType("sns");
                    bean.setSelected(true);
                    bean.setSize(fileImgs.get(i).length());
                    if(bean.getFile().getName().startsWith("sight")){
                        bean.setFileType("mp4");
                    }else{
                        bean.setFileType("img");
                    }
                    wxCacheFiles.add(bean);
                }
            }
            File target2 = new File(wx, "image");
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
                    bean.setSelected(true);
                    bean.setSize(fileImgs.get(i).length());
                    wxCacheFiles.add(bean);
                }
            }
            File target3 = new File(wx, "image2");
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
                    bean.setSelected(true);
                    bean.setSize(fileImgs.get(i).length());
                    wxCacheFiles.add(bean);
                }
            }

            File target4 = new File(wx, "video");
            fileImgs.clear();
            if (target4.exists()) {
                //遍历获取图片内容
                if (target4.isDirectory()) {
                    fileImgs.addAll(getFile(target4, "mp4"));
                }
                for (int i = 0; i < fileImgs.size(); i++) {
                    WxCacheBean bean = new WxCacheBean();
                    bean.setFile(fileImgs.get(i));
                    bean.setType("video");
                    bean.setFileType("mp4");
                    bean.setSelected(true);
                    bean.setSize(fileImgs.get(i).length());
                    wxCacheFiles.add(bean);
                }
            }
        }
        return wxCacheFiles;
    }


    public static void getAppsCache(Context context, PackageStatsObserver.GetCacheListener listener) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedPackages = pm.getInstalledApplications(PackageManager.GET_GIDS);

        List<ApplicationInfo> listTarget = new ArrayList<>();
        for (int i = 0; i < installedPackages.size(); i++) {
            ApplicationInfo info = installedPackages.get(i);
            //需要判断是否是系统应用
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //系统应用
            } else {
                listTarget.add(info);
            }
        }
        //判断需要扫描的数量
        if (listTarget.size() > 0) {
            IPackageStatsObserver.Stub observer = new PackageStatsObserver(listener, listTarget.size());
            for (int i = 0; i < listTarget.size(); i++) {
                getPackageInfo(context, listTarget.get(i).packageName, observer);
            }
        }
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
//                    if (type.equals("sns")) {
//                        if (f.getName().startsWith("snsb")) {
//                            mFileList.add(f);
//                            Log.e(TAG, f.getName());
//                        }
//                    } else {
                    if(type.equals("mp4")){
                        if(f.getName().endsWith("mp4")){
                            mFileList.add(f);
                            Log.e(TAG, f.getName());
                        }
                        continue;
                    }
                        if (size > 1024) {
                            mFileList.add(f);
                            Log.e(TAG, f.getName());
                        }
//                    }
                } else {
                    List<File> files_tem = getFile(f, type);
                    if (files_tem.size() > 0) mFileList.addAll(files_tem);
                }
            }
        }
        return mFileList;
    }


    /**
     * 获取多余安装包
     *
     * @param context
     * @return
     */
    public static List<File> getApk(final Context context) {
        File file = Environment.getExternalStorageDirectory();
        List<File> files = new ArrayList<>();
        files.addAll(getApkFile(file));
        return files;
    }

    public static List<File> getApkFile(File file) {
        List<File> list_files = new ArrayList<>();
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File temp_file = files[i];
            if (temp_file.isDirectory()) {
                list_files.addAll(getApkFile(temp_file));
            } else {
                if (temp_file.getName().endsWith(".apk")) {
                    list_files.add(temp_file);
                    Log.e(TAG, "apk 文件：" + temp_file.getName());
                }
            }
        }
        return list_files;
    }

    public static List<ApkBean> getApks(Context context) {
        List<File> listApks = new ArrayList<>();
        List<ApkBean> listApkBeans = new ArrayList<>();
        listApks.addAll(getApkFile(Environment.getExternalStorageDirectory().getAbsoluteFile()));
        for (int i = 0; i < listApks.size(); i++) {
            listApkBeans.add(getApkInfo(context, listApks.get(i).getAbsolutePath()));
        }
        return listApkBeans;
    }

    /**
     * 通过反射方法获取未安装apk包信息 icon和appName
     *
     * @param apkPath
     */
    public static ApkBean getApkInfo(Context context, String apkPath) {
        ApkBean bean = new ApkBean();
        File file = new File(apkPath);
        bean.setFile(file);
        bean.setSize(file.length());

        //获取版本号
        PackageManager pm = context.getPackageManager();
        PackageInfo pakinfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (pakinfo != null) {
            ApplicationInfo appinfo = pakinfo.applicationInfo;
            bean.setVersionName(pakinfo.versionName);
            bean.setVersionCode(pakinfo.versionCode);
            bean.setPkgName(pakinfo.packageName);

            String text = "versionName = " + pakinfo.versionName + " , pakName = " + pakinfo.packageName + ", versionCode:" + pakinfo.versionCode;
            Log.e(TAG, text);
        } else {
            System.out.println("pakinfo == null");
        }
        int versionCode = Utils.getApkVersionCode(context, bean.getPkgName());
        if (versionCode == 0) {
            bean.setState("未安装");
            bean.setSelected(true);
        } else if (versionCode >= bean.getVersionCode()) {
            bean.setState("已安装");
            bean.setSelected(true);
        } else {
            bean.setState("新版");
            bean.setSelected(true);
        }


        String PATH_PackageParser = "android.content.pm.PackageParser";
        String PATH_AssetManager = "android.content.res.AssetManager";
        try {
            // apk包的文件路径
            // 这是一个Package 解释器, 是隐藏的
            // 构造函数的参数只有一个, apk文件的路径
            // PackageParser packageParser = new PackageParser(apkPath);
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class<?>[] typeArgs = {String.class};
            Constructor<?> pkgParserCt;
            Object pkgParser;
            Method pkgParser_parsePackageMtd;
            Object[] valueArgs = {apkPath};
            Object pkgParserPkg;
            //高版本API有改变
            if (Build.VERSION.SDK_INT >= 21) {
                pkgParserCt = pkgParserCls.getConstructor(null);
                pkgParser = pkgParserCt.newInstance(null);
                typeArgs = new Class[2];
                typeArgs[0] = File.class;
                typeArgs[1] = Integer.TYPE;
                pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);

                valueArgs = new Object[2];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = 0;

                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
            } else {
                pkgParserCt = pkgParserCls.getConstructor(typeArgs);
                pkgParser = pkgParserCt.newInstance(valueArgs);

                typeArgs = new Class<?>[]{File.class, String.class, DisplayMetrics.class, int.class};
                pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);

                DisplayMetrics metrics = new DisplayMetrics();
                metrics.setToDefaults();

                valueArgs = new Object[4];
                valueArgs[0] = new File(apkPath);
                valueArgs[1] = apkPath;
                valueArgs[2] = metrics;
                valueArgs[3] = 0;
                pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
            }

            // 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
            // ApplicationInfo info = mPkgInfo.applicationInfo;
            Field appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");
            ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
            // uid 输出为"-1"，原因是未安装，系统未分配其Uid。
            Log.d(TAG, "pkg:" + info.packageName + " uid=" + info.uid);
            // Resources pRes = getResources();
            // AssetManager assmgr = new AssetManager();
            // assmgr.addAssetPath(apkPath);
            // Resources res = new Resources(assmgr, pRes.getDisplayMetrics(),
            // pRes.getConfiguration());
            Class assetMagCls = Class.forName(PATH_AssetManager);
            Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
            Object assetMag = assetMagCt.newInstance((Object[]) null);
            typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath",
                    typeArgs);
            valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
            Resources res = context.getResources();
            typeArgs = new Class[3];
            typeArgs[0] = assetMag.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor resCt = Resources.class.getConstructor(typeArgs);
            valueArgs = new Object[3];
            valueArgs[0] = assetMag;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            res = (Resources) resCt.newInstance(valueArgs);
            CharSequence label = null;
            if (info.labelRes != 0) {
                label = res.getText(info.labelRes);
                bean.setAppName(res.getText(info.labelRes) + "");
            }
            // if (label == null) {
            // label = (info.nonLocalizedLabel != null) ? info.nonLocalizedLabel
            // : info.packageName;
            // }
            Log.d(TAG, "label=" + label);
            // 这里就是读取一个apk程序的图标
            if (info.icon != 0) {
                Drawable icon = res.getDrawable(info.icon);
                bean.setIcon(icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }


    /**
     * 获取versionCode
     *
     * @param ctx
     * @param archiveFilePath
     * @return
     */
    public static String getUninstallAPKInfo(Context ctx, String archiveFilePath) {
        String versionName = null;
        String appName = null;
        String pakName = null;
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pakinfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (pakinfo != null) {
            ApplicationInfo appinfo = pakinfo.applicationInfo;
            versionName = pakinfo.versionName;
            Drawable icon = pm.getApplicationIcon(appinfo);
            appName = (String) pm.getApplicationLabel(appinfo);
            pakName = appinfo.packageName;

            String text = "versionName = " + versionName + " , appName = " + appName + " , pakName = " + pakName + ", versionCode:" + pakinfo.versionCode;
            Log.e(TAG, text);
        } else {
            System.out.println("pakinfo == null");
        }

        return versionName;
    }


    public static void getPackageInfo(Context context, String packageName, IPackageStatsObserver.Stub observer) {
        try {
            PackageManager pm = context.getPackageManager();
            Method getPackageSizeInfo = pm.getClass()
                    .getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            getPackageSizeInfo.invoke(pm, packageName, observer);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public static void cleanAppCache(Context context, List<AppCacheBean> list){
        File externalDir = context.getExternalCacheDir();
        if (externalDir == null) {
            return;
        }
        PackageManager pm = context.getPackageManager();
        //删除外部存储的缓存
        for (AppCacheBean bean : list) {
            if(!bean.isSelected()) continue;
            String externalCacheDir = externalDir.getAbsolutePath()
                    .replace(context.getPackageName(), bean.getPkgName());
            File externalCache = new File(externalCacheDir);
            if (externalCache.exists() && externalCache.isDirectory()) {
                deleteFile(externalCache);
            }
        }


        try {
            Method freeStorageAndNotify = pm.getClass()
                    .getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            long freeStorageSize = Long.MAX_VALUE;

            freeStorageAndNotify.invoke(pm, freeStorageSize, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    Log.e(TAG,"清理缓存：" + "包名：" + packageName + "---是否成功：" + String.valueOf(succeeded));
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String name : children) {
                boolean suc = deleteFile(new File(file, name));
                if (!suc) {
                    return false;
                }
            }
        }
        return file.delete();
    }
}
