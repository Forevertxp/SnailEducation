package com.snail.education.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.ui.me.activity.UserLoginActivity;
import com.snail.education.ui.me.activity.UserRegActivity;


public class IndexActivity extends Activity implements View.OnClickListener {

    private ImageView logoImage;
    private RelativeLayout operationRL;
    private TextView loginTV;
    private TextView visitTV;
    private Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        logoImage = (ImageView) findViewById(R.id.logoImage);
        loginTV = (TextView) findViewById(R.id.loginTV);
        visitTV = (TextView) findViewById(R.id.visitTV);
        regBtn = (Button) findViewById(R.id.btn_register);

        operationRL = (RelativeLayout) findViewById(R.id.operationRL);

        loginTV.setOnClickListener(this);
        visitTV.setOnClickListener(this);
        regBtn.setOnClickListener(this);

        SEAuthManager am = SEAuthManager.getInstance();
        if (am.isAuthenticated()) {
            operationRL.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(IndexActivity.this,
                            MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }, 500);
        } else {
            operationRL.setVisibility(View.VISIBLE);
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            slideview(0, -(display.getHeight() / 2 - 150));
        }
    }

    public void slideview(final float p1, final float p2) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, p1, p2);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(5000);
        animation.setStartOffset(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int left = logoImage.getLeft();
                int top = logoImage.getTop() + (int) (p2 - p1);
                int width = logoImage.getWidth();
                int height = logoImage.getHeight();
                logoImage.clearAnimation();
                logoImage.layout(left, top, left + width, top + height);
            }
        });
        logoImage.startAnimation(animation);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                Intent regIntent = new Intent(IndexActivity.this,
                        UserRegActivity.class);
                startActivity(regIntent);
                break;
            case R.id.loginTV:
                Intent loginIntent = new Intent(IndexActivity.this,
                        UserLoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.visitTV:
                Intent mainIntent = new Intent(IndexActivity.this,
                        MainActivity.class);
                startActivity(mainIntent);
                finish();
                break;
        }
    }
}
