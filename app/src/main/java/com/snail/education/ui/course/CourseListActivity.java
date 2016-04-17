package com.snail.education.ui.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.MCCourPoint;
import com.snail.education.protocol.model.MCCourSection;
import com.snail.education.protocol.model.MCCourse;
import com.snail.education.protocol.model.MCVideo;
import com.snail.education.protocol.model.VideoCollection;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.protocol.result.MCCourSectionResult;
import com.snail.education.protocol.result.VideoCollectionResult;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.svprogresshud.SVProgressHUD;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CourseListActivity extends Activity {

    private TextView secTextView;
    private ListView courseListView;
    private ImageView backIV;
    private CourseCateAdapter adapter;

    private RelativeLayout collectRL;
    private TextView nameTV, listenTV, collectionText;
    private ImageView collectImage;

    private Boolean isCollect;

    private MCCourse course;

    private String pid;
    private ArrayList<MCCourPoint> pointArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_course_list);
        pid = getIntent().getExtras().getString("pid");

        backIV = (ImageView) findViewById(R.id.iv_back);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nameTV = (TextView) findViewById(R.id.nameTV);
        listenTV = (TextView) findViewById(R.id.listenTV);
        collectionText = (TextView) findViewById(R.id.collectionText);
        collectRL = (RelativeLayout) findViewById(R.id.collectRL);
        collectImage = (ImageView) findViewById(R.id.collectImage);
        collectRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectCourse();
            }
        });
        secTextView = (TextView) findViewById(R.id.tv_section);
        courseListView = (ListView) findViewById(R.id.lv_point);
        performRefresh();

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MCCourPoint point = pointArrayList.get(position);
                Intent intent = new Intent(CourseListActivity.this, CourseDetailActivity.class);
                intent.putExtra("id", point.id);
                intent.putExtra("name", point.name);
                startActivity(intent);
            }
        });
    }

    public void performRefresh() {
        SECourseManager courseManager = SECourseManager.getInstance();
        courseManager.fetchCourseSection(pid, new Callback<MCCourSectionResult>() {
            @Override
            public void success(MCCourSectionResult result, Response response) {
                if (!result.apicode.equals("10000")) {
                    SVProgressHUD.showInViewWithoutIndicator(CourseListActivity.this, result.message, 2.0f);
                } else {
                    if (result.course.sectionArrayList.size() > 0) {
                        course = result.course;
                        nameTV.setText(result.course.courName);
                        listenTV.setText(result.course.playcount);

                        MCCourSection section = result.course.sectionArrayList.get(0);
                        secTextView.setText(section.childName);
                        pointArrayList = section.pointList;
                        adapter = new CourseCateAdapter(CourseListActivity.this, pointArrayList);
                        courseListView.setAdapter(adapter);
                    }
                    updateCourseInfo();
                    fetchCollectionState();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void updateCourseInfo() {
        SECourseManager cm = SECourseManager.getInstance();
        cm.updateCourseInfo(course.courId, new retrofit.Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void fetchCollectionState() {

        SECourseManager cm = SECourseManager.getInstance();
        cm.getCollectionState(course.courId, "1", new Callback<VideoCollectionResult>() {
            @Override
            public void success(VideoCollectionResult result, Response response) {
                VideoCollection collection = result.videoCollection;
                collectionText.setText(collection.collectCount);
                if (collection.isCollect.equals("1")) {
                    isCollect = true;
                    collectImage.setBackgroundResource(R.drawable.ic_collected);
                } else {
                    isCollect = false;
                    collectImage.setBackgroundResource(R.drawable.ic_collect);
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void collectCourse() {
        SEAuthManager am = SEAuthManager.getInstance();
        if (!am.isAuthenticated()) {
            SVProgressHUD.showInViewWithoutIndicator(CourseListActivity.this, "您尚未登陆", 2.0f);
            return;
        }
        SECourseManager cm = SECourseManager.getInstance();
        if (course == null)
            return;
        cm.collectVideo(course.courId, isCollect ? "0" : "1", am.getAccessUser().getId(), "1", new retrofit.Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {
                if (!result.apicode.equals("10000")) {
                    SVProgressHUD.showInViewWithoutIndicator(CourseListActivity.this, result.message, 2.0f);
                    return;
                }
                if (isCollect) {
                    isCollect = false;
                    collectImage.setBackgroundResource(R.drawable.ic_collect);
                    collectionText.setText(Integer.parseInt(collectionText.getText().toString()) - 1 + "");
                } else {
                    isCollect = true;
                    collectImage.setBackgroundResource(R.drawable.ic_collected);
                    collectionText.setText(Integer.parseInt(collectionText.getText().toString()) + 1 + "");
                }

                Intent intent = new Intent("com.swiftacademy.course.collection");
                sendBroadcast(intent);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}