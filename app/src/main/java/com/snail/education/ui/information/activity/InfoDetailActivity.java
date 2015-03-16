package com.snail.education.ui.information.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEInformationManager;
import com.snail.education.protocol.model.SEInfoDetail;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.RelativeCourseAdapter;

@SuppressLint("SetJavaScriptEnabled")
public class InfoDetailActivity extends SEBaseActivity {

    private WebView contentWebView = null;
    private TextView titleTV, datetimeTV, readTV, praiseTV;
    private ImageView praiseImage;
    private LinearLayout praiseLL;

    private final SEInformationManager informationManager = SEInformationManager.getInstance();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);
        setTitleText("资讯正文");

        titleTV = (TextView) findViewById(R.id.titleTV);
        datetimeTV = (TextView) findViewById(R.id.datetimeTV);
        contentWebView = (WebView) findViewById(R.id.webview);
        readTV = (TextView) findViewById(R.id.readCount);
        praiseLL = (LinearLayout) findViewById(R.id.praisLL);
        praiseTV = (TextView) findViewById(R.id.praiseCount);
        praiseImage = (ImageView) findViewById(R.id.praiseImage);
        // 启用javascript
        contentWebView.getSettings().setJavaScriptEnabled(true);
        // 添加js交互接口类，并起别名 imagelistner
        contentWebView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        contentWebView.setWebViewClient(new MyWebViewClient());
        contentWebView.setVerticalScrollBarEnabled(false);

        final int infoID = getIntent().getExtras().getInt("infoID");
        initInformation(infoID);

        praiseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praise(infoID);
            }
        });
    }


    /**
     * 加载资讯
     */
    private void initInformation(int infoID) {
        informationManager.fetchInformationDetail(infoID, new SECallBack() {
            @Override
            public void success() {
                SEInfoDetail infoDetail = informationManager.getInfoDetail();
                titleTV.setText(infoDetail.getTitle());
                datetimeTV.setText(infoDetail.getDate() + "    蜗牛MBA");
                contentWebView.loadData(infoDetail.getInfo(), "text/html; charset=UTF-8", null);
                praiseLL.setVisibility(View.VISIBLE);
                readTV.setText(infoDetail.getView() + "");
                praiseTV.setText(infoDetail.getZan());
            }

            @Override
            public void failure(ServiceError error) {

            }
        });
    }

    private void praise(int infoID) {
        informationManager.praise(infoID, new SECallBack() {
            @Override
            public void success() {
                praiseTV.setText(informationManager.getPraiseCount());
            }

            @Override
            public void failure(ServiceError error) {

            }
        });
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
        contentWebView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        public void openImage(String img) {
            Intent intent = new Intent();
            intent.putExtra("image", img);
            intent.setClass(context, ShowWebImageActivity.class);
            context.startActivity(intent);
        }
    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }
}
