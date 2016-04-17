package com.snail.education.ui.me.fragment;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.ui.me.activity.PDFViewActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChenxingFragment extends Fragment implements OnClickListener {

    private MediaPlayer mediaPlayer;
    private String path;
    private RelativeLayout coverRL;
    private SurfaceView surfaceView;
    private Boolean iStart = true;
    private Boolean pause = true;
    private Button issrt;
    private int position;
    private SeekBar seekbar;
    private upDateSeekBar update; // 更新进度条用
    private boolean flag = true; // 用于判断视频是否在播放中
    private RelativeLayout relativeLayout;
    private Button quanping;
    private Button xiaoping;

    private ImageView coverImage;
    private ProgressBar waitingBar;
    private TextView totalTime;
    private LinearLayout operationLL;
    private Button btn_apply;

    private String localUrl;

    private long mediaLength = 0;
    private long readSize = 0;

    private Boolean netState = true;
    private Boolean isRegister = false;
    private int cachePercent = 0;  //缓存长度
    private int progressPercent = 0; // 已播放长度


    public ChenxingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chenxing, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
        update = new upDateSeekBar(); // 创建更新进度条对象
        mediaPlayer = new MediaPlayer();
        initView(rootView);
        initVideo();
        return rootView;
    }


    private void initView(View view) {
        seekbar = (SeekBar) view.findViewById(R.id.seekbar);
        issrt = (Button) view.findViewById(R.id.issrt);
        issrt.setOnClickListener(this);
        surfaceView = (SurfaceView) view.findViewById(R.id.surface);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.btm);
        coverImage = (ImageView) view.findViewById(R.id.coverImage);
        waitingBar = (ProgressBar) view.findViewById(R.id.waitingBar);
        quanping = (Button) view.findViewById(R.id.quanping);
        xiaoping = (Button) view.findViewById(R.id.xiaoping);
        totalTime = (TextView) view.findViewById(R.id.totalTime);
        coverRL = (RelativeLayout) view.findViewById(R.id.coverRL);
        quanping.setOnClickListener(this);
        xiaoping.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mSurfaceViewWidth = dm.widthPixels;
        int mSurfaceViewHeight = dm.heightPixels;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.width = mSurfaceViewWidth;
        lp.height = mSurfaceViewHeight * 1 / 3 - 40;
        surfaceView.setLayoutParams(lp);
        coverRL.setLayoutParams(lp);
        surfaceView.getHolder().setFixedSize(lp.width, lp.height);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new surfaceCallBack());
        surfaceView.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(new surfaceSeekBar());
        mediaPlayer.setOnCompletionListener(new CompletionListener());
        mediaPlayer.setOnBufferingUpdateListener(new BufferListener());
        mediaPlayer.setOnInfoListener(new InfoListener());
        mediaPlayer.setOnErrorListener(new ErrorListener());

        operationLL = (LinearLayout) view.findViewById(R.id.operationLL);
        operationLL.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PDFViewActivity.class);
                intent.putExtra("title","晨星成长计划");
                intent.putExtra("fileName","chenxing_plan.pdf");
                startActivity(intent);
            }
        });
        btn_apply = (Button) view.findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser();
            }
        });

        // 注册网络监听
        registerNetBroad();
    }

    /**
     * 具体视频信息
     */
    private void initVideo() {
        // 视频路径
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            String localpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/SnailData/VideoCache/" + "genius_plan.mp4";
            File f = new File(localpath);
            if (!f.exists()) {
                path = "https://dn-orthopedia.qbox.me/genius_plan.mp4";
            } else {
                path = localpath;
            }
        } else {
            Toast.makeText(getActivity(), "SD卡不存在！", Toast.LENGTH_SHORT).show();
        }
        coverImage.setImageResource(R.drawable.ic_chenxing);
    }

    protected void openBrowser() {
        Uri uri = Uri.parse("http://www.chenxingplan.com/cxinfo/hzfreg.aspx?recommend=极速学院");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private final class surfaceSeekBar implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            progressPercent = progress * 100 / seekBar.getMax();
            Log.e("progressPercent", progressPercent + "%");
            // 如果视频已无缓存且无网络
            if (cachePercent - progressPercent <= 2 && !netState) {
                mediaPlayer.pause();
                issrt.setBackgroundResource(R.drawable.ic_play);
                pause = true;
                waitingBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            int value = seekbar.getProgress() * mediaPlayer.getDuration() // 计算进度条需要前进的位置数据大小
                    / seekbar.getMax();
            mediaPlayer.seekTo(value);
        }

    }

    private final class surfaceCallBack implements Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (position > 0 && mediaPlayer != null) {
                mediaPlayer.setDisplay(surfaceView.getHolder());
                mediaPlayer.seekTo(position);
                position = 0;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            position = mediaPlayer.getCurrentPosition();
            // 如果播放时程序退到后台，如home键被按下
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                issrt.setBackgroundResource(R.drawable.ic_play);
                pause = true;
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.issrt:
                relativeLayout.setVisibility(View.GONE);
                coverImage.setVisibility(View.GONE);
                if (iStart) {  //页面刚创建，及该页面第一次点击
                    if (!netState) {
                        Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_LONG).show();
                        return;
                    }
                    play(0);
                    pause = false;
                    waitingBar.setVisibility(View.VISIBLE);  // 显示加载进度条
                    issrt.setBackgroundResource(R.drawable.ic_pause);
                    iStart = false;
                    new Thread(update).start();
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        issrt.setBackgroundResource(R.drawable.ic_play);
                        pause = true;
                    } else {
                        // 如果无网络且无缓存
                        if ((cachePercent - progressPercent <= 2) && !netState) {
                            return;
                        } else {
                            waitingBar.setVisibility(View.GONE);
                            if (pause) {
                                issrt.setBackgroundResource(R.drawable.ic_pause);
                                mediaPlayer.start();
                                pause = false;
                            } else {
                                // 刚开始点击播放视频，视频还在缓冲阶段（此时isPlaying为false，且pause也为false）
                                waitingBar.setVisibility(View.VISIBLE);
                                issrt.setBackgroundResource(R.drawable.ic_play);
                                return;
                            }
                        }
                    }
                }
                break;
            case R.id.quanping:
                // 设置横屏
                if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case R.id.xiaoping:
                if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.surface:
                if (relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.setVisibility(View.GONE);
                } else {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void writeMedia() {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                FileOutputStream out = null;
                InputStream is = null;

                try {
                    URL url = new URL(path);
                    HttpURLConnection httpConnection = (HttpURLConnection) url
                            .openConnection();

                    if (localUrl == null) {
                        localUrl = Environment.getExternalStorageDirectory()
                                .getAbsolutePath()
                                + "/VideoCache/"
                                + "snail1" + ".mp4";
                    }

                    File cacheFile = new File(localUrl);

                    if (!cacheFile.exists()) {
                        cacheFile.getParentFile().mkdirs();
                        cacheFile.createNewFile();
                    }

                    readSize = cacheFile.length();
                    out = new FileOutputStream(cacheFile, true);

                    httpConnection.setRequestProperty("User-Agent", "NetFox");
                    httpConnection.setRequestProperty("RANGE", "bytes="
                            + readSize + "-");

                    is = httpConnection.getInputStream();

                    mediaLength = httpConnection.getContentLength();
                    if (mediaLength == -1) {
                        return;
                    }

                    mediaLength += readSize;

                    byte buf[] = new byte[4 * 1024];
                    int size = 0;

                    while ((size = is.read(buf)) != -1) {
                        try {
                            out.write(buf, 0, size);
                            readSize += size;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            //
                        }
                    }

                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            //
                        }
                    }
                }

            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            iStart = true;
        }
        // 取消网络监听
        unregisterNetBroad();
        super.onDestroy();
    }

    private void play(int position) {
        MediaThread mediathread = new MediaThread(position);
        new Thread(mediathread).start();
    }

    private class MediaThread implements Runnable {

        private int position;

        private MediaThread(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.setDisplay(surfaceView.getHolder());
                mediaPlayer.prepare();// 进行缓冲处理
                mediaPlayer.setOnPreparedListener(new PreparedListener(
                        position));// 监听缓冲是否完成
            } catch (Exception e) {
            }
        }
    }


    private final class PreparedListener implements OnPreparedListener {
        private int position;

        public PreparedListener(int position) {
            // TODO Auto-generated constructor stub
            this.position = position;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            waitingBar.setVisibility(View.GONE);
            totalTime.setText(convert2String(mediaPlayer.getDuration())); //视频时长
            mediaPlayer.start(); // 播放视频
            writeMedia();
            if (position > 0) {
                mediaPlayer.seekTo(position);
            }
        }
    }

    private final class CompletionListener implements OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            issrt.setBackgroundResource(R.drawable.ic_play);
            pause = true;
            mediaPlayer.seekTo(0);
            seekbar.setProgress(0);
            mediaPlayer.pause();
        }
    }

    private final class BufferListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            cachePercent = percent;
            Log.e("cachePercent", cachePercent + "%");
            seekbar.setSecondaryProgress(cachePercent * seekbar.getMax() / 100);
        }
    }

    private final class InfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                // 开始缓存
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    waitingBar.setVisibility(View.VISIBLE);
                    break;
                // 缓存结束
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    waitingBar.setVisibility(View.GONE);
                    if (mediaPlayer.isPlaying()) {
                        issrt.setBackgroundResource(R.drawable.ic_pause);
                    }
                    break;
            }
            return false;
        }
    }

    private final class ErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mediaPlayer.reset();
            Log.e("mediaPlayer Error", "what:" + what + "extra:" + extra);
            return false;
        }
    }

    /**
     * 更新进度条及时间
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mediaPlayer == null) {
                flag = false;
            } else if (mediaPlayer.isPlaying()) {
                flag = true;
                int position = mediaPlayer.getCurrentPosition();
                int mMax = mediaPlayer.getDuration();
                int sMax = seekbar.getMax();
                // 进度条
                seekbar.setProgress(position * sMax / mMax);
                //时间
                totalTime.setText(convert2String(mMax - position));
            } else {
                return;
            }
        }

        ;
    };

    class upDateSeekBar implements Runnable {

        @Override
        public void run() {
            mHandler.sendMessage(Message.obtain());
            if (flag) {
                mHandler.postDelayed(update, 1000);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        boolean enterFullScreen = false;
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏， 在此处添加额外的处理代码
            enterFullScreen = true;
            relativeLayout.setVisibility(View.GONE);
            operationLL.setVisibility(View.GONE);
            quanping.setVisibility(View.GONE);
            xiaoping.setVisibility(View.VISIBLE);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mSurfaceViewWidth = dm.widthPixels;
            int mSurfaceViewHeight = dm.heightPixels;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.width = mSurfaceViewWidth;
            lp.height = mSurfaceViewHeight;
            surfaceView.setLayoutParams(lp);
            surfaceView.getHolder().setFixedSize(mSurfaceViewWidth,
                    mSurfaceViewHeight);
            coverRL.setLayoutParams(lp);
            // 状态栏
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 当前为竖屏， 在此处添加额外的处理代码
            enterFullScreen = false;
            relativeLayout.setVisibility(View.GONE);
            operationLL.setVisibility(View.VISIBLE);
            xiaoping.setVisibility(View.GONE);
            quanping.setVisibility(View.VISIBLE);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mSurfaceViewWidth = dm.widthPixels;
            int mSurfaceViewHeight = dm.heightPixels;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.width = mSurfaceViewWidth;
            lp.height = mSurfaceViewHeight * 1 / 3 - 40;
            surfaceView.setLayoutParams(lp);
            surfaceView.getHolder().setFixedSize(lp.width, lp.height);
            coverRL.setLayoutParams(lp);
            // 状态栏
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        Intent intent = new Intent("com.swiftacademy.screen.changed");
        intent.putExtra("enterFullScreen", enterFullScreen);
        getActivity().sendBroadcast(intent);
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 网络监听
     */

    private void registerNetBroad() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        if (!isRegister) {
            getActivity().registerReceiver(netBroadcast, intentFilter);
            isRegister = true;
        }
    }

    private void unregisterNetBroad() {
        if (isRegister) {
            getActivity().unregisterReceiver(netBroadcast);
            isRegister = false;
        }
    }

    BroadcastReceiver netBroadcast = new BroadcastReceiver() {

        State wifiState = null;
        State mobileState = null;
        public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION.equals(intent.getAction())) {
                // 获取手机的连接服务管理器，这里是连接管理器类
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .getState();
                mobileState = cm
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                        .getState();
                if (wifiState != null && mobileState != null
                        && State.CONNECTED != wifiState
                        && State.CONNECTED == mobileState) {
                    netState = true;
                } else if (wifiState != null && mobileState != null
                        && State.CONNECTED == wifiState
                        && State.CONNECTED != mobileState) {
                    netState = true;
                } else if (wifiState != null && mobileState != null
                        && State.CONNECTED != wifiState
                        && State.CONNECTED != mobileState) {
                    netState = false;
                }
            }

        }
    };

    /**
     * 将长整型数字转换为日期格式的字符串
     *
     * @param time
     * @return
     */
    public static String convert2String(long time) {
        if (time > 0l) {
            String format = "";
            if (time < 60 * 60 * 1000) {
                format = "mm:ss";
            } else {
                format = "HH:mm:ss";
            }
            SimpleDateFormat sf = new SimpleDateFormat(format);
            Date date = new Date(time);
            return sf.format(date);
        }

        return "";
    }

}
