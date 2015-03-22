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
import com.snail.sortlistview.SortModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TeacherAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;

    public TeacherAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<SortModel> list) {
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
        final SortModel mContent = list.get(position);
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

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        Picasso.with(mContext)
                .load(SEConfig.getInstance().getAPIBaseURL() + this.list.get(position).getIcon())
                .resize(70, 70)
                .centerCrop()
                .into(viewHolder.avartar);
        viewHolder.tvTitle.setText(this.list.get(position).getName());
        viewHolder.tvOname.setText(this.list.get(position).getOname());
        viewHolder.tvJob.setText(this.list.get(position).getJob());
        viewHolder.tvCount.setText(this.list.get(position).getCount());

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


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}