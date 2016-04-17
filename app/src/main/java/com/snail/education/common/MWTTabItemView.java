package com.snail.education.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snail.education.R;

public class MWTTabItemView extends LinearLayout {

    private ImageView _iconImageView;
    private TextView _titleTextView;

    public MWTTabItemView(Context context) {
        super(context);
    }

    public MWTTabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MWTTabItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        _iconImageView = (ImageView) findViewById(R.id.IconImageView);
        _titleTextView = (TextView) findViewById(R.id.TitleTextView);

    }

    public void setNormalIcon(Bitmap normalBitmap) {
        _iconImageView.setImageBitmap(normalBitmap);
    }

    public void setIconTextSize(float iconTextSize) {
        _titleTextView.setTextSize(iconTextSize);
    }

    public CharSequence getIconText() {
        return _titleTextView.getText();
    }

    public void setIconText(CharSequence iconText) {
        _titleTextView.setText(iconText);
    }

    public void setTitleTextSize(float titleTextSize) {
        _titleTextView.setTextSize(titleTextSize);
    }

    public CharSequence getTitleText() {
        return _titleTextView.getText();
    }

    public void setTitleText(CharSequence titleText) {
        _titleTextView.setText(titleText);
    }

    public void setDisplayStyle(DisplayStyle displayStyle, Bitmap bitmap) {

        switch (displayStyle) {
            case NORMAL: {
                int color = getResources().getColor(R.color.TabBarForegroundColor);
                _titleTextView.setTextColor(color);
                _iconImageView.setImageBitmap(bitmap);
                break;
            }
            case SELECTED: {
                int color = getResources().getColor(R.color.common_blue);
                _titleTextView.setTextColor(color);
                _iconImageView.setImageBitmap(bitmap);
                break;
            }
            default:
                break;
        }

    }

    public enum DisplayStyle {
        NORMAL, SELECTED
    }
}
