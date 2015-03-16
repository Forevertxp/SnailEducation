package com.snail.education.ui.information;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.common.SEAutoSlidingPagerView;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEIndexManager;
import com.snail.education.protocol.manager.SEInformationManager;
import com.snail.education.protocol.model.SEInformation;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.index.ImagePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class InformationAdapter extends BaseAdapter {
    private Context context;
    private List<SEInformation> informationList;

    private static int LIMIT_INFORMATION = 5;
    //定义两个int常量标记不同的Item视图
    public static final int PIC_ITEM = 0;
    public static final int PIC_WORD_ITEM = 1;

    public InformationAdapter(Context context) {
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
        return getInformation(index);
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
                convertView.setTag(topViewHolder);
            } else {
                topViewHolder = (TopViewHolder) convertView.getTag();
            }
            final SEAutoSlidingPagerView slidingPagerView = topViewHolder.autoSlidingPagerView;
            final SEIndexManager indexManager = SEIndexManager.getInstance();
            indexManager.fetchAdInfo(2, new SECallBack() {
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
                convertView = View.inflate(context, R.layout.item_information, null);
                holder = new ViewHolder();
                holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SEInformation information = informationList.get(position - 1);
            holder.tv_title.setText(information.getTitle());
            holder.tv_content.setText(information.getInfo());
            String imageUrl = SEConfig.getInstance().getAPIBaseURL() + information.getIcon();
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
        if (informationList != null) {
            return informationList.size() + 1;  //顶部滚动图占据一行
        } else {
            return 1;
        }
    }

    private SEInformation getInformation(int index) {
        if (informationList != null && index > 0) {
            return informationList.get(index - 1);
        } else {
            return null;
        }
    }

    public void refresh(final SECallBack callback) {
        SEInformationManager informationManager = SEInformationManager.getInstance();
        informationManager.refreshInformation(LIMIT_INFORMATION, new SECallBack() {
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

    public void loadMore(final SECallBack callback) {
        double n = informationList.size() * 1.0 / LIMIT_INFORMATION;
        int page = (int) Math.ceil(n) + 1;
        SEInformationManager informationManager = SEInformationManager.getInstance();
        informationManager.loadMoreInformation(page, LIMIT_INFORMATION,
                new SECallBack() {
                    @Override
                    public void success() {
                        updatePresentingInformation(2);
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
        if (informationList == null || informationList.isEmpty()) {
            refresh(null);
        }
    }

    private void updatePresentingInformation(int type) {
        if (type == 1) {
            // 刷新
            informationList = new ArrayList<SEInformation>();
            informationList.addAll(SEInformationManager.getInstance().getInformation());
        } else {
            //加载更多
            informationList.addAll(SEInformationManager.getInstance().getInformation());
        }

    }

    class ViewHolder {
        private ImageView iv_avatar;
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
