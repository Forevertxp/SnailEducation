package com.snail.education.ui.index.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SETeacher;
import com.snail.education.protocol.result.SEOrgInfoResult;
import com.snail.education.protocol.result.SETeacherInfoResult;
import com.snail.education.protocol.service.SEOrgService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tree.love.animtabsview.AnimTabsView;

public class OrganizationInfoActivity extends SEBaseActivity {

    private String id;
    private ArrayList<SECourse> courseList;
    private ArrayList<SETeacher> teacherList;

    private AnimTabsView mTabsView;
    private ImageView teacherAvatar;
    private TextView nameText, subjectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_info);
        setTitleText("服务机构");
        mTabsView = (AnimTabsView) findViewById(R.id.publiclisten_tab);
        mTabsView.addItem("简介");
        mTabsView.addItem("名师");
        mTabsView.addItem("课程");

        teacherAvatar = (ImageView) findViewById(R.id.teacherAvatar);
        nameText = (TextView) findViewById(R.id.nameText);
        subjectText = (TextView) findViewById(R.id.subjectText);

        id = getIntent().getStringExtra("id");
        initOrgBasicInfo();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, OrgInfoFragment.newInstance(id)).commit();
        }

        mTabsView.setOnAnimTabsItemViewChangeListener(new AnimTabsView.IAnimTabsItemViewChangeListener() {
            @Override
            public void onChange(AnimTabsView tabsView, int targetPosition, int currentPosition) {
                switch (targetPosition) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, OrgInfoFragment.newInstance(id)).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, OrgTeacherFragment.newInstance(teacherList)).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, TeacherCourseFragment.newInstance(courseList)).commit();
                        break;
                }
            }
        });
    }

    private void initOrgBasicInfo() {
        SEOrgService orgService = SERestManager.getInstance().create(SEOrgService.class);
        orgService.fetchOrgInfo(id, new Callback<SEOrgInfoResult>() {
            @Override
            public void success(SEOrgInfoResult result, Response response) {
                if (result.state) {
                    Picasso.with(OrganizationInfoActivity.this)
                            .load(SEConfig.getInstance().getAPIBaseURL() + result.data.getIcon())
                            .into(teacherAvatar);
                    courseList = result.data.getClassList();
                    teacherList = result.data.getTeacherList();
                    nameText.setText(result.data.getName());
                    subjectText.setText("创建年份：" + result.data.getStartyear());
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
