package com.snail.education.ui.index.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.snail.education.R;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.service.SETeacherService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherInfoFragment extends Fragment {

    private WebView teacherView;

    private static final String ARG_ID = "id";
    private String teacherHtml;

    public TeacherInfoFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String arg) {
        TeacherInfoFragment fragment = new TeacherInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ID, arg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_info, container, false);
        teacherView = (WebView) rootView.findViewById(R.id.teacherInfo);
        initTeacherBasicInfo();
        return rootView;
    }

    private void initTeacherBasicInfo() {
        SETeacherService teacherService = SERestManager.getInstance().create(SETeacherService.class);
        String id = getArguments().getString(ARG_ID);
        if (TextUtils.isEmpty(id)) {
            return;
        }
        teacherService.fetchTeacherInfoHtml(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
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
                    teacherHtml = out.toString();
                    teacherView.loadDataWithBaseURL(null, teacherHtml, "text/html", "UTF-8", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }
}
