package com.snail.education.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.MCVideo;
import com.snail.education.protocol.result.MCSearchResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.CourseDetailActivity;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchResultActivity extends SEBaseActivity {

    private PullToRefreshListView searchListView;
    private SearchResultAdapter adapter;

    private String key;
    private ArrayList<MCVideo> videoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        key = getIntent().getStringExtra("keyword");
        setTitleText(key);

        searchListView = (PullToRefreshListView) findViewById(R.id.searchListView);
        searchListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MCVideo videoInfo = videoArrayList.get(position-1);
                Intent intent = new Intent(SearchResultActivity.this,CourseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoInfo",videoInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        performRefresh();
    }


    private void performRefresh() {
        SECourseManager courseManager = SECourseManager.getInstance();
        courseManager.searchVideoList(key, new Callback<MCSearchResult>() {
            @Override
            public void success(MCSearchResult result, Response response) {
                videoArrayList = result.videoArrayList;
                adapter = new SearchResultAdapter(SearchResultActivity.this,result.videoArrayList);
                searchListView.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
