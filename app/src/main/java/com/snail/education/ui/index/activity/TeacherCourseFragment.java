package com.snail.education.ui.index.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snail.education.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherCourseFragment extends Fragment {


    public TeacherCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_course, container, false);
    }


}
