package com.syezon.clean;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.ApkInfoAdapter;
import com.syezon.clean.adapter.ImgCompressAdapter;
import com.syezon.clean.adapter.WxBlogImageAdapter;
import com.syezon.clean.bean.ApkBean;
import com.syezon.clean.bean.ImgCompressBean;
import com.syezon.clean.bean.ImgCompressFileBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RunnableFuture;

public class ImgCompressActivity extends AppCompatActivity {

    private static final int GET_IMAGE_FINISHED = 11;
    private static final int GET_IMAGE_FAILED = 12;

    private LinearLayout llContainerItems;
    private DotRotateLoadingView customLoadingCompress;
    private Button btn;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_compress);
        initViews();
        initHandler();
        initData();
    }


    private void initData() {
        getImgCompress();
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case GET_IMAGE_FINISHED://成功获取图片列表
                        setScanImageFinished(llContainerItems);
                        break;
                    case GET_IMAGE_FAILED:

                        break;

                }
            }
        };
    }



    private void getImgCompress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = ImgCompress.getImages(ImgCompressActivity.this);
                if(result){
                    mHandler.sendEmptyMessage(GET_IMAGE_FINISHED);
                }else{
                    mHandler.sendEmptyMessage(GET_IMAGE_FAILED);
                }
            }
        }).start();
    }

    private void initViews() {
        llContainerItems = (LinearLayout) findViewById(R.id.ll_container_items);
        customLoadingCompress = (DotRotateLoadingView) findViewById(R.id.custom_loading_compress);
        customLoadingCompress.startRotating();
        btn = (Button) findViewById(R.id.btn_clean);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ImgCompress.cameraList != null && ImgCompress.cameraList.size() > 1){
                    ImgCompress.compress(ImgCompress.cameraList.get(ImgCompress.cameraList.size()-1).getPath());
                    ImgCompress.compress(ImgCompress.cameraList.get(ImgCompress.cameraList.size()-2).getPath());
                }
            }
        });
    }

    private void setScanImageFinished(LinearLayout llContainerItems) {
//        llContainerItems.removeAllViews();
//        View view = LayoutInflater.from(this).inflate(R.layout.item_compress_img_scan_result, null, false);
//        LinearLayout llItem = (LinearLayout) view.findViewById(R.id.ll_item_title);
//        final ImageView imgCheckbox = (ImageView) view.findViewById(R.id.img_item_state);
//        final ImageView imgArrow = (ImageView) view.findViewById(R.id.img_arrow);
//        final RecyclerView rec = (RecyclerView) view.findViewById(R.id.rec);
//        long size = 0;
//
//        RecyclerView.Adapter adapter = new ImgCompressAdapter(this, ImgCompress.cameraList, new ApkItemSelectedListener() {
//            @Override
//            public void itemSelectedChanged(long size) {
//                if(size > 0){
//                    imgCheckbox.setImageResource(R.drawable.checkbox_selected_part);
//                }
//            }
//        });
//        rec.setAdapter(adapter);
//        rec.setLayoutManager(new GridLayoutManager(this, 3));
//        llItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(rec.isShown()){
//                    rec.setVisibility(View.GONE);
//                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 180.0f, 360.0f)
//                            .setDuration(200);
//                    anima.setInterpolator(new LinearInterpolator());
//                    anima.start();
//                }else{
//                    rec.setVisibility(View.VISIBLE);
//                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrow, "rotation", 0.0f, 180.0f)
//                            .setDuration(200);
//                    anima.setInterpolator(new LinearInterpolator());
//                    anima.start();
//                }
//            }
//        });
//        rec.setVisibility(View.GONE);
//        llContainerItems.addView(view);
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
//        view.startAnimation(animation);
    }
}
