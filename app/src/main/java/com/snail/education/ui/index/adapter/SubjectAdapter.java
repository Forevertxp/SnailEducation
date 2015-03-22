package com.snail.education.ui.index.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SESubjectManager;
import com.snail.education.protocol.model.SESubject;
import com.snail.education.protocol.result.ServiceError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SubjectAdapter extends BaseAdapter {


    private Context context;
    private List<SESubject> subjectList;

    private static int LIMIT_SUBJECT = 10;
    private static int LIMIT_LENGTH = 100;

    public SubjectAdapter(Context context) {
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
            convertView = View.inflate(context, R.layout.item_subject, null);
            holder = new ViewHolder();
            holder.iv_avatar = (CircularImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SESubject subject = subjectList.get(position);
        holder.tv_title.setText(subject.getName());
        holder.tv_content.setText(subject.getTxt());
        String imageUrl = SEConfig.getInstance().getAPIBaseURL() + subject.getIcon();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        final String id = subject.getId();
        return convertView;
    }


    private int getInformationCount() {
        if (subjectList != null) {
            return subjectList.size();
        } else {
            return 0;
        }
    }

    private SESubject getSubject(int index) {
        if (subjectList != null) {
            return subjectList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final SECallBack callback) {
        SESubjectManager subjectManager = SESubjectManager.getInstance();
        subjectManager.refreshSubjectList(1, LIMIT_SUBJECT, LIMIT_LENGTH, new SECallBack() {
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

    public void loadMore(final SECallBack callback) {
        double n = subjectList.size() * 1.0 / LIMIT_SUBJECT;
        int page = (int) Math.ceil(n) + 1;
        SESubjectManager subjectManager = SESubjectManager.getInstance();
        subjectManager.refreshSubjectList(page, LIMIT_SUBJECT, LIMIT_LENGTH,
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
            subjectList = new ArrayList<SESubject>();
            subjectList.addAll(SESubjectManager.getInstance().getSubjectList());
        } else {
            //加载更多
            subjectList.addAll(SESubjectManager.getInstance().getSubjectList());
        }

    }

    class ViewHolder {
        private CircularImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
    }

}