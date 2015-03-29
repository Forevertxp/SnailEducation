package com.snail.education.ui.index.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.index.adapter.SearchResultAdapter;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;

public class SearchResultActivity extends SEBaseActivity {

    private PullToRefreshListView searchListView;
    private SearchResultAdapter adapter;
    private SEDataRetriever dataRetriver;

    private String key;
    private String opt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        key = getIntent().getExtras().getString("keywords");
        opt = "news,qabank,class,teacher,student,organization";
        setTitleText(key);

        this.setDataRetriver(new SEDataRetriever() {
            @Override
            public void refresh(SECallBack callback) {
                if (adapter != null) {
                    adapter.refresh(opt, key, 1, callback);
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


        searchListView = (PullToRefreshListView) findViewById(R.id.searchListView);
        searchListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
        adapter = new SearchResultAdapter(this);
        searchListView.setAdapter(adapter);

        adapter.refresh(opt, key, 1, null);
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
                    Toast.makeText(SearchResultActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SearchResultActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    public void setDataRetriver(SEDataRetriever dataRetriver) {
        this.dataRetriver = dataRetriver;
    }

    private void startRefreshAnimation() {
        if (searchListView != null) {
            searchListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (searchListView != null) {
            searchListView.onRefreshComplete();
        }
    }
}
