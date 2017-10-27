package com.syezon.clean;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.utils.WxUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WxCleanActiviry extends AppCompatActivity {


    private static final String TAG = WxCleanActiviry.class.getName();
    private static final int GET_GARBAGE_AND_LOG = 11;
    private static final int GET_GARBAGE_AND_LOG_FINISHED = 12;
    private static final int GET_TALKING_FINISHED = 13;

    private ImageView imgLoadedGarbage, imgLoadedProgram, imgLoadedTalking;
    private DotRotateLoadingView customLoadingGarbage, customLoadingProgram, customLoadingTalking;

    private Button btn;
    private TextView tvCleanScanTotal;
    private LinearLayout llContainerItem;

    private List<File> listGarbage = new ArrayList<>();
    private List<File> listCache = new ArrayList<>();
    public static List<WxCacheBean> listWxCache = new ArrayList<>();
    private Handler mHandler;

    private long scanTotalSize;//扫描结果总和


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_clean_activiry);
        initViews();

        initData();
    }


    /**
     * 判断是否完成全部扫描
     */
    private void checkFinishedAllScan() {
        llContainerItem.removeAllViews();
        if(listGarbage.size() > 0 || listCache.size() > 0){
            setGarbageAndCache(llContainerItem);
        }
//        if(listWxCache.size() > 0){
            setTalkingViews(llContainerItem);
//        }
    }



    private void getGarbageAndLog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                WxUtils.getGarbageAndCache(new FileScanUtil.ScanListener() {
                    @Override
                    public void getSuitableFile(File file) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = GET_GARBAGE_AND_LOG;
                        msg.obj = file;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void scanFinish() {
                        mHandler.sendEmptyMessage(GET_GARBAGE_AND_LOG_FINISHED);
                    }
                });
            }
        }).start();
    }

    private void getSmallProgram() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
    }

    /**
     * 获取聊天文件
     */
    private void getTalking() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<WxCacheBean> talkingFile = WxUtils.getTalkingFile();
                listWxCache.addAll(talkingFile);
                mHandler.sendEmptyMessage(GET_TALKING_FINISHED);
            }
        }).start();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        initHandler();
        getGarbageAndLog();
        getSmallProgram();
        getTalking();

    }




    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case GET_GARBAGE_AND_LOG:
                        File file = (File) msg.obj;
                        if(file.getName().endsWith(".log")){
                            listCache.add(file);
                        }else if(file.getName().endsWith(".temp")){
                            listGarbage.add(file);
                        }
                        Log.e(TAG, file.getAbsolutePath());
                        scanTotalSize += file.length();
                        tvCleanScanTotal.setText(Utils.formatSize(scanTotalSize));
                        break;
                    case GET_GARBAGE_AND_LOG_FINISHED:
                        customLoadingGarbage.cleanRotating();
                        customLoadingGarbage.setVisibility(View.GONE);
                        imgLoadedGarbage.setVisibility(View.VISIBLE);
                        break;
                    case GET_TALKING_FINISHED:
                        customLoadingTalking.cleanRotating();
                        customLoadingTalking.setVisibility(View.GONE);
                        imgLoadedTalking.setVisibility(View.VISIBLE);

                        checkFinishedAllScan();
                        break;
                }
            }
        };
    }


    private void initViews() {
        imgLoadedGarbage = (ImageView) findViewById(R.id.img_loaded_garbage);
        imgLoadedProgram = (ImageView) findViewById(R.id.img_loaded_program);
        imgLoadedTalking = (ImageView) findViewById(R.id.img_loaded_talking);
        customLoadingGarbage = (DotRotateLoadingView) findViewById(R.id.custom_loading_garbage);
        customLoadingProgram = (DotRotateLoadingView) findViewById(R.id.custom_loading_program);
        customLoadingTalking = (DotRotateLoadingView) findViewById(R.id.custom_loading_talking);

        customLoadingGarbage.startRotating();
        customLoadingTalking.startRotating();
        customLoadingProgram.startRotating();

        btn = (Button) findViewById(R.id.btn_clean);
        tvCleanScanTotal = (TextView) findViewById(R.id.tv_clean_scan_total);
        llContainerItem = (LinearLayout) findViewById(R.id.ll_container_items);
    }

    private void setGarbageAndCache(LinearLayout llContainerItem) {
//        View view = LayoutInflater.from(this).inflate(R.layout.item_wx_talking_scan_finished, null, false);
//        llContainerItem.addView(view);
    }

    private void setTalkingViews(LinearLayout llContainerItem) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_wx_talking_scan_finished, null, false);
        RelativeLayout rlTalkingImg = (RelativeLayout) view.findViewById(R.id.rl_talking_imgs);
        RelativeLayout rlVideo = (RelativeLayout) view.findViewById(R.id.rl_video);
        RelativeLayout rlTalkingSound = (RelativeLayout) view.findViewById(R.id.rl_talking_sound);
        RelativeLayout rlReceive = (RelativeLayout) view.findViewById(R.id.rl_receive);

        rlTalkingSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WxCleanActiviry.this, WxVoiceActivity.class));
            }
        });

        rlTalkingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WxCleanActiviry.this, WxTalkingImgActivity.class));
            }
        });

        llContainerItem.addView(view);
    }



}