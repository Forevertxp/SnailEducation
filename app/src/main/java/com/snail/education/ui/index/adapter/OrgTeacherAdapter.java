package com.snail.education.ui.index.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.app.SEAPP;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.model.SETeacher;
import com.snail.sortlistview.SortModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrgTeacherAdapter extends BaseAdapter {
    private List<SETeacher> list = null;
    private Context mContext;

    public OrgTeacherAdapter(Context mContext, List<SETeacher> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<SETeacher> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SETeacher mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_teacher, null);
            viewHolder.avartar = (ImageView) view.findViewById(R.id.teacherAvatar);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.tvOname = (TextView) view.findViewById(R.id.oname);
            viewHolder.tvJob = (TextView) view.findViewById(R.id.job);
            viewHolder.tvCount = (TextView) view.findViewById(R.id.course_count);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvLetter.setVisibility(View.GONE);

        Picasso.with(mContext)
                .load(SEConfig.getInstance().getAPIBaseURL() + this.list.get(position).getIcon())
                .resize(70, 70)
                .centerCrop()
                .into(viewHolder.avartar);
        viewHolder.tvTitle.setText(this.list.get(position).getName());
        viewHolder.tvOname.setText(this.list.get(position).getOname());
        viewHolder.tvJob.setText(this.list.get(position).getJob());
        viewHolder.tvCount.setText(this.list.get(position).getClasscount());

        return view;

    }


    final static class ViewHolder {
        ImageView avartar;
        TextView tvLetter;
        TextView tvTitle;
        TextView tvOname;
        TextView tvJob;
        TextView tvCount;
    }

}