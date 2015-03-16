package com.snail.education.ui.me;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.model.SEUser;

/**
 * Created by tianxiaopeng on 15-1-9.
 */
public class UserHeaderView extends FrameLayout {


    private Context _context;

    private CircularImageView avatarImageView;
    private TextView nicknameTextView, signatureTextView, scoreTextView;

    private SEUser _user;

    private UserMeFragment _userFragment;

    public UserHeaderView(UserMeFragment userFragment) {
        super(userFragment.getActivity());
        construct(userFragment);
    }

    private void construct(UserMeFragment userFragment) {
        _userFragment = userFragment;
        _context = userFragment.getActivity();
        LayoutInflater.from(_context).inflate(R.layout.view_user_header, this, true);
        setupViews();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupViews();
    }

    private void setupViews() {
        avatarImageView = (CircularImageView) findViewById(R.id.AvatarImageView);
        avatarImageView.setBorderWidth(4);
        avatarImageView.setBorderColor(getResources().getColor(R.color.lightgrey));
        avatarImageView.addShadow();
        nicknameTextView = (TextView) findViewById(R.id.NicknameTextView);
        signatureTextView = (TextView) findViewById(R.id.SignatureTextView);
        scoreTextView = (TextView) findViewById(R.id.ScoreTextView);

        avatarImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                _userFragment.onEditUserInfo();
            }
        });
    }

    public void setUser(SEUser user) {
        _user = user;
        updateAvatarImageView();
        updateTextViews();
    }

    private void updateAvatarImageView() {
        String avatarUrl = "";
        if (_user != null && !_user.getIcon().equals("") && _user.getIcon().indexOf("res/images/def.jpg") == -1) {
            avatarUrl = SEConfig.getInstance().getAPIBaseURL() + _user.getIcon();
            DisplayImageOptions options = new DisplayImageOptions.Builder()//
                    .cacheInMemory(true)//
                    .cacheOnDisk(true)//
                    .bitmapConfig(Bitmap.Config.RGB_565)//
                    .build();
            ImageLoader.getInstance().displayImage(avatarUrl, avatarImageView, options);
        }
    }

    private void updateTextViews() {
        if (_user != null) {
            nicknameTextView.setText(_user.getNickname());
            signatureTextView.setText(_user.getSay());
            scoreTextView.setText(_user.getPoint());
        }
    }
}

