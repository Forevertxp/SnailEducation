package com.snail.education.ui.course;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.model.MCCourPoint;
import com.snail.education.protocol.model.MCCourSection;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class CourseCateAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<MCCourPoint> pointList;

    public CourseCateAdapter(Context context, ArrayList<MCCourPoint> pointList) {
        super();
        this.context = context;
        this.pointList = pointList;
    }

    @Override
    public int getCount() {
        if (pointList != null) {
            return pointList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int index) {
        if (pointList != null) {
            return pointList.get(index);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_course_section, null);
            holder = new ViewHolder();
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MCCourPoint point = pointList.get(position);
        holder.tv_title.setText(point.name);
        return convertView;
    }


    class ViewHolder {
        private TextView tv_title;
        private ListView lv_point;
    }

}

