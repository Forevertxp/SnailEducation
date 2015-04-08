package com.snail.education.ui.me.activity;


import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.snail.education.R;
import com.snail.education.database.CourseDB;
import com.snail.education.ui.me.adapter.DownloadedAdapter;
import com.snail.education.ui.me.adapter.DownloadingAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadedFragment extends BaseFragment {

    private ListView downloadingLV;
    private List<CourseDB> courseList = new ArrayList<CourseDB>();
    private List<Boolean> boolList = new ArrayList<Boolean>();

    private boolean isChooseAll = false; //标记是否已全选

    public DownloadedAdapter adapter;
    private DownloadedAdapter.ViewHolder viewHolder;

    private DbUtils db;

    public DownloadedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloading, container, false);
        downloadingLV = (ListView) view.findViewById(R.id.downloadingLV);
        db = DbUtils.create(getActivity());
        initDownloadingData();
        downloadingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                viewHolder = (DownloadedAdapter.ViewHolder) view.getTag();
                if (DownloadedAdapter.CHECKBOS_VISIBLE) {
                    viewHolder.cb.toggle();
                    if (viewHolder.cb.isChecked()) {
                        boolList.set(position, true);
                    } else {
                        boolList.set(position, false);
                    }
                } else {
                    doPlay(courseList.get(position).getVideo(), DownloadActivity.SDPATH + "/snailvideo" + courseList.get(position).getId() + ".mp4");
                }
            }
        });
        return view;
    }

    private void initDownloadingData() {
        DbUtils db = DbUtils.create(getActivity());
        try {
            if (db.findAll(CourseDB.class) != null) {
                courseList = db.findAll(Selector.from(CourseDB.class)
                        .where("isdone", "=", 1)
                        .orderBy("id"));
                // checkbox 初始为未选中状态
                for (int i = 0; i < courseList.size(); i++) {
                    boolList.add(false);
                }
                adapter = new DownloadedAdapter(getActivity(), courseList, boolList);
                downloadingLV.setAdapter(adapter);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    public void doPlay(String sourceUrl, String targetUrl) {
        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
        intent.putExtra("videoPath", targetUrl);
        startActivity(intent);
    }

    public void chooseOrDeChoose() {
        if (isChooseAll) {
            for (int i = 0; i < courseList.size(); i++) {
                boolList.set(i, false);
            }
            isChooseAll = false;
        } else {
            for (int i = 0; i < courseList.size(); i++) {
                boolList.set(i, true);
            }
            isChooseAll = true;
        }
        adapter.notifyDataSetChanged();
    }

    public void deleteCourse() {
        for (int i = 0; i < courseList.size(); i++) {
            if (boolList.get(i)) {
                File file = new File(DownloadActivity.SDPATH + "/snailvideo" + courseList.get(i).getId() + ".mp4");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    db.deleteById(CourseDB.class, courseList.get(i).getId());
                    courseList.remove(courseList.get(i));
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}
