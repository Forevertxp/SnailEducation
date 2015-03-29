package com.snail.education.ui.me.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.snail.education.R;
import com.snail.education.database.CourseDB;
import com.snail.education.ui.me.adapter.DownloadAdapter;

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

    public DownloadAdapter adapter;
    private DownloadAdapter.ViewHolder viewHolder;

    private HttpHandler handler;

    public DownloadedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloading, container, false);
        downloadingLV = (ListView) view.findViewById(R.id.downloadingLV);
        initDownloadingData();
        downloadingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                viewHolder = (DownloadAdapter.ViewHolder) view.getTag();
                if (DownloadAdapter.CHECKBOS_VISIBLE) {
                    viewHolder.cb.toggle();
                    if (viewHolder.cb.isChecked()) {
                        boolList.set(position, true);
                    } else {
                        boolList.set(position, false);
                    }
                } else {
                    doPlay(courseList.get(position).getVideo(), "/sdcard/snailvideo" + courseList.get(position).getId() + ".mp4");
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
                adapter = new DownloadAdapter(getActivity(), courseList, boolList);
                downloadingLV.setAdapter(adapter);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    public void doPlay(String sourceUrl, String targetUrl) {

    }

    public void chooseOrDeChoose() {
        for (int i = 0; i < courseList.size(); i++) {
            if (isChooseAll) {
                boolList.set(i, false);
                isChooseAll = false;
            } else {
                boolList.set(i, true);
                isChooseAll = true;
            }
        }
        adapter.notifyDataSetChanged();
    }

}
