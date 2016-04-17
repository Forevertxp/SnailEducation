package com.snail.education.ui.me.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.protocol.model.MCCollectionItem;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


/**
 * Created by tianxiaopeng on 15/12/29.
 */
public class MCCollectionAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private LayoutInflater inflater;
    private ArrayList<MCCollectionItem> colletionList;

    public MCCollectionAdapter(Context context,ArrayList<MCCollectionItem> colletionList) {
        inflater = LayoutInflater.from(context);
        this.colletionList = colletionList;
    }

    @Override
    public int getCount() {
        return colletionList.size();
    }

    @Override
    public Object getItem(int position) {
        return colletionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_search_result, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.tv_title);
            holder.imageView = (RoundedImageView) convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MCCollectionItem item = colletionList.get(position);
        holder.text.setText(item.name);
        String imageUrl = item.address;
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .showImageOnFail(R.drawable.ic_default_video)
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.imageView, options);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.item_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.tv_header);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        if (colletionList.get(position).collectionType==1){
            holder.text.setText(" 课程");
        }else{
            holder.text.setText(" 视频");
        }

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        return colletionList.get(position).collectionType;
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
        RoundedImageView imageView;
    }

}