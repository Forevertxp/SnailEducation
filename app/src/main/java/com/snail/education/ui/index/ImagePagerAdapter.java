package com.snail.education.ui.index;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snail.education.R;
import com.snail.education.common.RecyclingPagerAdapter;
import com.snail.education.protocol.model.MCVideo;
import com.snail.education.ui.course.CourseDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-7.
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {
    private Context context;
    private ArrayList<MCVideo> videoList;

    private int size;

    public ImagePagerAdapter(Context context, ArrayList<MCVideo> videoList) {
        this.context = context;
        this.videoList = videoList;
        this.size = videoList.size();
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new ImageView(context);
            view.setTag(holder);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MCVideo videoInfo = videoList.get(position);
                    Intent intent = new Intent(context,CourseDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videoInfo", videoInfo);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        } else {
            holder = (ViewHolder) view.getTag();
        }
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        Picasso.with(context)
                .load(videoList.get(position).pic)
                .placeholder(R.drawable.default_index)
                .error(R.drawable.default_index)
                .resize(width, (int) (height * 0.3))
                .into(holder.imageView);
        return view;
    }

    private class ViewHolder {
        ImageView imageView;
    }

    private void handlePagerItemClicked(int index) {
        switch (index) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }
}
