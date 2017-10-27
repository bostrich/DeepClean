package com.syezon.clean;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.syezon.clean.adapter.VoiceAdapter;
import com.syezon.clean.adapter.WxBlogImageAdapter;
import com.syezon.clean.bean.WxCacheBean;

import java.util.ArrayList;
import java.util.List;

public class WxTalkingImgActivity extends AppCompatActivity {

    private RecyclerView recLongTime, recHalfYear, recOneMonth;
    private RecyclerView.Adapter adapterOneMonth, adapterHalfYear, adapterLongTime;

    private List<WxCacheBean> listLongTime = new ArrayList<>();
    private List<WxCacheBean> listHalfYear = new ArrayList<>();
    private List<WxCacheBean> listOneMonth = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_talking_img);

        initView();
        initData();
    }

    private void initData() {
        for (int i = 0; i < WxCleanActiviry.listWxCache.size(); i++) {
            WxCacheBean bean = WxCleanActiviry.listWxCache.get(i);
            if(bean.getType().equals("image2") || bean.getType().equals("image") || bean.getType().equals("sns")){
                long time = System.currentTimeMillis() - bean.getFile().lastModified();
                if(time < 1000 * 60 * 60 * 24 * 30){
                    listOneMonth.add(bean);
                }else if(time < 1000 * 60 * 60 * 24 * 30 * 6){
                    listHalfYear.add(bean);
                }else{
                    listLongTime.add(bean);
                }
            }
        }
        adapterOneMonth = new WxBlogImageAdapter(this, listOneMonth,null);
        recOneMonth.setAdapter(adapterOneMonth);
        recOneMonth.setLayoutManager(new GridLayoutManager(this, 3));

        adapterHalfYear = new WxBlogImageAdapter(this, listHalfYear,null);
        recHalfYear.setAdapter(adapterHalfYear);
        recHalfYear.setLayoutManager(new GridLayoutManager(this, 3));

        adapterLongTime = new WxBlogImageAdapter(this, listLongTime,null);
        recLongTime.setAdapter(adapterLongTime);
        recLongTime.setLayoutManager(new GridLayoutManager(this, 3));
    }

    private void initView() {
        recOneMonth = (RecyclerView) findViewById(R.id.rec_onemonth);
        recLongTime = (RecyclerView) findViewById(R.id.rec_longtime);
        recHalfYear = (RecyclerView) findViewById(R.id.rec_halfyear);
    }
}
