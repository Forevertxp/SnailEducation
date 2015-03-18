package com.snail.education.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.umeng.analytics.MobclickAgent;

public class SEBaseActivity extends FragmentActivity {


    private ImageView leftImage;
    private ImageView rightImage;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 返回箭头（默认不显示）
        getActionBar().setDisplayHomeAsUpEnabled(false);
        // 左侧图标点击事件使能
        getActionBar().setHomeButtonEnabled(true);
        // 使左上角图标(系统)是否显示
        getActionBar().setDisplayShowHomeEnabled(false);
        // 显示标题
        getActionBar().setDisplayShowTitleEnabled(false);
        //显示自定义视图
        getActionBar().setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.actionbar_layout, null);
        leftImage = (ImageView) actionbarLayout.findViewById(R.id.left_imbt);
        initLeft();
        rightImage = (ImageView) actionbarLayout.findViewById(R.id.right_imbt);
        titleText = (TextView) actionbarLayout.findViewById(R.id.tv_title);
        getActionBar().setCustomView(actionbarLayout);

    }

    private void initLeft() {
        leftImage.setVisibility(View.VISIBLE);
        leftImage.setImageResource(R.drawable.ic_back);
        leftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setTitleText(String title) {
        titleText.setText(title);
    }


    public void setRightImage(int resid) {
        rightImage.setVisibility(View.VISIBLE);
        rightImage.setImageResource(resid);
    }

    public void setRightImageInvisibility() {
        rightImage.setVisibility(View.GONE);
    }

    public void setLeftImageInvisibility() {
        leftImage.setVisibility(View.GONE);
    }

    public void setRightImageListener(View.OnClickListener l) {
        rightImage.setOnClickListener(l);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);   // 友盟统计
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

