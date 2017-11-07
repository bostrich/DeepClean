package com.syezon.clean;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.ApkInfoAdapter;
import com.syezon.clean.adapter.AppCacheAdapter;
import com.syezon.clean.bean.ApkBean;
import com.syezon.clean.bean.AppCacheBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import com.syezon.clean.utils.ApkUtils;


public class CleanActivity extends AppCompatActivity {

    private static final String TAG = CleanActivity.class.getName();
    private static final int GET_APP_CACHE_BEAN = 1;
    private static final int GET_APP_CACHE_FINISH = 2;
    private static final int GET_APK_INFO = 3;
    private static final int SCAN_APK_FINISHED = 4;
    private static final int SCAN_WX_FINISHED = 5;
    private static final int APP_CACHE = 11;
    private static final int APK = 12;
    private static final int CLEAN_SUCCESSED = 21;

    private DotRotateLoadingView customLoadingApk;
    private ImageView imgLoadedApk;
    private DotRotateLoadingView customLoadingAd;
    private ImageView imgLoadedAd;
    private DotRotateLoadingView customLoadingCache;
    private ImageView imgLoadedCache;
//    private DotRotateLoadingView customLoadingImg;
//    private ImageView imgLoadedImg;
    private DotRotateLoadingView customLoadingUninstall;
    private ImageView imgLoadedUninstall;
//    private DotRotateLoadingView customLoadingWx;
//    private ImageView imgLoadedWx;
    private TextView tvCleanTotal, tvScanTotal;
    private LinearLayout llContainerItems;
    private Button btn;

    private long scanSize;//扫描出来可以节省的空间
    private int scanFinishedItem;//扫描完成条目数

    private Handler mHandler;
    private List<ApkBean> apks = new ArrayList<>();
    private RecyclerView.Adapter adapterApks;

    private List<AppCacheBean> listAppCaches = new ArrayList<>();
    private RecyclerView.Adapter adapterCache;
    public static List<WxCacheBean> wxCacheFiles = new ArrayList<>();
    public List<ApkBean> apkFiles = new ArrayList<>();

