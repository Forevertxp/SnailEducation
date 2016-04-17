package com.snail.education.app;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.snail.education.R;
import com.snail.education.common.SEThemer;

/**
 * Created by tianxiaopeng on 15-1-7.
 */
public class SEAPP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SEThemer.getInstance().init(this);
        SEThemer.getInstance().setActionBarBackgroundColor(getResources().getColor(R.color.ActionBarBackgroundColor));
        SEThemer.getInstance().setActionBarForegroundColor(getResources().getColor(R.color.ActionBarForegroundColor));

        //final String API_BASE_URL = "http://api.nowthinkgo.com/";
        final String API_BASE_URL = "http://api.swiftacademy.cn:8080";
        SEConfig.getInstance().init(API_BASE_URL, "极速学院 v1.0", this);

        /**
         * 开源框架 Image-Loader
         */
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .showImageForEmptyUri(R.drawable.ic_launcher) //
                .showImageOnFail(R.drawable.ic_launcher) //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                .build();//
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())//
                .defaultDisplayImageOptions(defaultOptions)//
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
                .writeDebugLogs()//
                .build();//
        ImageLoader.getInstance().init(config);

    }
}
