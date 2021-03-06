package com.snail.education.ui.index.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.result.SETeacherInfoResult;
import com.snail.education.protocol.service.SETeacherService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tree.love.animtabsview.AnimTabsView;

public class TeacherInfoActivity extends SEBaseActivity {

    private String id;
    private ArrayList<SECourse> courseList;

    private AnimTabsView mTabsView;
    private ImageView teacherAvatar;
    private TextView nameText, subjectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        setTitleText("名师推荐");
        mTabsView = (AnimTabsView) findViewById(R.id.publiclisten_tab);
        mTabsView.addItem("简介");
        mTabsView.addItem("课程");

        teacherAvatar = (ImageView) findViewById(R.id.teacherAvatar);
        nameText = (TextView) findViewById(R.id.nameText);
        subjectText = (TextView) findViewById(R.id.subjectText);

        id = getIntent().getStringExtra("id");
        initTeacherBasicInfo();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, TeacherInfoFragment.newInstance(id)).commit();
        }

        mTabsView.setOnAnimTabsItemViewChangeListener(new AnimTabsView.IAnimTabsItemViewChangeListener() {
            @Override
            public void onChange(AnimTabsView tabsView, int targetPosition, int currentPosition) {
                switch (targetPosition) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, TeacherInfoFragment.newInstance(id)).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, TeacherCourseFragment.newInstance(courseList)).commit();
                        break;
                }
            }
        });
    }

    private void initTeacherBasicInfo() {
        SETeacherService teacherService = SERestManager.getInstance().create(SETeacherService.class);
        teacherService.fetchTeacherInfo(id, new Callback<SETeacherInfoResult>() {
            @Override
            public void success(SETeacherInfoResult result, Response response) {
                if (result.state) {
                    Picasso.with(TeacherInfoActivity.this)
                            .load(SEConfig.getInstance().getAPIBaseURL() + result.data.getIcon())
                            .into(teacherAvatar);
                    courseList = result.data.getClassList();
                    nameText.setText(result.data.getName());
                    subjectText.setText(result.data.getJob());
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
