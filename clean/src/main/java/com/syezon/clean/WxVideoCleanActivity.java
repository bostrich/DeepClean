package com.syezon.clean;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.WxVideoPagerAdapter;

public class WxVideoCleanActivity extends AppCompatActivity {

    public static final int SOURCE_VIDEO = 0;
    public static final int SOURCE_IMAGE = 1;
    public static final int SOURCE_VOICE = 2;


    private TabLayout tlTab;
    private ViewPager vp;
    private TextView tvTitle;
    private FragmentPagerAdapter adapter;

    private int sourceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_video_clean);

        if(getIntent().hasExtra("source")){
            sourceType = getIntent().getIntExtra("source", 0);
        }
        initViews();
    }

    private void initViews() {
        tlTab = (TabLayout) findViewById(R.id.tl_tab);
        vp = (ViewPager) findViewById(R.id.vp);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        adapter = new WxVideoPagerAdapter(this, getSupportFragmentManager(), sourceType);
        vp.setAdapter(adapter);
        tlTab.setupWithViewPager(vp);

        switch(sourceType){
            case SOURCE_IMAGE:
                tvTitle.setText("图片清理");
                break;
            case SOURCE_VIDEO:
                tvTitle.setText("小视频清理");
                break;
            case SOURCE_VOICE:
                tvTitle.setText("语音清理");
                break;
        }

    }

}
