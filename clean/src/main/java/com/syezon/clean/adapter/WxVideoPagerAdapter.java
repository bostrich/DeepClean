package com.syezon.clean.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.syezon.clean.fragment.VideoFragment;

/**
 *
 */

public class WxVideoPagerAdapter extends FragmentPagerAdapter {

    private Context context;


    public WxVideoPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return VideoFragment.newInstance(VideoFragment.TYPE_VIDEO_TALKING);
            case 1:
                return VideoFragment.newInstance(VideoFragment.TYPE_VIDEO_BLOG);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "聊天小视频";
            case 1:
                return "朋友圈视频";
            default:
                return "没有喽";
        }
    }
}
