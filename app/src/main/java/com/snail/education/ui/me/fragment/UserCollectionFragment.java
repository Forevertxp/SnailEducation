package com.snail.education.ui.me.fragment;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.MCCollectionItem;
import com.snail.education.protocol.model.MCVideo;
import com.snail.education.protocol.model.SECourseCate;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.MCCollectionResult;
import com.snail.education.ui.MainActivity;
import com.snail.education.ui.course.CourseDetailActivity;
import com.snail.education.ui.course.CourseListActivity;
import com.snail.education.ui.me.adapter.MCCollectionAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserCollectionFragment extends Fragment {

    private StickyListHeadersListView collectionLV;
    private MCCollectionAdapter adapter;
    private ArrayList<MCCollectionItem> collectionList;

    private Button btn_add;


    public UserCollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_collection, container, false);

        register();

        btn_add = (Button) v.findViewById(R.id.add_course);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.swiftacademy.tab.change");
                getActivity().sendBroadcast(intent);
            }
        });
        collectionLV = (StickyListHeadersListView) v.findViewById(R.id.collectionLV);
        collectionList = new ArrayList<MCCollectionItem>();

        if (SEAuthManager.getInstance().isAuthenticated()) {
            initData();
        }

        collectionLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MCCollectionItem item = collectionList.get(position);
                if (item.collectionType == 1) {
                    Intent intent = new Intent(getActivity(),CourseListActivity.class);
                    intent.putExtra("pid",item.id);
                    startActivity(intent);
                } else {
                    MCVideo videoInfo = new MCVideo();
                    videoInfo.id = item.id;
                    videoInfo.name = item.name;
                    videoInfo.address = item.address;
                    Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("videoInfo", videoInfo);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        return v;
    }

    private void register() {
        IntentFilter filter = new IntentFilter("com.swiftacademy.course.collection");
        getActivity().registerReceiver(changeTabReceiver, filter);
    }

    private void unregister() {
        getActivity().unregisterReceiver(changeTabReceiver);
    }

    private BroadcastReceiver changeTabReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    };

    private void initData() {
        SECourseManager cm = SECourseManager.getInstance();
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        cm.fetchCollectionVideoList(user.getId(), new Callback<MCCollectionResult>() {
            @Override
            public void success(MCCollectionResult result, Response response) {
                if (collectionList.size() > 0)
                    collectionList.clear();
                for (MCCollectionItem item : result.result.courseList) {
                    item.collectionType = 1;
                    collectionList.add(item);
                }
                for (MCCollectionItem item : result.result.videoList) {
                    item.collectionType = 2;
                    collectionList.add(item);
                }
                if (collectionList.size() > 0) {
                    btn_add.setVisibility(View.GONE);
                } else {
                    btn_add.setVisibility(View.VISIBLE);
                }
                Intent intent = new Intent("com.swiftacademy.count.changed");
                intent.putExtra("collectionCount",collectionList.size());
                getActivity().sendBroadcast(intent);
                adapter = new MCCollectionAdapter(getActivity(), collectionList);
                collectionLV.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregister();
    }
}
