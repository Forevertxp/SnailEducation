package com.snail.education.ui.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.database.CourseDB;
import com.snail.education.ui.activity.ImagePagerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-17.
 */
public class DownloadAdapter extends BaseAdapter {


    private Context context;
    private List<CourseDB> courseList;
    public static boolean CHECKBOS_VISIBLE = false;//用来记录是否显示checkBox

    private List<Boolean> boolList; // 用来记录checkbox的选中状态

    public DownloadAdapter(Context context, List<CourseDB> courseList, List<Boolean> boolList) {
        super();
        this.context = context;
        this.courseList = courseList;
        this.boolList = boolList;
    }

    @Override
    public int getCount() {
        return getStoryCount();
    }

    @Override
    public Object getItem(int index) {
        return getCourse(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_download, null);
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CourseDB course = courseList.get(position);
        holder.tv_title.setText(course.getName());
        holder.tv_content.setText(course.getSize() + "");
        String imageUrl = course.getThumb();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);
        if (CHECKBOS_VISIBLE) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.INVISIBLE);
        }
        holder.cb.setChecked(boolList.get(position));
        return convertView;
    }

    /**
     * 打开图片查看器
     *
     * @param position
     * @param urls2
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        context.startActivity(intent);
    }


    private int getStoryCount() {
        if (courseList != null) {
            return courseList.size();  //顶部滚动图占据一行
        } else {
            return 1;
        }
    }

    private CourseDB getCourse(int index) {
        if (courseList != null) {
            return courseList.get(index);
        } else {
            return null;
        }
    }

    public class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        public CheckBox cb;
    }

}