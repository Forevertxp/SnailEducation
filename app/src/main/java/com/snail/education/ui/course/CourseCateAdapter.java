package com.snail.education.ui.course;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.common.SEAutoSlidingPagerView;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.manager.SEIndexManager;
import com.snail.education.protocol.model.SECourseCate;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.index.ImagePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class CourseCateAdapter extends BaseAdapter {


    private Context context;
    private List<SECourseCate> courseList;

    //定义两个int常量标记不同的Item视图
    public static final int PIC_ITEM = 0;
    public static final int PIC_WORD_ITEM = 1;

    public CourseCateAdapter(Context context) {
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
            final SEIndexManager indexManager = SEIndexManager.getInstance();
            indexManager.fetchAdInfo(3, new SECallBack() {
                @Override
                public void success() {
                    slidingPagerView.setAdapter(new ImagePagerAdapter(context, indexManager.getAdList()));
                    slidingPagerView.setOnPageChangeListener(new MyOnPageChangeListener());
                    slidingPagerView.setInterval(4000);
                    slidingPagerView.setScrollDurationFactor(2.0);
                    slidingPagerView.startAutoScroll();
                }

                @Override
                public void failure(ServiceError error) {

                }
            });

        } else {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_course_category, null);
                holder = new ViewHolder();
                holder.iv_avatar = (CircularImageView) convertView.findViewById(R.id.iv_avatar);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SECourseCate course = courseList.get(position - 1);
            holder.tv_title.setText(course.name);
            holder.tv_content.setText(course.info);
            String imageUrl = SEConfig.getInstance().getAPIBaseURL() + course.icon;
            DisplayImageOptions options = new DisplayImageOptions.Builder()//
                    .cacheInMemory(true)//
                    .cacheOnDisk(true)//
                    .bitmapConfig(Bitmap.Config.RGB_565)//
                    .build();
            ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);
        }
        return convertView;
    }


    private int getInformationCount() {
        if (courseList != null) {
            return courseList.size() + 1;  //顶部滚动图占据一行
        } else {
            return 1;
        }
    }

    private SECourseCate getCousre(int index) {
        if (courseList != null && index > 0) {
            return courseList.get(index - 1);
        } else {
            return null;
        }
    }

    public void refresh(final SECallBack callback) {
        SECourseManager courseManager = SECourseManager.getInstance();
        courseManager.refreshCourse(new SECallBack() {
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

    public void refreshIfNeeded() {
        if (courseList == null || courseList.isEmpty()) {
            refresh(null);
        }
    }

    private void updatePresentingInformation(int type) {
        if (type == 1) {
            // 刷新
            courseList = new ArrayList<SECourseCate>();
            courseList.addAll(SECourseManager.getInstance().getCourseCateList());
        } else {
            //加载更多
            courseList.addAll(SECourseManager.getInstance().getCourseCateList());
        }

    }

    class ViewHolder {
        private CircularImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
    }

    class TopViewHolder {
        SEAutoSlidingPagerView autoSlidingPagerView;
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
}

