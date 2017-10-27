package com.syezon.clean.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.clean.R;
import com.syezon.clean.Utils;
import com.syezon.clean.bean.ApkBean;
import com.syezon.clean.bean.WxCacheBean;
import com.syezon.clean.interfaces.ApkItemSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.MyHolder> {

    private Context context;
    private List<WxCacheBean> list;
    private ApkItemSelectedListener listener;

    public VoiceAdapter(Context context, List<WxCacheBean> list, ApkItemSelectedListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voice, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        WxCacheBean bean = list.get(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.tvName.setText(format.format(new Date(bean.getFile().lastModified())));
        holder.tvSize.setText(Utils.formatSize(bean.getSize()));

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
