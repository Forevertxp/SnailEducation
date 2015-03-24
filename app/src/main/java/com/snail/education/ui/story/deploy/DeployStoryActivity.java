package com.snail.education.ui.story.deploy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.ui.activity.SEBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeployStoryActivity extends SEBaseActivity implements OnClickListener {

    private EditText addTwitter;
    private ImageView addPhote, imageView;
    private TextView sendTwitter;

    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2; //
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;
    /**
     * 这里的这个URL是我服务器的javaEE环境URL
     */
    private static String requestURL = "http://v1.api.upyun.com/ge-img-test";

    private String picPath = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_story);
        setTitleText("发表故事");
        init();
    }

    public void init() {
        addTwitter = (EditText) findViewById(R.id.twitter_add);
        addPhote = (ImageView) findViewById(R.id.photo_add);
        imageView = (ImageView) findViewById(R.id.photo);
        sendTwitter = (TextView) findViewById(R.id.twitter_send);
        progressDialog = new ProgressDialog(this);
        addPhote.setOnClickListener(this);
        sendTwitter.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_add:
                break;
            case R.id.twitter_send:
                if (picPath != null) {
                } else {
                    String content = addTwitter.getText().toString();
                    addTwitter(content, "");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
        }
    }


    // 发布新market消息
    private void addTwitter(final String content, final String image) {
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String path = "http://182.92.183.189:8831/twitter/add";
                Map<String, String> params = new HashMap<String, String>();
                params.put("content", content);
                params.put("image", image);
                Message message = Message.obtain();
                try {
                    JSONObject jsonObject = new JSONObject("");
                    if (jsonObject.getInt("code") == 0) {
                        message.arg1 = 1;
                    } else {
                        message.arg1 = -1;
                    }
                    mHandler.sendMessage(message);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        readThread.start();

    }

    ;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.arg1 == 1) {
            }
        }
    };
}

