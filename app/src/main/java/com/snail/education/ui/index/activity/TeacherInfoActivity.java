package com.snail.education.ui.index.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.result.SETeacherInfoResult;
import com.snail.education.protocol.service.SETeacherService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import tree.love.animtabsview.AnimTabsView;

public class TeacherInfoActivity extends SEBaseActivity {

    private String id;

    private AnimTabsView mTabsView;
    private ImageView teacherAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        mTabsView = (AnimTabsView) findViewById(R.id.publiclisten_tab);
        mTabsView.addItem("简介");
        mTabsView.addItem("课程");

        teacherAvatar = (ImageView) findViewById(R.id.teacherAvatar);

        id = getIntent().getStringExtra("id");
        initTeacherBasicInfo();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, TeacherInfoFragment.newInstance(id)).commit();
        }

        mTabsView.setOnAnimTabsItemViewChangeListener(new AnimTabsView.IAnimTabsItemViewChangeListener() {
            @Override
            public void onChange(AnimTabsView tabsView, int oldPosition, int currentPosition) {
                switch (currentPosition) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new TeacherCourseFragment()).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, TeacherInfoFragment.newInstance(id)).commit();
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
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
