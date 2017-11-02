package com.syezon.clean;

import android.animation.ObjectAnimator;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.VoiceAdapter;
import com.syezon.clean.bean.QQCacheBean;
import com.syezon.clean.bean.ScanBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class VoiceCleanActivity extends AppCompatActivity {

    public static final int SOURCE_TYPE_WX = 1;
    public static final int SOURCE_TYPE_QQ = 2;

    private RecyclerView rec;
    private RecyclerView.Adapter adapter;
    private CheckBox cb;
    private LinearLayout llTitle;
    private ImageView imgArrow;
    private TextView tvTotalSize;

    private long scanTotalSize;
    private int source;

    private List<ScanBean> listVoice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_voice);

        initView();
        initData();
    }

    private void initData() {
        if(getIntent().hasExtra("source")){
            source = getIntent().getIntExtra("source", 0);
        }

        switch(source){
            case SOURCE_TYPE_WX:
                for (int i = 0; i < WxCleanActiviry.listVoice.size(); i++) {
                    WxCacheBean bean = WxCleanActiviry.listVoice.get(i);
                    listVoice.add(bean);
                    scanTotalSize += bean.getSize();
                }
                break;
            case SOURCE_TYPE_QQ:
                for (int i = 0; i < QQCleanActivity.listVoices.size(); i++) {
                    QQCacheBean bean = QQCleanActivity.listVoices.get(i);
                    listVoice.add(bean);
                    scanTotalSize += bean.getSize();
                }
                break;
        }

        adapter = new VoiceAdapter(this, listVoice, new ApkItemSelectedListener() {
            @Override
            public void itemSelectedChanged(long size) {
                if(size < scanTotalSize){
                    cb.setChecked(false);
                }else if(size == scanTotalSize){
                    cb.setChecked(true);
                }
            }
        });
        tvTotalSize.setText(Utils.formatSize(scanTotalSize));
        rec.setAdapter(adapter);
        rec.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initView() {
        rec = (RecyclerView) findViewById(R.id.rec);
        cb = (CheckBox) findViewById(R.id.cb);
        imgArrow = (ImageView) findViewById(R.id.img_arrow);
        tvTotalSize = (TextView) findViewById(R.id.tv_total_size);
        llTitle = (LinearLayout) findViewById(R.id.ll_item_title);
        llTitle.setOnClickListener(new View.OnClickListener() {
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
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked()){
                    cb.setChecked(true);
                    for (int i = 0; i < listVoice.size(); i++) {
                        listVoice.get(i).setSelected(true);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    cb.setChecked(false);
                    for (int i = 0; i < listVoice.size(); i++) {
                        listVoice.get(i).setSelected(false);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
