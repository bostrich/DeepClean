package com.syezon.clean;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * QQ 接收的文件所在文件夹 /tencent/QQfile_recv
 * QQ 语音缓存所在文件夹(amr 文件) /tencten/MobileQQ/${QQ号码}/ptt
 * QQ 聊天图片所咋文件夹   /tencent/MobileQQ/photo
 * QQ 小视频    /tencent/MobileQQ/shortvideo    thumbs:缩略图文件夹
 *
 *
 */

public class QQCleanActivity extends AppCompatActivity {

    private ImageView imgLoadedGarbage,  imgLoadedTalking;
    private DotRotateLoadingView customLoadingGarbage, customLoadingTalking;
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



    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){

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

    }
}
