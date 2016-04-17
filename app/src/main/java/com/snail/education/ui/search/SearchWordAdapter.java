package com.snail.education.ui.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.model.MCKeyword;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15/12/19.
 */
public class SearchWordAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MCKeyword> keywordArrayList;

    public SearchWordAdapter(Context context, ArrayList<MCKeyword> keywordArrayList) {
        super();
        this.context = context;
        this.keywordArrayList = keywordArrayList;
    }


    @Override
    public int getCount() {
        return keywordArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        if (keywordArrayList != null) {
            return keywordArrayList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_keyword, null);
            holder = new ViewHolder();
            holder.tv_keyword = (TextView) convertView.findViewById(R.id.keywordTV);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MCKeyword keyword = keywordArrayList.get(position);
        holder.tv_keyword.setText(keyword.name);
        holder.tv_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchResultActivity.class);
                intent.putExtra("keyword", keyword.name);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        private TextView tv_keyword;
    }
}
