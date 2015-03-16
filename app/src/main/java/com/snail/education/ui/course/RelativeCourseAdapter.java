package com.snail.education.ui.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.model.SECourse;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-19.
 */
public class RelativeCourseAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<SECourse> courseList;

    public RelativeCourseAdapter(Context context,ArrayList<SECourse> courseList) {
        super();
        this.context = context;
        this.courseList = courseList;
    }

    @Override
    public int getCount() {
        return getInformationCount();
    }

    @Override
    public Object getItem(int index) {
        return getCousre(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_course_detail, null);
            holder = new ViewHolder();
            holder.iv_avatar = (CircularImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            holder.btn_learn = (Button) convertView.findViewById(R.id.btn_learn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SECourse course = courseList.get(position);
        holder.tv_title.setText(course.getName());
        holder.tv_content.setText(course.getOname());
        holder.tv_category.setText(course.getCname());
        String imageUrl = SEConfig.getInstance().getAPIBaseURL() + course.getIcon();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        final int id = course.getId();
        holder.btn_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(context, CourseDetailActivity.class);
                intent.putExtra("id", id);
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    private int getInformationCount() {
        if (courseList != null) {
            return courseList.size();
        } else {
            return 0;
        }
    }

    private SECourse getCousre(int index) {
        if (courseList != null) {
            return courseList.get(index);
        } else {
            return null;
        }
    }


    class ViewHolder {
        private CircularImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_category;
        private Button btn_learn;
    }

}



