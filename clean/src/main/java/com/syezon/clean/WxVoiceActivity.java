package com.syezon.clean;

import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.syezon.clean.adapter.VoiceAdapter;
import com.syezon.clean.bean.WxCacheBean;

import java.util.ArrayList;
import java.util.List;

public class WxVoiceActivity extends AppCompatActivity {

    private RecyclerView rec;
    private RecyclerView.Adapter adapter;

    private List<WxCacheBean> listVoice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_voice);

        initView();
        initData();
    }

    private void initData() {
        for (int i = 0; i < WxCleanActiviry.listWxCache.size(); i++) {
            WxCacheBean bean = WxCleanActiviry.listWxCache.get(i);
            if(bean.getType().equals("voice2")){
                listVoice.add(bean);
            }
        }
        adapter = new VoiceAdapter(this, listVoice,null);
        rec.setAdapter(adapter);
        rec.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initView() {
        rec = (RecyclerView) findViewById(R.id.rec);

    }
}
