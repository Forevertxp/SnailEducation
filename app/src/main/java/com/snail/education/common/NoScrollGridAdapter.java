package com.snail.education.common;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoScrollGridAdapter extends BaseAdapter {

    /**
     * 上下文
     */
    private Context ctx;
    /**
     * 图片Url集合
     */
    private ArrayList<String> imageUrls;

    public NoScrollGridAdapter(Context ctx, ArrayList<String> urls) {
        this.ctx = ctx;
        this.imageUrls = urls;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageUrls == null ? 0 : imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(ctx).inflate(R.layout.item_gridview, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_image);
        Picasso.with(ctx)
                .load(imageUrls.get(position))
                .into(imageView);
        int column;
        if (imageUrls.size() < 3) {
            column = imageUrls.size();
        } else {
            column = 3;
        }
        if (column > 1) {
            // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
            int itemWidth = (int) (ctx.getResources().getDisplayMetrics().widthPixels - column * 10) / column;
            // 下面根据比例计算item的高度，此处只是使用itemWidth
            int itemHeight = itemWidth;
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    itemWidth,
                    itemHeight);
            convertView.setLayoutParams(param);
        }
        return convertView;
    }

}
