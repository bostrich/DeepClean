package com.syezon.clean.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.syezon.clean.R;
import com.syezon.clean.Utils;
import com.syezon.clean.bean.ImgCompressBean;
import com.syezon.clean.bean.ScanBean;

import java.io.ByteArrayOutputStream;

/**
 *
 */

public class DialogUtil {

    public static void showImgCompress(Context context, ScanBean bean){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_img_compress);
        ImageView imgSource = (ImageView) dialog.findViewById(R.id.img_source);
        ImageView imgDes = (ImageView) dialog.findViewById(R.id.img_des);
        TextView tvSourceSize = (TextView) dialog.findViewById(R.id.tv_source_size);
        TextView tvDesSize = (TextView) dialog.findViewById(R.id.tv_des_size);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        if(bean instanceof ImgCompressBean){
            Bitmap bitmap = BitmapFactory.decodeFile(bean.getFile().getAbsolutePath());
            imgSource.setImageBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

            int length = baos.toByteArray().length;
            tvDesSize.setText(Utils.formatSize(length));
            imgDes.setImageBitmap(BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length));
        }
        tvSourceSize.setText(Utils.formatSize(bean.getSize()));
        dialog.show();

    }
}
