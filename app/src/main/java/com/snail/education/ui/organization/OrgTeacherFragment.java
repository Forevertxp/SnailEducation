package com.snail.education.ui.organization;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.snail.education.R;
import com.snail.education.protocol.manager.MCOrgManager;
import com.snail.education.protocol.model.MCOrgInfo;
import com.snail.education.protocol.model.MCTeacher;
import com.snail.education.protocol.result.MCTeacherResult;
import com.snail.education.ui.index.activity.TeacherInfoActivity;
import com.snail.education.ui.index.adapter.OrgTeacherAdapter;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrgTeacherFragment extends Fragment {

    private ListView teacherListView;
    private String orgId;
    private OrgTeacherAdapter adapter;

    private static final String ORG_INFO = "org_info";

    public OrgTeacherFragment() {
        // Required empty public constructor
    }

    public static OrgTeacherFragment newInstance(MCOrgInfo arg) {
        OrgTeacherFragment fragment = new OrgTeacherFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ORG_INFO, arg);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_org_teacher, container, false);

        teacherListView = (ListView) rootView.findViewById(R.id.teacherList);
        teacherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TeacherInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("teacherInfo",((MCTeacher) adapter.getItem(position)));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        MCOrgInfo orgInfo = (MCOrgInfo) getArguments().getSerializable(ORG_INFO);
        if (orgInfo!=null){
            orgId = orgInfo.id;
            initData();
        }
        return rootView;
    }

    private void initData(){
        MCOrgManager orgManager = MCOrgManager.getInstance();
        orgManager.fetchTeacherList(orgId, new Callback<MCTeacherResult>() {
            @Override
            public void success(MCTeacherResult result, Response response) {
                adapter = new OrgTeacherAdapter(getActivity(), result.teacherArrayList);
                teacherListView.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
