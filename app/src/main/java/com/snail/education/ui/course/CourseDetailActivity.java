package com.snail.education.ui.course;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SECourseDetail;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.pay.CoursePayActivity;
import com.snail.svprogresshud.SVProgressHUD;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 仿优酷播放
 *
 * @author hongdawei
 */

public class CourseDetailActivity extends SEBaseActivity implements OnClickListener {

    private int courseID = 0;

    private MediaPlayer mediaPlayer;
    private String path;
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

    private LinearLayout infoLL; // 下方信息布局，全屏时隐藏
    private TextView priceText;
    private TextView freeText;
    private TextView infoText;
    private ListView relativeListView;
    private ImageView coverImage;
    private ProgressBar waitingBar;
    private TextView totalTime;

    private String localUrl;

    private long mediaLength = 0;
    private long readSize = 0;

    private Boolean netState = true;
    private Boolean isRegister = false;
    private int cachePercent = 0;  //缓存长度
    private int progressPercent = 0; // 已播放长度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
        setContentView(R.layout.activity_course_detail);
        courseID = getIntent().getExtras().getInt("id");
        setTitleText(getIntent().getExtras().getString("name"));

        update = new upDateSeekBar(); // 创建更新进度条对象
        mediaPlayer = new MediaPlayer();
        initView();
        initCourseInfo();
    }


    private void initView() {
        // TODO Auto-generated method stub
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            String localpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/VideoCache/" + "snail2" + ".mp4";
            File f = new File(localpath);
            if (!f.exists()) {
                path = "http://api.nowthinkgo.com/upload/video/mp4/02.mp4";
            } else {
                path = localpath;
            }
        } else {
            Toast.makeText(this, "SD卡不存在！", Toast.LENGTH_SHORT).show();
        }
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        issrt = (Button) findViewById(R.id.issrt);
        issrt.setOnClickListener(this);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        relativeLayout = (RelativeLayout) findViewById(R.id.btm);
        coverImage = (ImageView) findViewById(R.id.coverImage);
        waitingBar = (ProgressBar) findViewById(R.id.waitingBar);
        quanping = (Button) findViewById(R.id.quanping);
        xiaoping = (Button) findViewById(R.id.xiaoping);
        totalTime = (TextView) findViewById(R.id.totalTime);
        quanping.setOnClickListener(this);
        xiaoping.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mSurfaceViewWidth = dm.widthPixels;
        int mSurfaceViewHeight = dm.heightPixels;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.width = mSurfaceViewWidth;
        lp.height = mSurfaceViewHeight * 1 / 3;
        surfaceView.setLayoutParams(lp);
        surfaceView.getHolder().setFixedSize(lp.width, lp.height);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new surfaceCallBack());
        surfaceView.setOnClickListener(this);
        seekbar.setOnSeekBarChangeListener(new surfaceSeekBar());
        mediaPlayer.setOnCompletionListener(new CompletionListener());
        mediaPlayer.setOnBufferingUpdateListener(new BufferListener());
        mediaPlayer.setOnInfoListener(new InfoListener());
        mediaPlayer.setOnErrorListener(new ErrorListener());

        infoLL = (LinearLayout) findViewById(R.id.infoLL);
        priceText = (TextView) findViewById(R.id.coursePrice);
        freeText = (TextView) findViewById(R.id.courseFree);
        infoText = (TextView) findViewById(R.id.courseInfo);
        relativeListView = (ListView) findViewById(R.id.relativeCourse);

        // 注册网络监听
        registerNetBroad();
    }

    /**
     * 具体课程信息
     */
    private void initCourseInfo() {

        final SECourseManager courseManager = SECourseManager.getInstance();
        SVProgressHUD.showInView(this, "正在加载，请稍后...", true);
        courseManager.getCourseDetail(courseID, 39, new SECallBack() {
            @Override
            public void success() {
                SVProgressHUD.dismiss(CourseDetailActivity.this);
                SECourseDetail courseDetail = courseManager.getCourseDetail();
                DisplayImageOptions options = new DisplayImageOptions.Builder()//
                        .cacheInMemory(true)//
                        .cacheOnDisk(true)//
                        .bitmapConfig(Bitmap.Config.RGB_565)//
                        .build();
                ImageLoader.getInstance().displayImage(SEConfig.getInstance().getAPIBaseURL() + courseDetail.getThumb(), coverImage, options);
                priceText.setText("¥ " + courseDetail.getPrice());
                if (courseDetail.getFree().equals("Y")) {
                    freeText.setText("免费");
                } else {
                    freeText.setText("马上购买");
                    freeText.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CourseDetailActivity.this, CoursePayActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                infoText.setText(courseDetail.getInfo());
                ArrayList<SECourse> courseArrayList = courseManager.getCourseList();
                RelativeCourseAdapter adapter = new RelativeCourseAdapter(CourseDetailActivity.this, courseArrayList);
                relativeListView.setAdapter(adapter);
            }

            @Override
            public void failure(ServiceError error) {
                SVProgressHUD.dismiss(CourseDetailActivity.this);
                Toast.makeText(CourseDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                issrt.setText("开始");
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
                issrt.setText("开始");
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
                        Toast.makeText(CourseDetailActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                        return;
                    }
                    play(0);
                    waitingBar.setVisibility(View.VISIBLE);  // 显示加载进度条
                    issrt.setText("暂停");
                    iStart = false;
                    new Thread(update).start();
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        issrt.setText("开始");
                        pause = true;
                    } else {
                        // 如果无网络且无缓存
                        if ((cachePercent - progressPercent <= 2) && !netState) {
                            return;
                        } else {
                            waitingBar.setVisibility(View.GONE);
                            if (pause) {
                                issrt.setText("暂停");
                                mediaPlayer.start();
                                pause = false;
                            }
                        }
                    }
                }
                break;
            case R.id.quanping:
                // 设置横屏
                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case R.id.xiaoping:
                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
    protected void onDestroy() {
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
            issrt.setText("开始");
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏， 在此处添加额外的处理代码
            infoLL.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            quanping.setVisibility(View.GONE);
            xiaoping.setVisibility(View.VISIBLE);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
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
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 当前为竖屏， 在此处添加额外的处理代码
            infoLL.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            xiaoping.setVisibility(View.GONE);
            quanping.setVisibility(View.VISIBLE);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mSurfaceViewWidth = dm.widthPixels;
            int mSurfaceViewHeight = dm.heightPixels;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.width = mSurfaceViewWidth;
            lp.height = mSurfaceViewHeight * 1 / 3;
            surfaceView.setLayoutParams(lp);
            surfaceView.getHolder().setFixedSize(lp.width, lp.height);
        }
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 网络监听
     */

    private void registerNetBroad() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        if (!isRegister) {
            registerReceiver(netBroadcast, intentFilter);
            isRegister = true;
        }
    }

    private void unregisterNetBroad() {
        if (isRegister) {
            unregisterReceiver(netBroadcast);
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
