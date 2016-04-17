package com.snail.education.ui.course;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.snail.education.R;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.MCSubCourse;
import com.snail.education.protocol.model.SECourseCate;
import com.snail.education.protocol.result.MCCourseListResult;
import com.snail.education.ui.BaseSearchActivity;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;
import com.snail.svprogresshud.SVProgressHUD;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CourseFragment extends Fragment {


    private PullToRefreshListView courseListView;
    private CourseAdapter adapter;
    private ArrayList<MCSubCourse> courseArrayList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mMainView = inflater.inflate(R.layout.fragment_information, container, false);

        setupNavBar(mMainView);

        courseListView = (PullToRefreshListView) mMainView.findViewById(R.id.infoListView);
        performRefresh();
        courseListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SECourseCate courseCate = (SECourseCate) adapter.getItem(position - 1);
                int cid = courseCate.getId();
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                intent.putExtra("cid", cid);
                startActivity(intent);
            }
        });
        return mMainView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(BaseSearchActivity.MENU_SEARCH).setVisible(false);
    }

    private void setupNavBar(View rootView) {
        RelativeLayout titleRL = (RelativeLayout) rootView.findViewById(R.id.titleRL);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titleRL.getLayoutParams();
        lp.height = getNavigationBarHeight();
        titleRL.setLayoutParams(lp);
    }

    private void performRefresh() {
        SECourseManager courseManager = SECourseManager.getInstance();
        courseManager.fetchHomeCourseList("0", new Callback<MCCourseListResult>() {
            @Override
            public void success(MCCourseListResult result, Response response) {
                if (!result.apicode.equals("10000")) {
                    SVProgressHUD.showInViewWithoutIndicator(getActivity(), result.message, 2.0f);
                } else {
                    courseArrayList = result.course.courseArrayList;
                    adapter = new CourseAdapter(getActivity(), courseArrayList);
                    courseListView.setAdapter(adapter);
                }
                courseListView.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                courseListView.onRefreshComplete();
            }
        });
    }

    private int getNavigationBarHeight() {
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

}

