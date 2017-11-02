package com.syezon.clean;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.clean.bean.QQCacheBean;
import com.syezon.clean.utils.QQScanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * QQ 接收的文件所在文件夹 /tencent/QQfile_recv
 * QQ 语音缓存所在文件夹(amr 文件) /tencten/MobileQQ/${QQ号码}/ptt
 * QQ 聊天图片所咋文件夹   /tencent/MobileQQ/photo
 * QQ 小视频    /tencent/MobileQQ/shortvideo    thumbs:缩略图文件夹
 *
 *
 */

public class QQCleanActivity extends AppCompatActivity {

    private static final int GET_TALKING_CACHE = 11;
    private static final int GET_FILE_BEAN = 12;
    private ImageView imgLoadedGarbage,  imgLoadedTalking;
    private DotRotateLoadingView customLoadingGarbage, customLoadingTalking;
    private LinearLayout llContainerItems;
    private TextView tvScanTotalSize;

    public static List<QQCacheBean> listPhotos = new ArrayList<>();
    public static List<QQCacheBean> listVideos = new ArrayList<>();
    public static List<QQCacheBean> listVoices = new ArrayList<>();
    public static List<QQCacheBean> listRecives = new ArrayList<>();

    private long scanTotalSize;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqclean);

        initViews();
        initHandler();
        initData();
    }


    /**
     * 初始化数据
     */
    private void initData() {
        //TODO 扫描垃圾缓存 垃圾文件、临时文件

        new Thread(new Runnable() {
            @Override
            public void run() {
                QQScanUtils.getTalkingCacheFile(new QQScanUtils.QQScanListener() {
                    @Override
                    public void getFile(QQCacheBean bean) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = GET_FILE_BEAN;
                        msg.obj = bean;
                        mHandler.sendMessage(msg);
                    }
                    @Override
                    public void scanFinished() {
                        mHandler.sendEmptyMessage(GET_TALKING_CACHE);
                    }
                });
            }
        }).start();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case GET_TALKING_CACHE:
                        setScanResultViews();
                        break;
                    case GET_FILE_BEAN:
                        if(msg.obj instanceof QQCacheBean){
                            QQCacheBean bean = (QQCacheBean) msg.obj;
                            initCacheFile(bean);
                            scanTotalSize += bean.getSize();
                            tvScanTotalSize.setText(Utils.formatSize(scanTotalSize));
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initCacheFile(QQCacheBean bean) {
        switch(bean.getType()){
            case "photo":
                listPhotos.add(bean);
                break;
            case "ptt":
                listVoices.add(bean);
                break;
            case "shortvideo":
                listVideos.add(bean);
                break;
            case "recvive":
                listRecives.add(bean);
                break;
        }
    }


    private void initViews() {
        imgLoadedGarbage = (ImageView) findViewById(R.id.img_loaded_garbage);
        imgLoadedTalking = (ImageView) findViewById(R.id.img_loaded_talking);
        customLoadingGarbage = (DotRotateLoadingView) findViewById(R.id.custom_loading_garbage);
        customLoadingTalking = (DotRotateLoadingView) findViewById(R.id.custom_loading_talking);

        customLoadingGarbage.startRotating();
        customLoadingTalking.startRotating();

        llContainerItems = (LinearLayout) findViewById(R.id.ll_container_items);

        tvScanTotalSize = (TextView) findViewById(R.id.tv_clean_scan_total);

    }

    private void setScanResultViews() {
        llContainerItems.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.item_qq_scan_result, null, false);
        RelativeLayout rlTalkingImg = (RelativeLayout) view.findViewById(R.id.rl_talking_imgs);
        RelativeLayout rlVideo = (RelativeLayout) view.findViewById(R.id.rl_video);
        RelativeLayout rlTalkingSound = (RelativeLayout) view.findViewById(R.id.rl_talking_sound);
        RelativeLayout rlReceive = (RelativeLayout) view.findViewById(R.id.rl_receive);
        LinearLayout llTitle = (LinearLayout) view.findViewById(R.id.ll_item_title);
        final ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow);
        final LinearLayout container = (LinearLayout) view.findViewById(R.id.ll_talking_item);
        if(listPhotos.size() < 1){
            rlTalkingImg.setVisibility(View.GONE);
        }
        if(listVideos.size() < 1){
            rlVideo.setVisibility(View.GONE);
        }
        if(listVoices.size() < 1){
            rlTalkingSound.setVisibility(View.GONE);
        }
        if(listRecives.size() < 1){
            rlReceive.setVisibility(View.GONE);
        }
        rlTalkingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QQCleanActivity.this, QQTalkingImgActivity.class);
                intent.putExtra("source", QQTalkingImgActivity.SOURCE_IMAGE);
                startActivity(intent);
            }
        });

        rlVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QQCleanActivity.this, QQTalkingImgActivity.class);
                intent.putExtra("source", QQTalkingImgActivity.SOURCE_VIDEO);
                startActivity(intent);
            }
        });

        rlTalkingSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QQCleanActivity.this, VoiceCleanActivity.class);
                intent.putExtra("source", VoiceCleanActivity.SOURCE_TYPE_QQ);
                startActivity(intent);
            }
        });

        rlReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QQCleanActivity.this, QQReceiveActivity.class));
            }
        });
        llTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(container.isShown()){
                    container.setVisibility(View.GONE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 180.0f, 360.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }else{
                    container.setVisibility(View.VISIBLE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 0.0f, 180.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }
            }
        });

        llContainerItems.addView(view);
    }
}
