package com.snail.education.ui.index.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.CourseAdapter;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;

public class AuditionActivity extends SEBaseActivity {

    private PullToRefreshListView courseListView;
    private CourseAdapter adapter;
    private SEDataRetriever dataRetriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audition);
        setTitleText("蜗牛试听");

        this.setDataRetriver(new SEDataRetriever() {
            @Override
            public void refresh(SECallBack callback) {
                if (adapter != null) {
                    //adapter.refresh("free", 0, 0, 0, callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }

            @Override
            public void loadMore(SECallBack callback) {
                if (callback != null) {
                    callback.success();
                }
            }
        });

        courseListView = (PullToRefreshListView) findViewById(R.id.courseListView);
        courseListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
//        adapter = new CourseAdapter(this);
//        courseListView.setAdapter(adapter);
//
//        adapter.refresh("free", 0, 0, 0, null);
    }

    private void performRefresh() {
        if (dataRetriver != null) {
            dataRetriver.refresh(new SECallBack() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(ServiceError error) {
                    Toast.makeText(AuditionActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    private void performLoadMore() {
        if (dataRetriver != null) {
            dataRetriver.loadMore(new SECallBack() {
                @Override
                public void success() {
                    stopRefreshAnimation();
                }

                @Override
                public void failure(ServiceError error) {
                    Toast.makeText(AuditionActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    public void setDataRetriver(SEDataRetriever dataRetriver) {
        this.dataRetriver = dataRetriver;
    }

    private void startRefreshAnimation() {
        if (courseListView != null) {
            courseListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (courseListView != null) {
            courseListView.onRefreshComplete();
        }
    }

}