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
import com.snail.education.protocol.manager.SEInformationManager;
import com.snail.education.protocol.manager.SEStudentManager;
import com.snail.education.protocol.model.SEInformation;
import com.snail.education.protocol.model.SEStudent;
import com.snail.education.protocol.result.ServiceError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-22.
 */

public class StudentAdapter extends BaseAdapter {
    private Context context;
    private List<SEStudent> studentList;

    private static int LIMIT_STUDENT = 10;

    public StudentAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingStudent(1);
    }

    @Override
    public int getCount() {
        return getInformationCount();
    }

    @Override
    public Object getItem(int index) {
        return getStudent(index);
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
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SEStudent student = studentList.get(position);
        holder.tv_title.setText(student.getName());
        holder.tv_content.setText(student.getSpeak());
        String imageUrl = SEConfig.getInstance().getAPIBaseURL() + student.getIcon();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);
        return convertView;
    }

    private int getInformationCount() {
        if (studentList != null) {
            return studentList.size();  //顶部滚动图占据一行
        } else {
            return 0;
        }
    }

    private SEStudent getStudent(int index) {
        if (studentList != null) {
            return studentList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final SECallBack callback) {
        SEStudentManager studentManager = SEStudentManager.getInstance();
        studentManager.refreshStudent(LIMIT_STUDENT, new SECallBack() {
            @Override
            public void success() {
                updatePresentingStudent(1);
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
        double n = studentList.size() * 1.0 / LIMIT_STUDENT;
        int page = (int) Math.ceil(n) + 1;
        SEStudentManager studentManager = SEStudentManager.getInstance();
        studentManager.loadMoreStudent(page, LIMIT_STUDENT,
                new SECallBack() {
                    @Override
                    public void success() {
                        updatePresentingStudent(2);
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
        if (studentList == null || studentList.isEmpty()) {
            refresh(null);
        }
    }

    private void updatePresentingStudent(int type) {
        if (type == 1) {
            // 刷新
            studentList = new ArrayList<SEStudent>();
            studentList.addAll(SEStudentManager.getInstance().getStudent());
        } else {
            //加载更多
            studentList.addAll(SEStudentManager.getInstance().getStudent());
        }

    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
    }

}

