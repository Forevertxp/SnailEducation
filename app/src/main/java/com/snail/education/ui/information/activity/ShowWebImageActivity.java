package com.snail.education.ui.information.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.ui.common.AsyncImageLoader;
import com.snail.education.ui.common.ZoomableImageView;

public class ShowWebImageActivity extends Activity {
    private TextView imageTextView = null;
    private String imagePath = null;
    private ZoomableImageView imageView = null;

    private ImageView back, share, download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web_image);
        getActionBar().hide();
        this.imagePath = getIntent().getStringExtra("image");

        this.imageTextView = (TextView) findViewById(R.id.show_webimage_imagepath_textview);
        imageTextView.setText(this.imagePath);
        imageView = (ZoomableImageView) findViewById(R.id.show_webimage_imageview);

        back = (ImageView) findViewById(R.id.pic_back);
        download = (ImageView) findViewById(R.id.pic_download);
        share = (ImageView) findViewById(R.id.pic_share);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        AsyncImageLoader imageLoader = new AsyncImageLoader();
        imageLoader.loadDrawable(this.imagePath, new AsyncImageLoader.ImageCallback() {
            @Override
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                imageView.setImageBitmap(drawableToBitmap(imageDrawable));
            }
        });

    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
