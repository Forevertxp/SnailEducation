package com.snail.education.ui.me.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.snail.education.R;
import com.snail.education.protocol.manager.MCOrgManager;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.model.MCCheckInfo;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.protocol.result.MCEnrollListResult;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.me.adapter.EnrollAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserEnrollActivity extends SEBaseActivity {

    private SwipeMenuListView enrollLV;
    private EnrollAdapter adapter;

    private ArrayList<MCCheckInfo> enrollList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_enroll);

        setTitleText("我的报名");

        enrollLV = (SwipeMenuListView) findViewById(R.id.enrollLV);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(160);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        enrollLV.setMenuCreator(creator);
        enrollLV.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        enrollLV.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        MCCheckInfo info = enrollList.get(position);
                        cancelEnroll(info.checkId);
                        break;
                }
                return false;
            }
        });

        performRefresh();
    }

    private void performRefresh() {
        MCOrgManager om = MCOrgManager.getInstance();
        SEAuthManager am = SEAuthManager.getInstance();
        if (am.isAuthenticated()) {
            String userPhone = am.getAccessUser().getPhone();
            om.fetchEnrollList(userPhone, new Callback<MCEnrollListResult>() {
                @Override
                public void success(MCEnrollListResult result, Response response) {
                    enrollList = result.enrollList;
                    adapter = new EnrollAdapter(UserEnrollActivity.this, enrollList);
                    enrollLV.setAdapter(adapter);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void cancelEnroll(String enrollId) {
        MCOrgManager om = MCOrgManager.getInstance();
        om.cancelEnroll(enrollId, new Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {
                performRefresh();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
