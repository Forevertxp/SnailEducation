package com.snail.education.ui.index.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.snail.education.R;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.service.SEOrgService;

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
public class OrgInfoFragment extends Fragment {

    private WebView teacherView;

    private static final String ARG_ID = "id";
    private String teacherHtml;

    public OrgInfoFragment() {
        // Required empty public constructor
    }

    public static OrgInfoFragment newInstance(String arg) {
        OrgInfoFragment fragment = new OrgInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ID, arg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_org_info, container, false);
        teacherView = (WebView) rootView.findViewById(R.id.teacherInfo);
        initTeacherBasicInfo();
        return rootView;
    }

    private void initTeacherBasicInfo() {
        SEOrgService orgService = SERestManager.getInstance().create(SEOrgService.class);
        String id = getArguments().getString(ARG_ID);
        if (TextUtils.isEmpty(id)) {
            return;
        }
        orgService.fetchOrgInfoHtml(id, new Callback<Response>() {
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
