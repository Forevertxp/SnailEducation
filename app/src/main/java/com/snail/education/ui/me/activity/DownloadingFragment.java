package com.snail.education.ui.me.activity;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.snail.education.R;
import com.snail.education.database.CourseDB;
import com.snail.education.ui.me.adapter.DownloadAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadingFragment extends BaseFragment {

    private ListView downloadingLV;
    private List<CourseDB> courseList = new ArrayList<CourseDB>();
    private List<Boolean> boolList = new ArrayList<Boolean>();

    private boolean isChooseAll = false; //标记是否已全选

    private DownloadAdapter adapter;

    private DownloadingListener listener;

    public DownloadingFragment() {
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
                if (DownloadAdapter.CHECKBOS_VISIBLE) {
                    DownloadAdapter.ViewHolder viewHolder = (DownloadAdapter.ViewHolder) view.getTag();
                    viewHolder.cb.toggle();
                    if (viewHolder.cb.isChecked()) {
                        boolList.set(position, true);
                    } else {
                        boolList.set(position, false);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (DownloadingListener) activity;
    }

    private void initDownloadingData() {
        DbUtils db = DbUtils.create(getActivity());
        try {
            courseList = db.findAll(CourseDB.class);
            // checkbox 初始为未选中状态
            for (int i = 0; i < courseList.size(); i++) {
                boolList.add(false);
            }
            adapter = new DownloadAdapter(getActivity(), courseList, boolList);
            downloadingLV.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void chooseAll() {
        for (int i = 0; i < courseList.size(); i++) {
            if (isChooseAll) {
                boolList.add(false);
                isChooseAll = false;
            } else {
                boolList.add(true);
                isChooseAll = true;
            }
        }
        adapter.notifyDataSetChanged();
    }

    public interface DownloadingListener
    {
        public void showMessage(int index);
    }
}
