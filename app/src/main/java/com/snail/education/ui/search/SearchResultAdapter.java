package com.snail.education.ui.search;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.protocol.model.MCVideo;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SearchResultAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<MCVideo> searchList;

    public SearchResultAdapter(Context context,ArrayList<MCVideo> searchList) {
        super();
        this.context = context;
        this.searchList = searchList;
    }

    @Override
    public int getCount() {
        return getSearchCount();
    }

    @Override
    public Object getItem(int index) {
        if (searchList != null) {
            return searchList.get(index);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_search_result, null);
            holder = new ViewHolder();
            holder.iv_avatar = (RoundedImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MCVideo video = searchList.get(position);
        holder.tv_title.setText(video.name);
        holder.tv_content.setText(video.name);
        String imageUrl = video.address;
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .showImageOnFail(R.drawable.ic_default_video)
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        return convertView;
    }


    private int getSearchCount() {
        if (searchList != null) {
            return searchList.size();
        } else {
            return 0;
        }
    }

    class ViewHolder {
        private RoundedImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_category;
    }

}


