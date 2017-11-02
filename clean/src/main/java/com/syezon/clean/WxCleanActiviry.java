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
    private static final int GET_SCAN_TALKING_FILE = 14;

    private ImageView imgLoadedGarbage, imgLoadedProgram, imgLoadedTalking;
    private DotRotateLoadingView customLoadingGarbage, customLoadingProgram, customLoadingTalking;

    private Button btn;
    private TextView tvCleanScanTotal;
    private LinearLayout llContainerItem;

    private List<File> listGarbage = new ArrayList<>();
    private List<File> listCache = new ArrayList<>();
    public static List<WxCacheBean> listWxCache = new ArrayList<>();
    public static List<WxCacheBean> listTalkingVideo = new ArrayList<>();
    public static List<WxCacheBean> listTalkingVideoCover = new ArrayList<>();
    public static List<WxCacheBean> listBlogVideo = new ArrayList<>();
    public static List<WxCacheBean> listBlogImage = new ArrayList<>();
    public static List<WxCacheBean> listTalkingImage = new ArrayList<>();
    public static List<WxCacheBean> listVoice = new ArrayList<>();
    public static List<WxCacheBean> listReceiveFiles = new ArrayList<>();


    private Handler mHandler;
    private long scanTotalSize;//扫描结果总和


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_clean_activiry);
        initHandler();
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
        //设置显示视图背景
        if(llContainerItem.getChildCount() > 0){
            for (int i = 0; i < llContainerItem.getChildCount() - 1; i++) {
                View view = llContainerItem.getChildAt(i);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if(layoutParams instanceof LinearLayout.LayoutParams){
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutParams;
                    params.setMargins(0, 0, 0, 40);
                    view.setLayoutParams(params);
                }
            }
        }

        btn.setEnabled(true);
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
        //TODO 扫描小程序文件
    }

    /**
     * 获取聊天文件
     */
    private void getTalking() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<WxCacheBean> talkingFile = WxUtils.getTalkingFile(new WxUtils.WxScanListener() {
                    @Override
                    public void getFile(WxCacheBean bean) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = GET_SCAN_TALKING_FILE;
                        msg.obj = bean;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void scanFinished() {
                        mHandler.sendEmptyMessage(GET_TALKING_FINISHED);
                    }
                });
                listWxCache.addAll(talkingFile);
            }
        }).start();
    }

    /**
     * 初始化数据
     */
    private void initData() {
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

                    case GET_SCAN_TALKING_FILE://获取到微信文件
                        if(msg.obj instanceof  WxCacheBean) {
                            WxCacheBean bean = (WxCacheBean) msg.obj;
                            addScanTalkiingFile(bean);
                            scanTotalSize += bean.getSize();
                            tvCleanScanTotal.setText(Utils.formatSize(scanTotalSize));
                        }
                        break;
                }
            }
        };
    }

    /**
     * 添加扫描到的微信聊天图片到集合中
     * @param bean
     */
    private void addScanTalkiingFile(WxCacheBean bean) {
        switch(bean.getType()){
            case "video"://聊天视频
                if(bean.getFile().getName().endsWith("mp4")){
                    listTalkingVideo.add(bean);
                }else if(bean.getFile().getName().endsWith("jpg")){
                    listTalkingVideoCover.add(bean);
                }

                break;
            case "sns"://朋友圈图片和视频
                if(bean.getFileType().endsWith("mp4")){
                    listBlogVideo.add(bean);
                }else{
                    listBlogImage.add(bean);
                }
                break;
            case "image2":
                listTalkingImage.add(bean);
                break;
            case "image":
                listBlogImage.add(bean);
                break;
            case "voice2":
                listVoice.add(bean);
                break;
            case "download":
                listReceiveFiles.add(bean);
                break;
        }
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
        btn.setEnabled(false);
    }

    private void setGarbageAndCache(LinearLayout llContainerItem) {
//        View view = LayoutInflater.from(this).inflate(R.layout.item_wx_talking_scan_finished, null, false);
//        llContainerItem.addView(view);
    }

    private void setTalkingViews(LinearLayout llContainerItem) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_wx_talking_scan_finished, null, false);
        final LinearLayout llItemTitle = (LinearLayout) view.findViewById(R.id.ll_item_title);
        final LinearLayout llTalkingItems = (LinearLayout) view.findViewById(R.id.ll_talking_item);
        llItemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llTalkingItems.isShown()){
                    llTalkingItems.setVisibility(View.GONE);
                }else{
                    llTalkingItems.setVisibility(View.VISIBLE);
                }
            }
        });
        RelativeLayout rlTalkingImg = (RelativeLayout) view.findViewById(R.id.rl_talking_imgs);
        RelativeLayout rlVideo = (RelativeLayout) view.findViewById(R.id.rl_video);
        RelativeLayout rlTalkingSound = (RelativeLayout) view.findViewById(R.id.rl_talking_sound);
        RelativeLayout rlReceive = (RelativeLayout) view.findViewById(R.id.rl_receive);
        //判断是否具有数据，没有不显示
        if(listTalkingVideo.size() > 0 || listBlogVideo.size() > 0){
            rlVideo.setVisibility(View.VISIBLE);
        }
        if(listTalkingImage.size() > 0 || listBlogImage.size() > 0){
            rlTalkingImg.setVisibility(View.VISIBLE);
        }
        if(listVoice.size() > 0){
            rlTalkingSound.setVisibility(View.VISIBLE);
        }
        if(listReceiveFiles.size() > 0){
            rlReceive.setVisibility(View.VISIBLE);
        }

        rlVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WxCleanActiviry.this, WxVideoCleanActivity.class);
                intent.putExtra("source", WxVideoCleanActivity.SOURCE_VIDEO);
                startActivity(intent);
            }
        });

        rlTalkingSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WxCleanActiviry.this, VoiceCleanActivity.class);
                intent.putExtra("source", VoiceCleanActivity.SOURCE_TYPE_WX);
                startActivity(intent);
            }
        });

        rlTalkingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WxCleanActiviry.this, WxVideoCleanActivity.class);
                intent.putExtra("source", WxVideoCleanActivity.SOURCE_IMAGE);
                startActivity(intent);
            }
        });
        rlReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WxCleanActiviry.this, ReceiveActivity.class));
            }
        });

        llContainerItem.addView(view);
    }


}
