package com.snail.education.ui.setting;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.ui.activity.SEBaseActivity;

public class AboutActivity extends SEBaseActivity {

    private TextView versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitleText("关于");

        versionText = (TextView) findViewById(R.id.versionText);
        versionText.setText(SEConfig.getInstance().getVersionText());
    }

}
