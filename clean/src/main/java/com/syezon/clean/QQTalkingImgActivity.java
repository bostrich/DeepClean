package com.syezon.clean;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.QQImageAdapter;
import com.syezon.clean.adapter.WxBlogImageAdapter;
import com.syezon.clean.bean.QQCacheBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class QQTalkingImgActivity extends AppCompatActivity {

    public static final int SOURCE_VIDEO = 1;
    public static final int SOURCE_IMAGE = 2;

    private LinearLayout container;

    private List<QQCacheBean> listThumbnail = new ArrayList<>();
    private List<QQCacheBean> listOneWeek = new ArrayList<>();
    private List<QQCacheBean> listOneMonth = new ArrayList<>();
    private List<QQCacheBean> listLongTime = new ArrayList<>();

    private int source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqtalking_img);
        initView();
        initData();
    }

    private void initData() {
        if(getIntent().hasExtra("source")){
            source = getIntent().getIntExtra("source", 0);
        }
        switch(source){
            case SOURCE_IMAGE:
                initImageData();
                break;
            case SOURCE_VIDEO:
                initVideoData();
                break;
        }

        if(listThumbnail.size() > 0){
            addItemView(listThumbnail, "缩略图");
        }

        if(listOneWeek.size() > 0){
            addItemView(listOneWeek, "一周内");
        }

        if(listOneMonth.size() > 0){
            addItemView(listOneMonth, "一个月内");
        }

        if(listLongTime.size() > 0 ){
            addItemView(listLongTime, "遥远的时代");
        }

    }

    private void initVideoData() {
        for (int i = 0; i < QQCleanActivity.listVideos.size(); i++) {
            QQCacheBean bean = QQCleanActivity.listVideos.get(i);
            if(bean.getFileType().equals("png")){
                listThumbnail.add(bean);
            }else if(bean.getFileType().equals("mp4")){
                long dayTime = 1000 * 60 * 60 * 24;
                if (System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 7) {
                    listOneWeek.add(bean);
                } else if (System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 30) {
                    listOneMonth.add(bean);
                } else {
                    listLongTime.add(bean);
                }
            }

        }
    }

    private void initImageData() {
        for (int i = 0; i < QQCleanActivity.listPhotos.size(); i++) {
            QQCacheBean bean = QQCleanActivity.listPhotos.get(i);
            long dayTime = 1000 * 60 * 60 * 24;
            if (System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 7) {
                listOneWeek.add(bean);
            } else if (System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 30) {
                listOneMonth.add(bean);
            } else {
                listLongTime.add(bean);
            }
        }
    }

    private void initView() {
        container = (LinearLayout) findViewById(R.id.ll_container);
    }


    private void addItemView(final List<QQCacheBean> list, String title) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_wx_video_clean, null, false);
        LinearLayout llThumbnail = (LinearLayout) view.findViewById(R.id.ll_thumbnail);
        final TextView tvTotalThumbnail = (TextView) view.findViewById(R.id.tv_total_thumbnail);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        final ImageView imgArrowThumbnail = (ImageView) view.findViewById(R.id.img_arrow_thumbnail);
        final CheckBox cbThumbnail = (CheckBox) view.findViewById(R.id.cb_thumbnail);
        final RecyclerView recThumbnail = (RecyclerView) view.findViewById(R.id.rec_thumbnail);
        llThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recThumbnail.isShown()){
                    recThumbnail.setVisibility(View.GONE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrowThumbnail, "rotation", 180.0f, 360.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }else{
                    recThumbnail.setVisibility(View.VISIBLE);
                    ObjectAnimator anima = ObjectAnimator.ofFloat(imgArrowThumbnail, "rotation", 0.0f, 180.0f)
                            .setDuration(200);
                    anima.setInterpolator(new LinearInterpolator());
                    anima.start();
                }
            }
        });
        long size = 0;
        for (int i = 0; i < list.size(); i++) {
            size += list.get(i).getSize();
        }
        final long totalSize = size;
        tvTotalThumbnail.setText(Utils.formatSize(size));

        final RecyclerView.Adapter adapter = new QQImageAdapter(this, list, new ApkItemSelectedListener() {
            @Override
            public void itemSelectedChanged(long size) {
                if(size == totalSize){
                    cbThumbnail.setChecked(true);
                }else{
                    cbThumbnail.setChecked(false);
                }
            }
        });

        recThumbnail.setAdapter(adapter);
        if(list.size() > 12){
            ViewGroup.LayoutParams layoutParams = recThumbnail.getLayoutParams();
            float density = getResources().getDisplayMetrics().scaledDensity;
            int height = (int)(400.0 * density);
            layoutParams.height = height;
            recThumbnail.setLayoutParams(layoutParams);
        }
        recThumbnail.setLayoutManager(new GridLayoutManager(this, 3));

        cbThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbThumbnail.isChecked()){
                    cbThumbnail.setChecked(true);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelected(true);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    cbThumbnail.setChecked(false);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setSelected(false);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        recThumbnail.setVisibility(View.GONE);

        container.addView(view);
    }
}
