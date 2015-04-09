package com.snail.education.ui.index.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.snail.education.R;
import com.snail.education.protocol.model.SETeacher;
import com.snail.education.ui.index.adapter.OrgTeacherAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrgTeacherFragment extends Fragment {

    private static final String ARG_TEACHER = "teacherList";
    private ListView teacherListView;

    public OrgTeacherFragment() {
        // Required empty public constructor
    }

    public static OrgTeacherFragment newInstance(ArrayList<SETeacher> teacherList) {
        OrgTeacherFragment orgTeacherFragment = new OrgTeacherFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TEACHER, teacherList);
        orgTeacherFragment.setArguments(bundle);
        return orgTeacherFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_org_teacher, container, false);
        ArrayList<SETeacher> teacherList = (ArrayList<SETeacher>) getArguments().getSerializable(ARG_TEACHER);
        teacherListView = (ListView) rootView.findViewById(R.id.teacherList);
        final OrgTeacherAdapter adapter = new OrgTeacherAdapter(getActivity(), teacherList);
        teacherListView.setAdapter(adapter);
        teacherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TeacherInfoActivity.class);
                intent.putExtra("id", ((SETeacher) adapter.getItem(position)).getId());
                startActivity(intent);
            }
        });
        return rootView;
    }


}
