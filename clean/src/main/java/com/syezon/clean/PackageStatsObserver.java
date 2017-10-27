package com.syezon.clean;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.util.Log;

import com.syezon.clean.bean.AppCacheBean;

/**
 * Created by June on 2017/9/14.
 *
 */
public class PackageStatsObserver extends IPackageStatsObserver.Stub {

    private static final String TAG = PackageStatsObserver.class.getName();
    private GetCacheListener listener;
    private int count;
    private int time = 0;

    public PackageStatsObserver(GetCacheListener listener, int count) {
        this.listener = listener;
        this.count = count;
    }

    @Override
    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
        time ++;
        if (pStats == null || !succeeded) {//没有获取到数据

        }else{
            AppCacheBean bean = new AppCacheBean();
            bean.setPkgName(pStats.packageName);
            bean.setSize(pStats.cacheSize + pStats.externalCacheSize);
            Log.e(TAG, "缓存：" + pStats.cacheSize + "外部缓存： " + pStats.externalCacheSize);
            if(listener != null) listener.getCache(bean);
        }
        if(time >= count){
            if(listener != null) listener.finishScan();
        }
    }

    public interface GetCacheListener{
        void getCache(AppCacheBean bean);
        void finishScan();
    }
}
