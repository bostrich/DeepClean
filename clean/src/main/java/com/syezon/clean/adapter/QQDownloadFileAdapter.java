package com.syezon.clean.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.syezon.clean.R;
import com.syezon.clean.Utils;
import com.syezon.clean.bean.QQCacheBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.List;

/**
 *
 */
public class QQDownloadFileAdapter extends RecyclerView.Adapter<QQDownloadFileAdapter.MyHolder> {

    private Context context;
    private List<QQCacheBean> list;
    private ApkItemSelectedListener listener;

    public QQDownloadFileAdapter(Context context, List<QQCacheBean> list, ApkItemSelectedListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_download, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final int tem_position = position;
        QQCacheBean bean = list.get(position);
        holder.tvName.setText(bean.getFile().getName());
        holder.tvSize.setText(Utils.formatSize(bean.getSize()));
        if(bean.isSelected()){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        TextView tvName;
        TextView tvSize;
        CheckBox checkBox;

        public MyHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSize = (TextView) itemView.findViewById(R.id.tv_size);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }


}
