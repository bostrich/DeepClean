package com.syezon.clean.fragment;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.syezon.clean.R;
import com.syezon.clean.Utils;
import com.syezon.clean.WxCleanActiviry;
import com.syezon.clean.adapter.WxBlogImageAdapter;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    private static final String ARG_TYPE = "arg_type";
    public static final int TYPE_VIDEO_TALKING = 1;
    public static final int TYPE_VIDEO_BLOG = 2;


    private LinearLayout llContainer;


    private List<WxCacheBean> listThumbnail = new ArrayList<>();
    private List<WxCacheBean> listOneWeek = new ArrayList<>();
    private List<WxCacheBean> listOneMonth = new ArrayList<>();
    private List<WxCacheBean> listLongTime = new ArrayList<>();


    private int type;

    public VideoFragment() {

    }


    public static VideoFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        initItemView(view);
        initData();
        return view;
    }

    private void initData() {
        switch(type){
            case TYPE_VIDEO_TALKING:
                initVideoTalking();
                break;
            case TYPE_VIDEO_BLOG:
                initVideoBlogVideo();
                break;

        }


    }

    private void initVideoBlogVideo() {
        for (int i = 0; i < WxCleanActiviry.listBlogVideo.size(); i++) {
            WxCacheBean bean = WxCleanActiviry.listBlogVideo.get(i);
            long dayTime = 1000 * 60 * 60 * 24;
            if(System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 7){
                listOneWeek.add(bean);
            }else if(System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 30){
                listOneMonth.add(bean);
            }else{
                listLongTime.add(bean);
            }
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

    private void initVideoTalking() {
        for (int i = 0; i < WxCleanActiviry.listTalkingVideoCover.size(); i++) {
            listThumbnail.add(WxCleanActiviry.listTalkingVideoCover.get(i));
        }
        if(listThumbnail.size() > 0){
            addItemView(listThumbnail, "缩略图");
        }

        for (int i = 0; i < WxCleanActiviry.listTalkingVideo.size(); i++) {
            WxCacheBean bean = WxCleanActiviry.listTalkingVideo.get(i);
            long dayTime = 1000 * 60 * 60 * 24;
            if(System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 7){
                listOneWeek.add(bean);
            }else if(System.currentTimeMillis() - bean.getFile().lastModified() < dayTime * 30){
                listOneMonth.add(bean);
            }else{
                listLongTime.add(bean);
            }
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

    private void addItemView(final List<WxCacheBean> list, String title) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_wx_video_clean, null, false);
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

        final RecyclerView.Adapter adapter = new WxBlogImageAdapter(getContext(), list, new ApkItemSelectedListener() {
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
            float density = getContext().getResources().getDisplayMetrics().scaledDensity;
            int height = (int)(400.0 * density);
            layoutParams.height = height;
            recThumbnail.setLayoutParams(layoutParams);
        }
        recThumbnail.setLayoutManager(new GridLayoutManager(getContext(), 3));

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

        llContainer.addView(view);
    }

    private void initItemView(View view) {
        llContainer = (LinearLayout) view.findViewById(R.id.ll_container);
    }

}
