package com.snail.education.ui.course;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.common.SEAutoSlidingPagerView;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.MCSubCourse;
import com.snail.education.protocol.result.MCBannerResult;
import com.snail.education.ui.index.ImagePagerAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class CourseAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<MCSubCourse> courseList;

    //定义两个int常量标记不同的Item视图
    public static final int PIC_ITEM = 0;
    public static final int PIC_WORD_ITEM = 1;

    public CourseAdapter(Context context, ArrayList<MCSubCourse> courseList) {
        super();
        this.context = context;
        this.courseList = courseList;
    }

    @Override
    public int getCount() {
        if (courseList != null) {
            return courseList.size() + 1;  //顶部滚动图占据一行
        } else {
            return 1;
        }
    }

    @Override
    public Object getItem(int index) {
        if (courseList != null && index > 0) {
            return courseList.get(index - 1);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return PIC_ITEM;
        } else {
            return PIC_WORD_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        //因为有两种视图，所以返回2
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        TopViewHolder topViewHolder = null;
        if (getItemViewType(position) == PIC_ITEM) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.view_information_top, null);
                topViewHolder = new TopViewHolder();
                topViewHolder.autoSlidingPagerView = (SEAutoSlidingPagerView) convertView.findViewById(R.id.autoSlideImage);
                int height = context.getResources().getDisplayMetrics().heightPixels;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (height * 0.3));
                topViewHolder.autoSlidingPagerView.setLayoutParams(layoutParams);
                convertView.setTag(topViewHolder);
            } else {
                topViewHolder = (TopViewHolder) convertView.getTag();
            }
            final SEAutoSlidingPagerView slidingPagerView = topViewHolder.autoSlidingPagerView;
            final SECourseManager courseManager = SECourseManager.getInstance();
            courseManager.fetchHomeBanner(new Callback<MCBannerResult>() {
                @Override
                public void success(MCBannerResult result, Response response) {
                    slidingPagerView.setAdapter(new ImagePagerAdapter(context, result.videoList));
                    slidingPagerView.setOnPageChangeListener(new MyOnPageChangeListener());
                    slidingPagerView.setInterval(4000);
                    slidingPagerView.setScrollDurationFactor(2.0);
                    slidingPagerView.startAutoScroll();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_course, null);
                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.gv_course = (GridView) convertView.findViewById(R.id.gv_course);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MCSubCourse course = courseList.get(position - 1);
            holder.tv_title.setText(course.name);
            setGridView(holder.gv_course, course.subCourseArrayList);
        }
        return convertView;
    }

    /**
     * 设置GirdView参数，绑定数据
     */
    private void setGridView(GridView gridView, final ArrayList<MCSubCourse> subCourseList) {
        int length = 150;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (subCourseList.size() * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(subCourseList.size()); // 设置列数量=列表集合数

        GridViewAdapter adapter = new GridViewAdapter(subCourseList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,CourseListActivity.class);
                intent.putExtra("pid",subCourseList.get(position).id);
                context.startActivity(intent);
            }
        });
    }


    class ViewHolder {
        private TextView tv_title;
        private GridView gv_course;
    }

    class TopViewHolder {
        SEAutoSlidingPagerView autoSlidingPagerView;
    }

    class GridViewHolder {
        private TextView tv_name;
        private ImageView iv_course;
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private class GridViewAdapter extends BaseAdapter {

        private ArrayList<MCSubCourse> subCourseList;

        public GridViewAdapter(ArrayList<MCSubCourse> subCourseList) {
            this.subCourseList = subCourseList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_gridview_course, null);
                viewHolder = new GridViewHolder();
                viewHolder.iv_course = (ImageView) convertView.findViewById(R.id.iv_course);
                viewHolder.iv_course.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // 设置缩放方式
                viewHolder.iv_course.setPadding(5, 0, 5, 0); // 设置ImageView的内边距
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GridViewHolder) convertView.getTag();
            }
            MCSubCourse course = subCourseList.get(position);
            viewHolder.tv_name.setText(course.name);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(course.address, viewHolder.iv_course, options);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            if (courseList != null) {
                return courseList.get(position);
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return subCourseList.size();
        }
    }
}