package com.snail.education.ui.index.adapter;

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
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SESubjectManager;
import com.snail.education.protocol.model.SESubItem;
import com.snail.education.protocol.result.ServiceError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SubItemAdapter extends BaseAdapter {


    private Context context;
    private List<SESubItem> itemList;

    private static int LIMIT_SUBJECT = 10;
    private static int LIMIT_LENGTH = 100;

    public SubItemAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingSubject(1);
    }

    @Override
    public int getCount() {
        return getInformationCount();
    }

    @Override
    public Object getItem(int index) {
        return getSubject(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_subitem, null);
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SESubItem item = itemList.get(position);
        holder.tv_content.setText(item.getTxt());
        holder.tv_date.setText(item.getDate());
        String imageUrl = SEConfig.getInstance().getAPIBaseURL() + item.getIcon();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        return convertView;
    }


    private int getInformationCount() {
        if (itemList != null) {
            return itemList.size();
        } else {
            return 0;
        }
    }

    private SESubItem getSubject(int index) {
        if (itemList != null) {
            return itemList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(String cid, final SECallBack callback) {
        SESubjectManager subjectManager = SESubjectManager.getInstance();
        subjectManager.refreshSubItem(1, LIMIT_SUBJECT, LIMIT_LENGTH, cid, new SECallBack() {
            @Override
            public void success() {
                updatePresentingSubject(1);
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

    public void loadMore(String cid, final SECallBack callback) {
        double n = itemList.size() * 1.0 / LIMIT_SUBJECT;
        int page = (int) Math.ceil(n) + 1;
        SESubjectManager subjectManager = SESubjectManager.getInstance();
        subjectManager.refreshSubItem(page, LIMIT_SUBJECT, LIMIT_LENGTH, cid,
                new SECallBack() {
                    @Override
                    public void success() {
                        updatePresentingSubject(2);
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

    private void updatePresentingSubject(int type) {
        if (type == 1) {
            // 刷新
            itemList = new ArrayList<SESubItem>();
            itemList.addAll(SESubjectManager.getInstance().getItemList());
        } else {
            //加载更多
            itemList.addAll(SESubjectManager.getInstance().getItemList());
        }

    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_content;
        private TextView tv_date;
    }

}


