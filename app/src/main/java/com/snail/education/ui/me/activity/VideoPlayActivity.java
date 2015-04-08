package com.snail.education.ui.me.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import com.snail.education.R;

public class VideoPlayActivity extends Activity {
    private FullScreenVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoView = (FullScreenVideoView) findViewById(R.id.videoView);
        /**
         * VideoView控制视频播放的功能相对较少，具体而言，它只有start和pause方法。为了提供更多的控制，
         * 可以实例化一个MediaController，并通过setMediaController方法把它设置为VideoView的控制器。
         */
        videoView.setMediaController(new MediaController(this));
        String videoPath = getIntent().getExtras().getString("videoPath");
        Uri videoUri = Uri.parse(videoPath);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

}
