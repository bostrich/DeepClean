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

    private TabLayout tlTab;
    private ViewPager vp;
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_video_clean);
        initViews();
    }

    private void initViews() {
        tlTab = (TabLayout) findViewById(R.id.tl_tab);
        vp = (ViewPager) findViewById(R.id.vp);

        adapter = new WxVideoPagerAdapter(this, getSupportFragmentManager());
        vp.setAdapter(adapter);
        tlTab.setupWithViewPager(vp);

    }


}
