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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.ApkInfoAdapter;
import com.syezon.clean.adapter.AppCacheAdapter;
import com.syezon.clean.bean.ApkBean;
import com.syezon.clean.bean.AppCacheBean;
import com.syezon.clean.bean.ImgCompressBean;
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

    private DotRotateLoadingView customLoadingApk;
    private ImageView imgLoadedApk;
    private DotRotateLoadingView customLoadingAd;
    private ImageView imgLoadedAd;
    private DotRotateLoadingView customLoadingCache;
    private ImageView imgLoadedCache;
    private DotRotateLoadingView customLoadingImg;
    private ImageView imgLoadedImg;
    private DotRotateLoadingView customLoadingUninstall;
    private ImageView imgLoadedUninstall;
    private DotRotateLoadingView customLoadingWx;
    private ImageView imgLoadedWx;

    private TextView tvCleanTotal, tvScanTotal;

    private LinearLayout llContainerItems;

    private long scanSize;//扫描出来可以节省的空间
    private int scanFinishedItem;//扫描完成条目数

    private Handler mHandler;
    private List<ApkBean> apks = new ArrayList<>();
    private RecyclerView.Adapter adapterApks;

    private List<AppCacheBean> listAppCaches = new ArrayList<>();
    private RecyclerView.Adapter adapterCache;
    public static List<WxCacheBean> wxCacheFiles = new ArrayList<>();
    public List<ApkBean> apkFiles = new ArrayList<>();



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
//        getWxCache();
//        getPics();
//        getVideos();

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
                        customLoadingWx.cleanRotating();
                        customLoadingWx.setVisibility(View.GONE);
                        imgLoadedWx.setVisibility(View.VISIBLE);
                        scanFinishedItem++;
                        checkFinishedAllScan();
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

    /**
     * 获取视频文件
     */
    private void getVideos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoCompress.getVideos(CleanActivity.this.getApplicationContext());
            }
        }).start();
    }

    /**
     * 获取要压缩的图片
     */
    private void getPics() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = ImgCompress.getImages(CleanActivity.this);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        long size = 0;
                        for (ImgCompressBean bean :ImgCompress.screenshotList) {
                            size += bean.getSaveSize();
                        }

                        for (ImgCompressBean bean :ImgCompress.cameraList) {
                            size += bean.getSaveSize();
                        }

                    }
                });
            }
        }).start();
    }

    private void getWxCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                wxCacheFiles.addAll(CleanManager.searchWx(CleanActivity.this));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(SCAN_WX_FINISHED);
                    }
                });
            }
        }).start();
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
//            if(wxCacheFiles.size() > 0){
                setWxCleanViews(llContainerItems);
//            }

            //添加QQ专清视图判断内存中是否具有QQ安装文件夹
            setQQCleanViews(llContainerItems);

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
            size += bean.getSize();
        }
        tvTotalSize.setText(Utils.formatSize(size));
        RecyclerView.Adapter adapter = new AppCacheAdapter(this, listAppCaches, new ApkItemSelectedListener() {
            @Override
            public void itemSelectedChanged(long size) {
                if(size > 0){
                    imgCheckbox.setImageResource(R.drawable.checkbox_selected_part);
                }
                tvTotalSize.setText(Utils.formatSize(size));
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

    private void setApkFilesViews(LinearLayout llContainerItems) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_clean_scan_finished, null, false);
        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item_title);
        final TextView tvTotalSize = (TextView) view.findViewById(R.id.tv_total_size);
        final ImageView imgCheckbox = (ImageView) view.findViewById(R.id.img_item_state);
        final ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow);
        final RecyclerView rec = (RecyclerView) view.findViewById(R.id.rec);
        long size = 0;
        for (ApkBean bean :apkFiles) {
            size += bean.getSize();
        }
        tvTotalSize.setText(Utils.formatSize(size));
        RecyclerView.Adapter adapter = new ApkInfoAdapter(this, apkFiles, new ApkItemSelectedListener() {
            @Override
            public void itemSelectedChanged(long size) {
                if(size > 0){
                    imgCheckbox.setImageResource(R.drawable.checkbox_selected_part);
                }
                tvTotalSize.setText(Utils.formatSize(size));
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

        initWxViews();

        initCompressViews();

        initVideoViews();


    }

    /**
     * 初始化扫描开始时视图
     */
    private void initScanBeginViews() {
        customLoadingApk = (DotRotateLoadingView) findViewById(R.id.custom_loading_apk);
        customLoadingAd = (DotRotateLoadingView) findViewById(R.id.custom_loading_ad);
        customLoadingCache = (DotRotateLoadingView) findViewById(R.id.custom_loading_cache);
        customLoadingImg = (DotRotateLoadingView) findViewById(R.id.custom_loading_img);
        customLoadingUninstall = (DotRotateLoadingView) findViewById(R.id.custom_loading_uninstall);
        customLoadingWx = (DotRotateLoadingView) findViewById(R.id.custom_loading_wx);

        customLoadingApk.startRotating();
        customLoadingAd.startRotating();
        customLoadingCache.startRotating();
        customLoadingImg.startRotating();
        customLoadingUninstall.startRotating();
        customLoadingWx.startRotating();

        imgLoadedAd = (ImageView) findViewById(R.id.img_loaded_ad);
        imgLoadedApk = (ImageView) findViewById(R.id.img_loaded_apk);
        imgLoadedCache = (ImageView) findViewById(R.id.img_loaded_cache);
        imgLoadedImg = (ImageView) findViewById(R.id.img_loaded_img);
        imgLoadedUninstall = (ImageView) findViewById(R.id.img_loaded_uninstall);
        imgLoadedWx = (ImageView) findViewById(R.id.img_loaded_wx);

        //顶部视图初始化
        tvCleanTotal = (TextView) findViewById(R.id.tv_clean_total);
        tvScanTotal = (TextView) findViewById(R.id.tv_clean_scan_total);

        llContainerItems = (LinearLayout) findViewById(R.id.ll_container_items);

    }

    private void initVideoViews() {
    }

    private void initCompressViews() {
    }

    private void initWxViews() {
    }

    private void deleteApk() {
        for (int i = 0; i < apks.size(); i++) {
            ApkBean bean = apks.get(i);
            if (bean.isSelected()) {
                File file = bean.getFile();
                boolean hasDeleted = file.delete();
                Log.e(TAG, "是否删除：" + String.valueOf(hasDeleted));
            }
        }
    }

}
