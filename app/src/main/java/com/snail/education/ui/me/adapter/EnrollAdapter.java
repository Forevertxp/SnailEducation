package com.snail.education.ui.me.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.protocol.model.MCCheckInfo;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 16/1/20.
 */
public class EnrollAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MCCheckInfo> enrollList;


    public EnrollAdapter(Context context, ArrayList<MCCheckInfo> enrollList) {
        super();
        this.context = context;
        this.enrollList = enrollList;
    }

    @Override
    public int getCount() {
        if (enrollList != null) {
            return enrollList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int index) {
        if (enrollList != null) {
            return enrollList.get(index);
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
            convertView = android.view.View.inflate(context, R.layout.item_enroll, null);
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MCCheckInfo info = enrollList.get(position);
        holder.tv_title.setText(info.orgName);
        String imageUrl = info.orgPic;
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_date;
    }
}
