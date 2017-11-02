package com.syezon.clean.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.syezon.clean.R;
import com.syezon.clean.Utils;
import com.syezon.clean.bean.QQCacheBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.List;

/**
 * Created by June on 2017/9/7.
 */
public class QQImageAdapter extends RecyclerView.Adapter<QQImageAdapter.MyHolder> {

    private Context context;
    private List<QQCacheBean> list;
    private ApkItemSelectedListener listener;

    public QQImageAdapter(Context context, List<QQCacheBean> list, ApkItemSelectedListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wx_blog_image, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final int tem_position = position;
        QQCacheBean bean = list.get(position);
        if(bean.getFileType().equals("mp4")){
            Glide.with(context).load(bean.getFile()).asBitmap().override(200, 200).into(holder.icon);
        }else{
            Glide.with(context).load(bean.getFile()).override(200, 200).into(holder.icon);
        }
        holder.tvSize.setText(Utils.formatSize(bean.getFile().length()));
        if(bean.isSelected()){
            holder.cb.setChecked(true);
        }else{
            holder.cb.setChecked(false);
        }
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.get(tem_position).setSelected(isChecked);
                long size = 0;
                for (int i = 0; i < list.size(); i++) {
                    QQCacheBean bean = list.get(i);
                    if(bean.isSelected()){
                        size += bean.getSize();
                    }
                }
                if(listener != null) listener.itemSelectedChanged(size);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView tvSize;
        CheckBox cb;

        public MyHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.img_icon);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
            cb = (CheckBox) itemView.findViewById(R.id.cb);
        }
    }


}
