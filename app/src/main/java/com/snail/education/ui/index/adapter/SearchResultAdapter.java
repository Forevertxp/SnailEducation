package com.snail.education.ui.index.adapter;

import android.content.Context;
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
import com.snail.education.protocol.manager.SEIndexManager;
import com.snail.education.protocol.model.SESearch;
import com.snail.education.protocol.result.ServiceError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SearchResultAdapter extends BaseAdapter {


    private Context context;
    private List<SESearch> searchList;
    private static int SEARCH_LIMIT = 10;

    public SearchResultAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingInformation(1);
    }

    @Override
    public int getCount() {
        return getSearchCount();
    }

    @Override
    public Object getItem(int index) {
        return getSearch(index);
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
            holder.iv_avatar = (CircularImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_category = (TextView) convertView.findViewById(R.id.tv_category);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SESearch searchResult = searchList.get(position);
        holder.tv_title.setText(searchResult.getTitle());
        holder.tv_content.setText(searchResult.getInfo());
        holder.tv_category.setText(searchResult.getType());
        String imageUrl = SEConfig.getInstance().getAPIBaseURL() + searchResult.getIcon();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
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

    private SESearch getSearch(int index) {
        if (searchList != null) {
            return searchList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(String opt, String key, int page, final SECallBack callback) {
        SEIndexManager indexManager = SEIndexManager.getInstance();
        indexManager.search(opt, key, page, SEARCH_LIMIT, new SECallBack() {
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
            searchList = new ArrayList<SESearch>();
            searchList.addAll(SEIndexManager.getInstance().getSearchList());
        } else {
            //加载更多
            searchList.addAll(SEIndexManager.getInstance().getSearchList());
        }

    }

    class ViewHolder {
        private CircularImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_category;
    }

}


