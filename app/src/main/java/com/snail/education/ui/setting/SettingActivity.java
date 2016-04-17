package com.snail.education.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.sharesdk.ShareModel;
import com.snail.education.sharesdk.SharePopupWindow;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.me.activity.UserPasswordActivity;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.utils.UIHandler;

public class SettingActivity extends SEBaseActivity implements PlatformActionListener, Handler.Callback {

    private SharePopupWindow share;
    private RelativeLayout shareRL;
    private TextView versionText;
    private RelativeLayout passRL;
    private RelativeLayout downloadRL;
    private TextView downloadTV;
    private RelativeLayout aboutRL;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitleText("设置");

        versionText = (TextView) findViewById(R.id.versionText);
        versionText.setText("V 1.0");

        //获取SharedPreferences对象
        sp = getSharedPreferences("SP", MODE_PRIVATE);

        passRL = (RelativeLayout) findViewById(R.id.passRL);
        passRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UserPasswordActivity.class);
                startActivity(intent);
            }
        });

        aboutRL = (RelativeLayout) findViewById(R.id.aboutRL);
        aboutRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        downloadRL = (RelativeLayout) findViewById(R.id.downloadRL);
        downloadTV = (TextView) findViewById(R.id.downloadTV);
        if (sp.getInt("DOWNLOAD_TYPE",0)==0){
            downloadTV.setText("仅WIFI");
        }else {
            downloadTV.setText("移动数据和WIFI");
        }
        downloadRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomPopWindow();
            }
        });

        shareRL = (RelativeLayout) findViewById(R.id.shareRL);
        shareRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                share = new SharePopupWindow(SettingActivity.this);
                share.setPlatformActionListener(SettingActivity.this);
                ShareModel model = new ShareModel();
                model.setImageUrl("http://upload.swiftacademy.cn:8080/swift/logo.png");
                model.setText("我正在学习极速学院的MBA课程");
                model.setTitle("极速学院");
                model.setUrl("http://www.pgyer.com/swiftacademy");
                share.initShareParams(model);
                share.showShareWindow();
                // 显示窗口 (设置layout在PopupWindow中显示的位置)
                share.showAtLocation(SettingActivity.this.findViewById(R.id.main_setting),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            }
        });
    }


    private void showBottomPopWindow() {
        final View popView = LayoutInflater.from(this).inflate(R.layout.layout_popwindow_download, null);
        final PopupWindow popWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        TextView tv_only = (TextView) popView.findViewById(R.id.pop_first);
        TextView tv_both = (TextView) popView.findViewById(R.id.pop_second);
        TextView tv_cancel = (TextView) popView.findViewById(R.id.pop_cancel);
        View rootView = popView.findViewById(R.id.pop_root);

        tv_only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTV.setText("仅WIFI");
                //存入数据
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("DOWNLOAD_TYPE", 0);
                editor.commit();
                popWindow.dismiss();
            }
        });
        tv_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTV.setText("移动数据和WIFI");
                //存入数据
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("DOWNLOAD_TYPE", 1);
                editor.commit();
                popWindow.dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);
        //影响返回键所需
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /////////////////////// 分享相关  ///////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        if (share != null) {
            share.dismiss();
        }
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        Message msg = new Message();
        msg.what = 0;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onComplete(Platform plat, int action,
                           HashMap<String, Object> res) {
        Message msg = new Message();
        msg.arg1 = 1;
        msg.arg2 = action;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        Message msg = new Message();
        msg.what = 1;
        UIHandler.sendMessage(msg, this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;
        if (what == 1) {
            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
        }
        if (share != null) {
            share.dismiss();
        }
        return false;
    }


}
