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
import com.snail.education.protocol.manager.SEMsgManager;
import com.snail.education.protocol.manager.SESubjectManager;
import com.snail.education.protocol.model.SEMsg;
import com.snail.education.protocol.model.SESubject;
import com.snail.education.protocol.result.ServiceError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class MsgAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<SEMsg> msgList;

    private static int LIMIT_MSG = 10;

    public MsgAdapter(Context context) {
        super();
        this.context = context;
        updatePresentingSubject(1);
    }

    @Override
    public int getCount() {
        return getMsgCount();
    }

    @Override
    public Object getItem(int index) {
        return getMsg(index);
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
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SEMsg subject = msgList.get(position);
        holder.tv_title.setText(subject.getTitle());
        holder.tv_content.setText(subject.getMsg());
        return convertView;
    }


    private int getMsgCount() {
        if (msgList != null) {
            return msgList.size();
        } else {
            return 0;
        }
    }

    private SEMsg getMsg(int index) {
        if (msgList != null) {
            return msgList.get(index);
        } else {
            return null;
        }
    }

    public void refresh(final SECallBack callback) {
        SEMsgManager msgManager = SEMsgManager.getInstance();
        msgManager.refresh(1, new SECallBack() {
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
        double n = msgList.size() * 1.0 / LIMIT_MSG;
        int page = (int) Math.ceil(n) + 1;
        SEMsgManager msgManager = SEMsgManager.getInstance();
        msgManager.refresh(page,
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
            msgList = new ArrayList<SEMsg>();
            msgList.addAll(SEMsgManager.getInstance().getMsgList());
        } else {
            //加载更多
            msgList.addAll(SEMsgManager.getInstance().getMsgList());
        }

    }

    class ViewHolder {
        private TextView tv_title;
        private TextView tv_content;
    }

}