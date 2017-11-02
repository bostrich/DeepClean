package com.syezon.clean.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.syezon.clean.WxVideoCleanActivity;
import com.syezon.clean.fragment.VideoFragment;

/**
 *
 */

public class WxVideoPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private int source;


    public WxVideoPagerAdapter(Context context, FragmentManager fm, int source){
        super(fm);
        this.context = context;
        this.source = source;
    }


    @Override
    public Fragment getItem(int position) {
        if(source == WxVideoCleanActivity.SOURCE_IMAGE){
            switch(position){
                case 0:
                    return VideoFragment.newInstance(VideoFragment.TYPE_IMAGE_TALKING);
                case 1:
                    return VideoFragment.newInstance(VideoFragment.TYPE_IMAGE_BLOG);
            }

        }else if(source == WxVideoCleanActivity.SOURCE_VIDEO){
            switch(position){
                case 0:
                    return VideoFragment.newInstance(VideoFragment.TYPE_VIDEO_TALKING);
                case 1:
                    return VideoFragment.newInstance(VideoFragment.TYPE_VIDEO_BLOG);
            }
        }else if(source == WxVideoCleanActivity.SOURCE_VOICE){
            return VideoFragment.newInstance(VideoFragment.TYPE_VOICE);
        }

        return null;
    }

    @Override
    public int getCount() {
        switch(source){
            case WxVideoCleanActivity.SOURCE_IMAGE:
                return 2;
            case WxVideoCleanActivity.SOURCE_VIDEO:
                return 2;
            case WxVideoCleanActivity.SOURCE_VOICE:
                return 1;
        }
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(source == WxVideoCleanActivity.SOURCE_VIDEO){
            switch(position){
                case 0:
                    return "聊天小视频";
                case 1:
                    return "朋友圈视频";
                default:
                    return "没有喽";
            }
        }else if(source == WxVideoCleanActivity.SOURCE_IMAGE){
            switch(position){
                case 0:
                    return "聊天图片";
                case 1:
                    return "朋友圈图片";
                default:
                    return "没有喽";
            }
        }else if (source == WxVideoCleanActivity.SOURCE_VOICE){
            return "聊天语音";
        }
        return "没有喽";

    }
}
