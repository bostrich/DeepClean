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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syezon.clean.adapter.WxBlogImageAdapter;
import com.syezon.clean.bean.ApkBean;
import com.syezon.clean.bean.ImgCompressBean;
import com.syezon.clean.bean.ImgCompressFileBean;
import com.syezon.clean.bean.ScanBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RunnableFuture;

public class ImgCompressActivity extends AppCompatActivity {

    private static final int GET_IMAGE_FINISHED = 11;
    private static final int GET_IMAGE_FAILED = 12;
    private static final int GET_FILE_BEAN = 13;

    private LinearLayout llContainerItems;
    private DotRotateLoadingView customLoadingCompress;
    private Button btn;
    private ImageView imgLoaded;
    private TextView tvScanTotalSize;

    public static List<ScanBean> listDcim = new ArrayList<>();
    public static List<ScanBean> listScreenshot = new ArrayList<>();

    private long scanTotalSize;

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
        listDcim.clear();
        listScreenshot.clear();
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

                    case GET_FILE_BEAN:
                        if(msg.obj instanceof ScanBean){
                            ScanBean bean = (ScanBean) msg.obj;
                            if(bean.getType().equals("dcim")){
                                listDcim.add(bean);
                            }else if(bean.getType().equals("screenShot")){
                                listScreenshot.add(bean);
                            }
                            scanTotalSize += bean.getSize();
                            tvScanTotalSize.setText(Utils.formatSize(scanTotalSize));
                        }
                        break;

                }
            }
        };
    }



    private void getImgCompress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImgCompress.getImages(ImgCompressActivity.this, new ImgCompress.ScanListener() {
                    @Override
                    public void getFile(ScanBean bean) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = GET_FILE_BEAN;
                        msg.obj = bean;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void scanFinished() {
                        mHandler.sendEmptyMessage(GET_IMAGE_FINISHED);
                    }
                });
            }
        }).start();
    }

    private void initViews() {
        llContainerItems = (LinearLayout) findViewById(R.id.ll_container_items);
        customLoadingCompress = (DotRotateLoadingView) findViewById(R.id.custom_loading_compress);
        customLoadingCompress.startRotating();
        imgLoaded = (ImageView) findViewById(R.id.img_loaded_garbage);
        btn = (Button) findViewById(R.id.btn_clean);
        tvScanTotalSize = (TextView) findViewById(R.id.tv_clean_scan_total);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setScanImageFinished(LinearLayout llContainerItems) {
        customLoadingCompress.cleanRotating();
        customLoadingCompress.setVisibility(View.GONE);
        imgLoaded.setVisibility(View.VISIBLE);

        llContainerItems.removeAllViews();

        if(listDcim.size() > 0){
            addItemView(listDcim, "手机拍照（" + listDcim.size() + "）");
        }
        if(listScreenshot.size() > 0){
            addItemView(listScreenshot, "手机截屏（" + listScreenshot.size() + "）" );
        }



    }

    private void addItemView(final List<ScanBean> list, String title) {
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

        final RecyclerView.Adapter adapter = new WxBlogImageAdapter(this, list, new ApkItemSelectedListener() {
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

        llContainerItems.addView(view);
    }
}
