package com.snail.education.ui.index.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.SEStudent;
import com.snail.education.protocol.service.SEStudentService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.svprogresshud.SVProgressHUD;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class StudentInfoActivity extends SEBaseActivity {

    private SEStudent student;

    private WebView studentWeb;
    private ImageView stuAvatar;
    private TextView nameText, schoolText, speakText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        student = (SEStudent) getIntent().getSerializableExtra("student");
        setTitleText(student.getName());
        stuAvatar = (ImageView) findViewById(R.id.studentAvatar);
        nameText = (TextView) findViewById(R.id.nameText);
        schoolText = (TextView) findViewById(R.id.schoolText);
        speakText = (TextView) findViewById(R.id.speakText);
        nameText.setText(student.getName());
        schoolText.setText(student.getSchool());
        speakText.setText(student.getSpeak());
        Picasso.with(this)
                .load(SEConfig.getInstance().getAPIBaseURL() + student.getIcon())
                .resize(100, 100)
                .into(stuAvatar);
        studentWeb = (WebView) findViewById(R.id.studentWeb);
        initStudentInfo();
    }


    /**
     * 具体学生信息
     */
    private void initStudentInfo() {

        SEStudentService seStudentService = SERestManager.getInstance().create(SEStudentService.class);
        SVProgressHUD.showInView(this, "正在加载，请稍后...", true);
        seStudentService.fetchStudentInfo(7, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                SVProgressHUD.dismiss(StudentInfoActivity.this);
                TypedInput body = response.getBody();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    StringBuilder out = new StringBuilder();
                    String newLine = System.getProperty("line.separator");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line);
                        out.append(newLine);
                    }
                    String studentHtml = out.toString();
                    studentWeb.loadDataWithBaseURL(null, studentHtml, "text/html", "UTF-8", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.dismiss(StudentInfoActivity.this);
            }
        });
    }

}
