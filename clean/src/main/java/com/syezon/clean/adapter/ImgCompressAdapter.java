package com.syezon.clean.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.syezon.clean.R;
import com.syezon.clean.bean.ImgCompressBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.List;

/**
 * Created by June on 2017/9/7.
 */
public class ImgCompressAdapter extends RecyclerView.Adapter<ImgCompressAdapter.MyHolder> {

    private Context context;
    private final List<ImgCompressBean> list;
    private ApkItemSelectedListener listener;

    public ImgCompressAdapter(Context context, List<ImgCompressBean> list, ApkItemSelectedListener listener) {
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
        ImgCompressBean bean = list.get(position);
        Glide.with(context).load(bean.getPath()).override(200, 200).into(holder.icon);
        if(bean.isSelected()){
            holder.cb.setChecked(true);
        }else{
            holder.cb.setChecked(false);
        }
        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    list.get(tem_position).setSelected(true);
                }else{
                    list.get(tem_position).setSelected(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        CheckBox cb;

        public MyHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.img_icon);
            cb = (CheckBox) itemView.findViewById(R.id.cb);
        }
    }


}
