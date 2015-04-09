package com.snail.education.ui.index.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.index.adapter.StudentAdapter;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;

import java.io.Serializable;

public class StudentActivity extends SEBaseActivity {

    private PullToRefreshListView studentListView;
    private StudentAdapter adapter;
    private SEDataRetriever dataRetriver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        setTitleText("学员风采");

        this.setDataRetriver(new SEDataRetriever() {
            @Override
            public void refresh(SECallBack callback) {
                if (adapter != null) {
                    adapter.refresh(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }

            @Override
            public void loadMore(SECallBack callback) {
                if (adapter != null) {
                    adapter.loadMore(callback);
                } else {
                    if (callback != null) {
                        callback.success();
                    }
                }
            }
        });

        studentListView = (PullToRefreshListView) findViewById(R.id.studentListView);
        studentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
        adapter = new StudentAdapter(this);
        studentListView.setAdapter(adapter);
        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(StudentActivity.this, StudentInfoActivity.class);
                intent.putExtra("student", (Serializable) adapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshIfNeeded();
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
                    Toast.makeText(StudentActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(StudentActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    public void setDataRetriver(SEDataRetriever dataRetriver) {
        this.dataRetriver = dataRetriver;
    }

    private void startRefreshAnimation() {
        if (studentListView != null) {
            studentListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (studentListView != null) {
            studentListView.onRefreshComplete();
        }
    }

}
