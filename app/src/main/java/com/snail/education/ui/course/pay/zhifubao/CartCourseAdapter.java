package com.snail.education.ui.course.pay.zhifubao;

/**
 * Created by Administrator on 2015/3/16.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.circularimageview.CircularImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.model.SECart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class CartCourseAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<SECart> cartList;

    public CartCourseAdapter(Context context, ArrayList<SECart> cartList) {
        super();
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public int getCount() {
        return getInformationCount();
    }

    @Override
    public Object getItem(int index) {
        return getCart(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_cart_course, null);
            holder = new ViewHolder();
            holder.iv_avatar = (CircularImageView) convertView.findViewById(R.id.iv_avatar);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_teacher = (TextView) convertView.findViewById(R.id.tv_teacher);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SECart cart = cartList.get(position);
        holder.tv_title.setText(cart.getData().getName());
        holder.tv_content.setText(cart.getData().getOname());
        holder.tv_teacher.setText(cart.getData().getTname());
        holder.tv_price.setText("￥" + cart.getData().getPrice());
        String imageUrl = SEConfig.getInstance().getAPIBaseURL() + cart.getData().getIcon();
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .cacheInMemory(true)//
                .cacheOnDisk(true)//
                .bitmapConfig(Bitmap.Config.RGB_565)//
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, holder.iv_avatar, options);

        return convertView;
    }

    private int getInformationCount() {
        if (cartList != null) {
            return cartList.size();
        } else {
            return 0;
        }
    }

    private SECart getCart(int index) {
        if (cartList != null) {
            return cartList.get(index);
        } else {
            return null;
        }
    }

    class ViewHolder {
        private CircularImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_teacher;
        private TextView tv_price;
    }

}



