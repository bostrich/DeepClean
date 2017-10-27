package com.syezon.clean;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.ImgCompressAdapter;
import com.syezon.clean.adapter.WxBlogImageAdapter;
import com.syezon.clean.bean.ImgCompressBean;
import com.syezon.clean.bean.ImgCompressFileBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImgCompressActivity extends AppCompatActivity {

    private LinearLayout llContent;
    private TextView tvCameraCount, tvCameraSize, tvScreenshotCount, tvScreenshotSize;
    private CheckBox cbCamera, cbScreenshot;
    private RecyclerView recCamera, recScreenshot;
    private LinearLayout llCamera, llCameraTitle, llScreenshot, llScreenshotTitle;
    private RecyclerView.Adapter adapterCamera, adapterScreenshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_compress);
        initViews();
        initData();
    }

    private void initData() {

        if(ImgCompress.cameraList.size() > 0){
            adapterCamera = new ImgCompressAdapter(this, ImgCompress.cameraList, null);
            recCamera.setAdapter(adapterCamera);
            recCamera.setLayoutManager(new GridLayoutManager(this, 4));
        }else{
            llCamera.setVisibility(View.GONE);
        }

        if(ImgCompress.screenshotList.size() > 0){
            adapterScreenshot = new ImgCompressAdapter(this, ImgCompress.screenshotList, null);
            recScreenshot.setAdapter(adapterScreenshot);
            recScreenshot.setLayoutManager(new GridLayoutManager(this, 4));
        }else{
            llScreenshot.setVisibility(View.GONE);
        }

    }

    private void initViews() {
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        tvCameraCount = (TextView) findViewById(R.id.tv_camera_count);
        tvCameraSize = (TextView) findViewById(R.id.tv_camera_total_size);
        tvScreenshotCount = (TextView) findViewById(R.id.tv_screenshot_count);
        tvScreenshotSize = (TextView) findViewById(R.id.tv_screenshot_total_size);

        cbCamera = (CheckBox) findViewById(R.id.cb_camera);
        cbCamera.setChecked(true);
        cbCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ImgCompress.cameraList.size() > 0){
                    if(isChecked){
                        for (ImgCompressBean bean :ImgCompress.cameraList) {
                            bean.setSelected(true);
                        }
                        adapterCamera.notifyDataSetChanged();
                    }else{
                        for (ImgCompressBean bean :ImgCompress.cameraList) {
                            bean.setSelected(false);
                        }
                        adapterCamera.notifyDataSetChanged();
                    }
                }
            }
        });
        cbScreenshot = (CheckBox) findViewById(R.id.cb_screenshot);
        cbScreenshot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ImgCompress.screenshotList.size() > 0){
                    if(isChecked){
                        for (ImgCompressBean bean :ImgCompress.screenshotList) {
                            bean.setSelected(true);
                        }
                        adapterScreenshot.notifyDataSetChanged();
                    }else{
                        for (ImgCompressBean bean :ImgCompress.screenshotList) {
                            bean.setSelected(false);
                        }
                        adapterScreenshot.notifyDataSetChanged();
                    }
                }
            }
        });

        recCamera = (RecyclerView) findViewById(R.id.rec_camera);
        recCamera.setVisibility(View.GONE);
        recScreenshot = (RecyclerView) findViewById(R.id.rec_screenshot);
        recScreenshot.setVisibility(View.GONE);

        llCamera = (LinearLayout) findViewById(R.id.ll_camera);
        llCameraTitle = (LinearLayout) findViewById(R.id.ll_camera_title);
        llCameraTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recCamera.isShown()){
                    recCamera.setVisibility(View.GONE);
                }else{
                    recCamera.setVisibility(View.VISIBLE);
                }
            }
        });
        llScreenshot = (LinearLayout) findViewById(R.id.ll_screentshot);
        llScreenshotTitle = (LinearLayout) findViewById(R.id.ll_screenshot_title);
        llScreenshotTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recScreenshot.isShown()){
                    recScreenshot.setVisibility(View.GONE);
                }else{
                    recScreenshot.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
