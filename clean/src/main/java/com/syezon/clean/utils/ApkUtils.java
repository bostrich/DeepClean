package com.syezon.clean.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.syezon.clean.Utils;
import com.syezon.clean.bean.ApkBean;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 *
 */

public class ApkUtils {

    public static final String TAG = ApkUtils.class.getName();

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
}