    private long apkScanTotalSize;
    private long cacheScanTotalSize;
    private long apkCleanTotalSize;
    private long cacheCleanTotalSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        ButterKnife.bind(this);
        initHandler();
        initViews();
        initData();
    }

    private void initData() {
        getApkAndLog();
        getAppCaches();

    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_APP_CACHE_BEAN:
                        AppCacheBean bean = (AppCacheBean) msg.obj;
                        Log.e(TAG, "获取APP缓存：" + bean.getPkgName() + "---大小：" + bean.getSize());
                        if (bean.getSize() > 8192) {
                            PackageManager pm = getPackageManager();
                            try {
                                ApplicationInfo applicationInfo = pm.getApplicationInfo(bean.getPkgName(), PackageManager.GET_META_DATA);
                                bean.setIcon(applicationInfo.loadIcon(pm));
                                bean.setAppName(applicationInfo.loadLabel(pm).toString());
                                bean.setSelected(true);
                                listAppCaches.add(bean);

                                scanSize += bean.getSize();
                                tvScanTotal.setText(Utils.formatSize(scanSize));
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case GET_APP_CACHE_FINISH:
                        customLoadingCache.cleanRotating();
                        customLoadingCache.setVisibility(View.GONE);
                        imgLoadedCache.setVisibility(View.VISIBLE);
                        scanFinishedItem++;
                        checkFinishedAllScan();
                        break;

                    case GET_APK_INFO:
                        ApkBean apkInfo = (ApkBean) msg.obj;
                        apkFiles.add(apkInfo);
                        scanSize += apkInfo.getSize();
                        tvScanTotal.setText(Utils.formatSize(scanSize));
                        break;
                    case SCAN_APK_FINISHED:
                        customLoadingApk.cleanRotating();
                        customLoadingApk.setVisibility(View.GONE);
                        imgLoadedApk.setVisibility(View.VISIBLE);
                        scanFinishedItem++;
                        checkFinishedAllScan();
                        break;
                    case SCAN_WX_FINISHED:
                        scanFinishedItem++;
                        checkFinishedAllScan();
                        break;

                    case CLEAN_SUCCESSED:
                        int count = llContainerItems.getChildCount();
                        if(count > 3){
                            for (int i = 0; i < count - 3; i++) {
                                llContainerItems.removeViewAt(0);
                            }
                        }
                        break;
                }

            }
        };
    }

    private void getApkAndLog() {
        new Thread() {
            @Override
            public void run() {
                FileScanUtil.scanApkAndLog(Environment.getExternalStorageDirectory().getAbsoluteFile(), new FileScanUtil.ScanListener() {
                    @Override
                    public void getSuitableFile(File file) {
                        if(file.getName().endsWith(".apk")){
                            ApkBean apkInfo = ApkUtils.getApkInfo(CleanActivity.this, file.getAbsolutePath());
                            Message msg = mHandler.obtainMessage();
                            msg.what = GET_APK_INFO;
                            msg.obj = apkInfo;
                            mHandler.sendMessage(msg);
                        }else{

                        }
                    }

                    @Override
                    public void scanFinish() {
                        mHandler.sendEmptyMessage(SCAN_APK_FINISHED);
                    }
                }, 0);
            }
        }.start();
    }



    private void getAppCaches() {
        new Thread() {
            @Override
            public void run() {
                CleanManager.getAppsCache(CleanActivity.this, new PackageStatsObserver.GetCacheListener() {
                    @Override
                    public void getCache(AppCacheBean bean) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = GET_APP_CACHE_BEAN;
                        msg.obj = bean;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void finishScan() {
                        mHandler.sendEmptyMessage(GET_APP_CACHE_FINISH);
                    }
                });

            }
        }.start();
    }



    /**
     * 判断是否完成全部扫描
     */
    private void checkFinishedAllScan() {
        if(scanFinishedItem >= 2){//完成全部扫描项
            //移除全部视图
            llContainerItems.removeAllViews();
            //动态添加视图
            if(apkFiles.size() > 0){
                setApkFilesViews(llContainerItems);
            }
            if(listAppCaches.size() > 0){
                setAppCacheViews(llContainerItems);
            }
            //添加微信专清视图判断内存中是否具有微信安装文件夹
            setWxCleanViews(llContainerItems);

            //添加QQ专清视图判断内存中是否具有QQ安装文件夹
            setQQCleanViews(llContainerItems);

            //节省空间视图
            setSaveSpaceViews(llContainerItems);

            //设置显示视图背景
            if(llContainerItems.getChildCount() > 0){
                for (int i = 0; i < llContainerItems.getChildCount() - 1; i++) {
                    View view = llContainerItems.getChildAt(i);
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    if(layoutParams instanceof LinearLayout.LayoutParams){
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutParams;
                        params.setMargins(0, 0, 0, 40);
                        view.setLayoutParams(params);
                    }
                }
            }
        }

        setButtonClick();

        tvCleanTotal.setVisibility(View.VISIBLE);
        setCleanTotalSize();
    }

    private void setButtonClick() {
        btn.setEnabled(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AppCacheBean> list = new ArrayList<>();
                for (int i = 0; i < listAppCaches.size(); i++) {
                    AppCacheBean bean = listAppCaches.get(i);
                    if(bean.isSelected()){
                        list.add(bean);
                    }
                }
                CleanManager.cleanAppCache(CleanActivity.this, list);

                deleteApk();

                mHandler.sendEmptyMessage(CLEAN_SUCCESSED);
            }
        });
    }


    private void setAppCacheViews(LinearLayout llContainerItems) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_clean_scan_finished, null, false);
        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item_title);
        final TextView tvTotalSize = (TextView) view.findViewById(R.id.tv_total_size);
        final TextView tvItemName = (TextView) view.findViewById(R.id.tv_app_name);
        final ImageView imgCheckbox = (ImageView) view.findViewById(R.id.img_item_state);
        final ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow);
        final RecyclerView rec = (RecyclerView) view.findViewById(R.id.rec);
        tvItemName.setText("软件缓存");
        long size = 0;
        for (AppCacheBean bean :listAppCaches) {
            cacheScanTotalSize += bean.getSize();
        }
        cacheCleanTotalSize = cacheScanTotalSize;
        tvTotalSize.setText(Utils.formatSize(cacheScanTotalSize));
        setCheckboxImgAndTag(imgCheckbox, 2);
        final RecyclerView.Adapter adapter = new AppCacheAdapter(this, listAppCaches, new ApkItemSelectedListener() {
            @Override
            public void itemSelectedChanged(long size) {
                if(size == cacheScanTotalSize){
                    setCheckboxImgAndTag(imgCheckbox, 2);
                }else if(size == 0){
                    setCheckboxImgAndTag(imgCheckbox, 0);
                }else{
                    setCheckboxImgAndTag(imgCheckbox, 1);
                }
                cacheCleanTotalSize = size;
                setCleanTotalSize();
                tvTotalSize.setText(Utils.formatSize(size));
            }
        });

        imgCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = (int) view.getTag();
                switch(state){
                    case 0:
                        for (int i = 0; i < listAppCaches.size(); i++) {
                            listAppCaches.get(i).setSelected(true);
                        }
                        adapter.notifyDataSetChanged();
                        cacheCleanTotalSize = cacheScanTotalSize;
                        setCleanTotalSize();
                        setCheckboxImgAndTag(imgCheckbox, 2);
                        break;
                    case 1:
                        for (int i = 0; i < listAppCaches.size(); i++) {
                            listAppCaches.get(i).setSelected(true);
                        }
                        adapter.notifyDataSetChanged();
                        tvTotalSize.setText(Utils.formatSize(cacheScanTotalSize));
                        cacheCleanTotalSize = cacheScanTotalSize;
                        setCleanTotalSize();
                        setCheckboxImgAndTag(imgCheckbox, 2);
                        break;
                    case 2:
                        for (int i = 0; i < listAppCaches.size(); i++) {
                            listAppCaches.get(i).setSelected(false);
                        }
                        adapter.notifyDataSetChanged();
                        setCheckboxImgAndTag(imgCheckbox, 0);
                        cacheCleanTotalSize = 0;
                        setCleanTotalSize();break;
                }
            }
        });
        rec.setAdapter(adapter);
        rec.setLayoutManager(new LinearLayoutManager(this));
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rec.isShown()){
                    rec.setVisibility(View.GONE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 180.0f, 360.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }else{
                    rec.setVisibility(View.VISIBLE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 0.0f, 180.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }
            }
        });
        rec.setVisibility(View.GONE);
        llContainerItems.addView(view);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
        view.startAnimation(animation);
    }

    private void setCleanTotalSize() {
        tvCleanTotal.setText("已选择：" + Utils.formatSize(apkCleanTotalSize + cacheCleanTotalSize));
    }

    private void setApkFilesViews(LinearLayout llContainerItems) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_clean_scan_finished, null, false);
        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item_title);
        final TextView tvTotalSize = (TextView) view.findViewById(R.id.tv_total_size);
        final ImageView imgCheckbox = (ImageView) view.findViewById(R.id.img_item_state);
        final ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow);
        final RecyclerView rec = (RecyclerView) view.findViewById(R.id.rec);
        for (ApkBean bean :apkFiles) {
            apkScanTotalSize += bean.getSize();
        }
        setCheckboxImgAndTag(imgCheckbox, 2);
        apkCleanTotalSize = apkScanTotalSize;
        tvTotalSize.setText(Utils.formatSize(apkScanTotalSize));
        final RecyclerView.Adapter adapter = new ApkInfoAdapter(this, apkFiles, new ApkItemSelectedListener() {
            @Override
            public void itemSelectedChanged(long size) {
                if(size == apkScanTotalSize){
                    setCheckboxImgAndTag(imgCheckbox, 2);
                }else if(size == 0){
                    setCheckboxImgAndTag(imgCheckbox, 0);
                }else{
                    setCheckboxImgAndTag(imgCheckbox, 1);
                }
                apkCleanTotalSize = size;
                setCleanTotalSize();
                tvTotalSize.setText(Utils.formatSize(size));
            }
        });
        imgCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = (int) v.getTag();
                switch(state){
                    case 0:
                        for (int i = 0; i < apkFiles.size(); i++) {
                            apkFiles.get(i).setSelected(true);
                        }
                        adapter.notifyDataSetChanged();
                        setCheckboxImgAndTag(imgCheckbox, 2);
                        apkCleanTotalSize = apkScanTotalSize;
                        setCleanTotalSize();
                        break;
                    case 1:
                        for (int i = 0; i < apkFiles.size(); i++) {
                            apkFiles.get(i).setSelected(true);
                        }
                        adapter.notifyDataSetChanged();
                        tvTotalSize.setText(Utils.formatSize(apkScanTotalSize));
                        setCheckboxImgAndTag(imgCheckbox, 2);
                        apkCleanTotalSize = apkScanTotalSize;
                        setCleanTotalSize();
                        break;
                    case 2:
                        for (int i = 0; i < apkFiles.size(); i++) {
                            apkFiles.get(i).setSelected(false);
                        }
                        adapter.notifyDataSetChanged();
                        setCheckboxImgAndTag(imgCheckbox, 0);
                        apkCleanTotalSize = 0;
                        setCleanTotalSize();
                        break;
                }
            }

        });
        rec.setAdapter(adapter);
        rec.setLayoutManager(new LinearLayoutManager(this));
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rec.isShown()){
                    rec.setVisibility(View.GONE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 180.0f, 360.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }else{
                    rec.setVisibility(View.VISIBLE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 0.0f, 180.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }
            }
        });
        rec.setVisibility(View.GONE);
        llContainerItems.addView(view);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
        view.startAnimation(animation);

    }

    private void setSaveSpaceViews(LinearLayout llContainerItems) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_save_space, null, false);
        LinearLayout llSaveSpace = (LinearLayout) view.findViewById(R.id.ll_save_space);
        final LinearLayout llImgCompress = (LinearLayout) view.findViewById(R.id.ll_img_compress);
        final LinearLayout llVideoCompress = (LinearLayout) view.findViewById(R.id.ll_video_compress);
        final ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow);
        llSaveSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(llImgCompress.isShown()){
                    llImgCompress.setVisibility(View.GONE);
                    llVideoCompress.setVisibility(View.GONE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 180.0f, 360.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }else{
                    llImgCompress.setVisibility(View.VISIBLE);
                    llVideoCompress.setVisibility(View.VISIBLE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 0.0f, 180.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }
            }
        });
        llImgCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CleanActivity.this, ImgCompressActivity.class));
            }
        });
        llVideoCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CleanActivity.this, VideoCompressActivity.class));
            }
        });
        llContainerItems.addView(view);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
        view.startAnimation(animation);
    }


    private void setWxCleanViews(LinearLayout llContainerItems) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_wx_scan_result, null, false);
        LinearLayout llWx = (LinearLayout) view.findViewById(R.id.ll_wx);
        TextView tvTotalSize = (TextView) view.findViewById(R.id.tv_total_size);
        ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow_right);
        llWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CleanActivity.this,WxCleanActiviry.class));
            }
        });
        long size = 0;
        for (WxCacheBean bean : wxCacheFiles) {
            size += bean.getSize();
        }
        tvTotalSize.setText(Utils.formatSize(size));
        llContainerItems.addView(view);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
        view.startAnimation(animation);

    }

    private void setQQCleanViews(LinearLayout llContainerItems) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_wx_scan_result, null, false);
        LinearLayout llWx = (LinearLayout) view.findViewById(R.id.ll_wx);
        TextView tvTotalSize = (TextView) view.findViewById(R.id.tv_total_size);
        ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow_right);
        ImageView imgIcon = (ImageView) view.findViewById(R.id.img_arrow);
        imgIcon.setImageResource(R.drawable.icon_qq);
        llWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CleanActivity.this, QQCleanActivity.class));
            }
        });
        tvTotalSize.setText("");
        llContainerItems.addView(view);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
        view.startAnimation(animation);

    }

    private void initViews() {

        adapterApks = new ApkInfoAdapter(this, apks, new ApkItemSelectedListener() {
            @Override
            public void itemSelectedChanged(long size) {
            }
        });

        initScanBeginViews();

        btn = (Button) findViewById(R.id.btn_clean);
        btn.setEnabled(false);

    }

    /**
     * 初始化扫描开始时视图
     */
    private void initScanBeginViews() {
        customLoadingApk = (DotRotateLoadingView) findViewById(R.id.custom_loading_apk);
        customLoadingAd = (DotRotateLoadingView) findViewById(R.id.custom_loading_ad);
        customLoadingCache = (DotRotateLoadingView) findViewById(R.id.custom_loading_cache);
        customLoadingUninstall = (DotRotateLoadingView) findViewById(R.id.custom_loading_uninstall);
//        customLoadingImg = (DotRotateLoadingView) findViewById(R.id.custom_loading_img);
//        customLoadingWx = (DotRotateLoadingView) findViewById(R.id.custom_loading_wx);

        customLoadingApk.startRotating();
        customLoadingAd.startRotating();
        customLoadingCache.startRotating();
        customLoadingUninstall.startRotating();
//        customLoadingImg.startRotating();
//        customLoadingWx.startRotating();

        imgLoadedAd = (ImageView) findViewById(R.id.img_loaded_ad);
        imgLoadedApk = (ImageView) findViewById(R.id.img_loaded_apk);
        imgLoadedCache = (ImageView) findViewById(R.id.img_loaded_cache);
        imgLoadedUninstall = (ImageView) findViewById(R.id.img_loaded_uninstall);
//        imgLoadedImg = (ImageView) findViewById(R.id.img_loaded_img);
//        imgLoadedWx = (ImageView) findViewById(R.id.img_loaded_wx);

        //顶部视图初始化
        tvCleanTotal = (TextView) findViewById(R.id.tv_clean_total);
        tvScanTotal = (TextView) findViewById(R.id.tv_clean_scan_total);

        tvCleanTotal.setVisibility(View.GONE);

        llContainerItems = (LinearLayout) findViewById(R.id.ll_container_items);

    }


    private void deleteApk() {
        for (int i = 0; i < apkFiles.size(); i++) {
            ApkBean bean = apkFiles.get(i);
            if (bean.isSelected()) {
                File file = bean.getFile();
                boolean hasDeleted = file.delete();
                Log.e(TAG, "是否删除：" + String.valueOf(hasDeleted));
            }
        }
    }

    public void setCheckboxImgAndTag(ImageView img, int state){
        if(state == 0){
            img.setImageResource(R.drawable.checkbox_unselected);
            img.setTag(0);
        }else if(state == 1){
            img.setImageResource(R.drawable.checkbox_selected_part);
            img.setTag(1);
        }else if(state ==2){
            img.setImageResource(R.drawable.checkbox_selected);
            img.setTag(2);
        }

    }

}
