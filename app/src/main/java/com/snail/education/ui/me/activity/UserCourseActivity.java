package com.snail.education.ui.me.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.course.CourseAdapter;
import com.snail.education.ui.me.adapter.UserCourseAdapter;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;
import com.snail.segmented.SegmentedRadioGroup;


public class UserCourseActivity extends SEBaseActivity implements RadioGroup.OnCheckedChangeListener {

    private SegmentedRadioGroup segmentText;
    private RadioButton all, instudy, never, finished;

    private PullToRefreshListView courseListView;
    private UserCourseAdapter adapter;
    private SEDataRetriever dataRetriver;

    private int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_course);
        setTitleText("我的学习进度");

        uid = Integer.parseInt(SEAuthManager.getInstance().getAccessUser().getId());

        segmentText = (SegmentedRadioGroup) findViewById(R.id.segment_text);
        segmentText.setOnCheckedChangeListener(this);

        all = (RadioButton) findViewById(R.id.button_all);
        instudy = (RadioButton) findViewById(R.id.button_instudy);
        never = (RadioButton) findViewById(R.id.button_never);
        finished = (RadioButton) findViewById(R.id.button_finished);
        int width = getResources().getDisplayMetrics().widthPixels / 4 - 10;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        all.setLayoutParams(layoutParams);
        instudy.setLayoutParams(layoutParams);
        never.setLayoutParams(layoutParams);
        finished.setLayoutParams(layoutParams);

        this.setDataRetriver(new SEDataRetriever() {
            @Override
            public void refresh(SECallBack callback) {
                if (adapter != null) {
                    adapter.refresh(0, uid, 1, callback);
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


        courseListView = (PullToRefreshListView) findViewById(R.id.myCourseListView);
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
        adapter = new UserCourseAdapter(this);
        courseListView.setAdapter(adapter);

        adapter.refresh(0, uid, 1, null);
    }


    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.button_all) {
            adapter.refresh(0, uid, 1, null);
        } else if (checkedId == R.id.button_instudy) {
            adapter.refresh(1, uid, 1, null);
        } else if (checkedId == R.id.button_never) {
            adapter.refresh(2, uid, 1, null);
        } else if (checkedId == R.id.button_finished) {
            adapter.refresh(3, uid, 1, null);
        }
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
                    Toast.makeText(UserCourseActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(UserCourseActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
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