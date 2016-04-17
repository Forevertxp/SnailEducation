package com.snail.education.ui.organization;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.manager.MCOrgManager;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.MCOrgInfo;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SETeacher;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.protocol.result.SEOrgInfoResult;
import com.snail.education.protocol.service.SEOrgService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.index.activity.TeacherCourseFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tree.love.animtabsview.AnimTabsView;

public class OrganizationInfoActivity extends SEBaseActivity {

    private AnimTabsView mTabsView;
    private ImageView orgAvatar;

    private MCOrgInfo orgInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_info);

        mTabsView = (AnimTabsView) findViewById(R.id.publiclisten_tab);
        mTabsView.addItem("简介");
        mTabsView.addItem("名师");
        mTabsView.addItem("报名");
        orgAvatar = (ImageView) findViewById(R.id.orgAvatar);

        orgInfo = (MCOrgInfo)getIntent().getSerializableExtra(OrganizationFragment.ORG_KEY);
        setTitleText(orgInfo.name);
        updateAttendCount();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(orgInfo.address, orgAvatar, options);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, OrgInfoFragment.newInstance(orgInfo)).commit();
        }

        mTabsView.setOnAnimTabsItemViewChangeListener(new AnimTabsView.IAnimTabsItemViewChangeListener() {
            @Override
            public void onChange(AnimTabsView tabsView, int targetPosition, int currentPosition) {
                switch (targetPosition) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, OrgInfoFragment.newInstance(orgInfo)).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, OrgTeacherFragment.newInstance(orgInfo)).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, OrgEnrollFragment.newInstance(orgInfo)).commit();
                        break;
                }
            }
        });
    }

    // 更新机构关注人数
    private void updateAttendCount(){
        MCOrgManager om = MCOrgManager.getInstance();
        om.updateAttendCount(orgInfo.id, new Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
