package com.snail.education.ui.organization;

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
import com.snail.education.protocol.model.MCOrgInfo;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15/12/20.
 */
public class OrgInfoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MCOrgInfo> orgList;

    public OrgInfoAdapter(Context context, ArrayList<MCOrgInfo> orgList) {
        super();
        this.context = context;
        this.orgList = orgList;
    }

    @Override
    public int getCount() {
        return orgList.size();
    }

    @Override
    public Object getItem(int position) {
        if (orgList != null) {
            return orgList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_org_info, null);
            holder = new ViewHolder();
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.avatar_org);
            holder.tv_name = (TextView) convertView.findViewById(R.id.name_org);
            holder.tv_profile = (TextView) convertView.findViewById(R.id.profile_org);
            holder.tv_enroll = (TextView) convertView.findViewById(R.id.enrollCount);
            holder.tv_visit = (TextView) convertView.findViewById(R.id.visitCount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MCOrgInfo orgInfo = orgList.get(position);
        holder.tv_name.setText(orgInfo.name);
        holder.tv_profile.setText("\u3000\u3000" + orgInfo.profile);
        holder.tv_enroll.setText(orgInfo.checkinCount+"人");
        holder.tv_visit.setText(orgInfo.attendCount+"人");
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .showImageOnFail(R.drawable.ic_logo)
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(orgInfo.logo, holder.iv_avatar, options);
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_name;
        private TextView tv_profile;
        private TextView tv_enroll;
        private TextView tv_visit;
    }

}
