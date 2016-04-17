package com.snail.education.ui.me.fragment;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.snail.education.R;
import com.snail.education.database.CourseDB;
import com.snail.education.protocol.model.MCVideo;
import com.snail.education.ui.me.adapter.DownloadAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDownloadFragment extends Fragment {

    private SwipeMenuListView downloadingLV;
    private List<CourseDB> courseList = new ArrayList<CourseDB>();

    public DownloadAdapter adapter;
    private DbUtils db;
    private HttpUtils http;


    public UserDownloadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_downloading, container, false);

        db = DbUtils.create(getActivity());
        // 1.创建下载器
        http = new HttpUtils();
        // 2.最大开启线程数量
        http.configRequestThreadPoolSize(3);
        register();

        downloadingLV = (SwipeMenuListView) rootView.findViewById(R.id.downloadingLV);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(160);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        downloadingLV.setMenuCreator(creator);
        downloadingLV.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        downloadingLV.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        adapter.deleteDownloadVideo(position);
                        break;
                }
                return false;
            }
        });

        downloadingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.openVideo(position);
            }
        });

        setupDownloadData();


        return rootView;
    }

    private void register() {
        IntentFilter filter1 = new IntentFilter("com.swiftacademy.download");
        getActivity().registerReceiver(broadcastReceiver, filter1);

        IntentFilter filter2 = new IntentFilter("com.swiftacademy.download.clear");
        getActivity().registerReceiver(clearReceiver, filter2);
    }

    private void unregister() {
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(clearReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MCVideo videoInfo = (MCVideo) intent.getSerializableExtra("videoInfo");
            adapter.addDownloadingVideo(videoInfo);
        }
    };

    // 清空下载
    private BroadcastReceiver clearReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            for (int i = 0; i < courseList.size(); i++) {
                CourseDB video = courseList.get(i);
                if (video.getHandler() != null) {
                    video.getHandler().cancel();
                }
                File file = new File(DownloadAdapter.SDPATH + "/swiftvideo" + video.getId() + ".mp4");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    db.deleteById(CourseDB.class, video.getId());
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
            setupDownloadData();
        }
    };

    private void setupDownloadData() {
        if (courseList.size() > 0)
            courseList.clear();
        try {
            if (db.findAll(CourseDB.class) != null) {
                courseList = db.findAll(Selector.from(CourseDB.class)
                        .orderBy("id"));
            }
            Intent intent = new Intent("com.swiftacademy.count.changed");
            intent.putExtra("downloadCount", courseList.size());
            getActivity().sendBroadcast(intent);
            adapter = new DownloadAdapter(getActivity(), courseList, http);
            downloadingLV.setAdapter(adapter);

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopDownload();
        unregister();
    }
}

