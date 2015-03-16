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
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.result.ServiceError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class CourseAdapter extends BaseAdapter {


    private Context context;
    private List<SECourse> courseList;

    public CourseAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingInformation(1);
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
        final String name = course.getName();
        holder.btn_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(context, CourseDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
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

    public void refresh(String free, int tid, int oid, int cid, final SECallBack callback) {
        SECourseManager courseManager = SECourseManager.getInstance();
        courseManager.refreshCourseList(free, tid, oid, cid, new SECallBack() {
            @Override
            public void success() {
                updatePresentingInformation(1);
                notifyDataSetChanged();
                if (callback != null) {
                    callback.success();
                }
            }

            @Override
            public void failure(ServiceError error) {
                if (callback != null) {
                    callback.failure(error);
                }
            }
        });
    }

    private void updatePresentingInformation(int type) {
        if (type == 1) {
            // 刷新
            courseList = new ArrayList<SECourse>();
            courseList.addAll(SECourseManager.getInstance().getCourseList());
        } else {
            //加载更多
            courseList.addAll(SECourseManager.getInstance().getCourseList());
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


