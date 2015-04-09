package com.snail.education.ui.index.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.snail.education.R;
import com.snail.education.protocol.model.SECourse;
import com.snail.education.ui.course.RelativeCourseAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherCourseFragment extends Fragment {

    private static final String ARG_COURSE = "courseList";
    private ListView courseListView;

    public TeacherCourseFragment() {
        // Required empty public constructor
    }


    public static TeacherCourseFragment newInstance(ArrayList<SECourse> arg) {
        TeacherCourseFragment fragment = new TeacherCourseFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_COURSE, arg);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_course, container, false);
        ArrayList<SECourse> courseList = (ArrayList<SECourse>) getArguments().getSerializable(ARG_COURSE);
        courseListView = (ListView) rootView.findViewById(R.id.courseListView);
        RelativeCourseAdapter adapter = new RelativeCourseAdapter(getActivity(), courseList);
        courseListView.setAdapter(adapter);
        return rootView;
    }


}
