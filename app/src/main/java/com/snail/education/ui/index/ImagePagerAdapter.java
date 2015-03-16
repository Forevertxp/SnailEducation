package com.snail.education.ui.index;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.snail.education.app.SEConfig;
import com.snail.education.common.RecyclingPagerAdapter;
import com.snail.education.protocol.model.SEAdvertisement;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-7.
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {
    private Context context;
    private ArrayList<SEAdvertisement> adImageList;

    private int size;

    public ImagePagerAdapter(Context context, ArrayList<SEAdvertisement> adImageList) {
        this.context = context;
        this.adImageList = adImageList;
        this.size = adImageList.size();
    }

    @Override
    public int getCount() {
        return adImageList.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = holder.imageView = new ImageView(context);
            view.setTag(holder);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    int item = autoSlidingPagerView.getCurrentItem();
//                    handlePagerItemClicked(item);
                }
            });
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Picasso.with(context)
                .load(SEConfig.getInstance().getAPIBaseURL() + adImageList.get(position).getImg())
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
