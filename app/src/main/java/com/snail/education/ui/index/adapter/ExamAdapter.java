package com.snail.education.ui.index.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEExamManager;
import com.snail.education.protocol.manager.SESubjectManager;
import com.snail.education.protocol.model.SEExam;
import com.snail.education.protocol.result.ServiceError;
import com.snail.staggeredgridview.util.DynamicHeightImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class ExamAdapter extends BaseAdapter {


    private Context context;
    private List<SEExam> examList;

    private static int LIMIT_EXAM = 5;
    private static int LIMIT_LENGTH = 100;

    public ExamAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingExam(1);
    }

    @Override
    public int getCount() {
        return getExamCount();
    }

    @Override
    public Object getItem(int index) {
        return getExame(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_information, null);
            holder = new ViewHolder();
            holder.iv_avatar = (DynamicHeightImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SEExam exam = examList.get(position);
        holder.tv_title.setText(exam.getTitle());
        holder.tv_content.setText(exam.getInfo());
        String imageUrl = SEConfig.getInstance().getAPIBaseURL() + exam.getIcon();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        return convertView;
    }


    private int getExamCount() {
        if (examList != null) {
            return examList.size();
        } else {
            return 0;
        }
    }

    private SEExam getExame(int index) {
        if (examList != null) {
            return examList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final SECallBack callback) {
        SEExamManager examManager = SEExamManager.getInstance();
        examManager.refreshExamList(1, LIMIT_EXAM, LIMIT_LENGTH, new SECallBack() {
            @Override
            public void success() {
                updatePresentingExam(1);
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
        double n = examList.size() * 1.0 / LIMIT_EXAM;
        int page = (int) Math.ceil(n) + 1;
        SESubjectManager subjectManager = SESubjectManager.getInstance();
        subjectManager.refreshSubjectList(page, LIMIT_EXAM, LIMIT_LENGTH,
                new SECallBack() {
                    @Override
                    public void success() {
                        updatePresentingExam(2);
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

    private void updatePresentingExam(int type) {
        if (type == 1) {
            // 刷新
            examList = new ArrayList<SEExam>();
            examList.addAll(SEExamManager.getInstance().getExamList());
        } else {
            //加载更多
            examList.addAll(SEExamManager.getInstance().getExamList());
        }

    }

    class ViewHolder {
        private DynamicHeightImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
    }

}


