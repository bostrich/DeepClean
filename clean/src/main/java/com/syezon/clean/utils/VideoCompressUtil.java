package com.syezon.clean.utils;


import android.content.Context;
import android.util.Log;


import com.syezon.clean.FFmpegNativeBridge;

/**
 * 视频压缩类
 * ffmpeg各个参数代表的意思:
 * -i:      设定输入流
 * -y:      覆盖输出文件
 * -c:v:    视频编码格式(-vcodec)
 * -c:a:    音频编码格式(-acodec)
 * -b:v:    视频码率(-b:v 10M 表示视频码率为10Mbps)
 * -b:a:    音频码率(-b:a 128K 表示音频码率为 128Kbps)
 * -vf:     缩放(-vf scale=iw/2:-1  iw : 是输入的宽度 -1: 通知缩放滤镜在输出时保持原始的宽高比)
 * -preset: 调节编码速度和质量的平衡(ultrafast 最快)
 */

public class VideoCompressUtil {


    private static final String TAG = VideoCompressUtil.class.getName();

    public static void doCompress (String path) {
        // test compress time
        // you need replace to your source
        long startTime = System.currentTimeMillis();
        String newPath = path.replace(".mp4", "_" + System.currentTimeMillis() + ".mp4");
        Log.e(TAG, "压缩视频路径：" + path);
        int ret = FFmpegNativeBridge.runCommand(new String[]{"ffmpeg",
                "-i", path,
                "-y",
                "-c:v", "libx264",
                "-c:a", "aac",
                "-vf", "scale=iw:-1",
                "-preset", "ultrafast",
                "-b:v", "450k",
                "-b:a", "96k",
                newPath});
        System.out.println("ret: " + ret + ", time: " + (System.currentTimeMillis() - startTime));

    }
}
