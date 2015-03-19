package com.snail.education.ui.me.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.snail.education.R;
import com.snail.education.database.CourseDB;
import com.snail.education.ui.me.adapter.DownloadAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadingFragment extends BaseFragment {

    private ListView downloadingLV;


    public DownloadingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloading, container, false);
        downloadingLV = (ListView) view.findViewById(R.id.downloadingLV);
        initDownloadingData();
        return view;
    }

    private void initDownloadingData() {
        DbUtils db = DbUtils.create(getActivity());
        try {
            List<CourseDB> list = db.findAll(CourseDB.class);
            DownloadAdapter adapter = new DownloadAdapter(getActivity(), list);
            downloadingLV.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }
}
