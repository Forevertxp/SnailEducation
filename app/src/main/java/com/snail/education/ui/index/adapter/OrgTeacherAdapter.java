package com.snail.education.ui.index.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.model.MCTeacher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrgTeacherAdapter extends BaseAdapter {
    private ArrayList<MCTeacher> list = null;
    private Context mContext;

    public OrgTeacherAdapter(Context mContext, ArrayList<MCTeacher> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(ArrayList<MCTeacher> list) {
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
        final MCTeacher mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_teacher, null);
            viewHolder.avartar = (ImageView) view.findViewById(R.id.teacherAvatar);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvContent = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Picasso.with(mContext)
                .load(this.list.get(position).avatar)
                .resize(70, 70)
                .centerCrop()
                .into(viewHolder.avartar);
        viewHolder.tvTitle.setText(this.list.get(position).name);
        viewHolder.tvContent.setText(this.list.get(position).profile);

        return view;

    }


    final static class ViewHolder {
        ImageView avartar;
        TextView tvTitle;
        TextView tvContent;
    }

}