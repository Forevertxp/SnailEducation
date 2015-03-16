package com.snail.education.ui.index.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.manager.SEStudentManager;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SECourseDetail;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEStudentService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.RelativeCourseAdapter;
import com.snail.svprogresshud.SVProgressHUD;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class StudentInfoActivity extends SEBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        initStudentInfo();
    }


    /**
     * 具体学生信息
     */
    private void initStudentInfo() {

        SEStudentService seStudentService = SERestManager.getInstance().create(SEStudentService.class);
        SVProgressHUD.showInView(this, "正在加载，请稍后...", true);
        seStudentService.fetchStudentInfo(7, new Callback<String>() {
            @Override
            public void success(String result, Response response) {
                TextView textView = (TextView) findViewById(R.id.test);
                textView.setText(result);
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(StudentInfoActivity.this);
                TextView textView = (TextView) findViewById(R.id.test);
                textView.setText(error.getMessage());
            }
        });
    }

}
