package com.syezon.clean;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syezon.clean.bean.QQCacheBean;
import com.syezon.clean.utils.QQScanUtils;

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
    private ImageView imgLoadedGarbage,  imgLoadedTalking;
    private DotRotateLoadingView customLoadingGarbage, customLoadingTalking;
    private LinearLayout llContainerItems;

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

        //TODO 扫描聊天文件 包括聊天视频，聊天文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<QQCacheBean> list = QQScanUtils.getTalkingCacheFile();
                mHandler.sendEmptyMessage(GET_TALKING_CACHE);
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
                    default:
                        break;
                }
            }
        };
    }



    private void initViews() {
        imgLoadedGarbage = (ImageView) findViewById(R.id.img_loaded_garbage);
        imgLoadedTalking = (ImageView) findViewById(R.id.img_loaded_talking);
        customLoadingGarbage = (DotRotateLoadingView) findViewById(R.id.custom_loading_garbage);
        customLoadingTalking = (DotRotateLoadingView) findViewById(R.id.custom_loading_talking);

        customLoadingGarbage.startRotating();
        customLoadingTalking.startRotating();

        llContainerItems = (LinearLayout) findViewById(R.id.ll_container_items);

    }

    private void setScanResultViews() {
        llContainerItems.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.item_qq_scan_result, null, false);
        RelativeLayout rlTalkingImg = (RelativeLayout) view.findViewById(R.id.rl_talking_imgs);
        RelativeLayout rlVideo = (RelativeLayout) view.findViewById(R.id.rl_video);
        RelativeLayout rlTalkingSound = (RelativeLayout) view.findViewById(R.id.rl_talking_sound);
        RelativeLayout rlReceive = (RelativeLayout) view.findViewById(R.id.rl_receive);


        llContainerItems.addView(view);
    }
}
