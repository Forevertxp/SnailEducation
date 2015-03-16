package com.snail.education.ui;

import android.app.Activity;
import android.os.Bundle;

import com.snail.education.R;
import com.snail.education.ui.BaseSearchActivity;
import com.snail.education.ui.MainFragment;
import com.sriramramani.droid.inspector.server.ViewServer;

import cn.sharesdk.framework.ShareSDK;

public class MainActivity extends BaseSearchActivity {


    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ShareSDK.initSDK(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        ViewServer.get(this).addWindow(this);

        setLeftImageInvisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

}
