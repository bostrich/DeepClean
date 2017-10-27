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

import com.syezon.clean.R;
import com.syezon.clean.R2;
import com.syezon.clean.Utils;
import com.syezon.clean.bean.ApkBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class ApkInfoAdapter extends RecyclerView.Adapter<ApkInfoAdapter.MyHolder> {

    private Context context;
    private List<ApkBean> list;
    private ApkItemSelectedListener listener;

    public ApkInfoAdapter(Context context, List<ApkBean> list, ApkItemSelectedListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_apk_info, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final int tem_position = position;
        ApkBean bean = list.get(position);
        if (bean.getIcon() != null) {
            holder.icon.setImageDrawable(bean.getIcon());
        }
        holder.appName.setText(bean.getAppName());
        holder.apkState.setText("/"  + bean.getState());
        holder.size.setText(Utils.formatSize(bean.getSize()));
        if(bean.isSelected()){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    list.get(tem_position).setSelected(true);
                }else{
                    list.get(tem_position).setSelected(false);
                }
                long totalSize = 0L;
                for (int i = 0; i < list.size(); i++) {
                    ApkBean bean = list.get(i);
                    if(bean.isSelected()){
                        totalSize += bean.getSize();
                    }
                }
                if(listener != null) listener.itemSelectedChanged(totalSize);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView appName;
        TextView apkState;
        TextView size;
        CheckBox checkBox;

        public MyHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.img_icon);
            appName = (TextView) itemView.findViewById(R.id.tv_app_name);
            apkState = (TextView) itemView.findViewById(R.id.tv_apk_state);
            size = (TextView) itemView.findViewById(R.id.tv_size);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }


}
