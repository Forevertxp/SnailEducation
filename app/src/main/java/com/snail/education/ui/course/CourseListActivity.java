package com.snail.education.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CourseListActivity extends SEBaseActivity implements View.OnClickListener {

    private PullToRefreshListView courseListView;
    private TextView cateTV, organizationTV, teacherTV;
    private CourseAdapter adapter;
    private SEDataRetriever dataRetriver;

    private PopupWindow popupWindow;
    private ListView listView;
    private int mScreenWidth;

    private int cid = 0; // 课程类别id
    private int tid = 0; // 名师id
    private int oid = 0; // 机构id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        setTitleText("蜗牛课程");
        cid = getIntent().getExtras().getInt("cid");

        this.setDataRetriver(new SEDataRetriever() {
            @Override
            public void refresh(SECallBack callback) {
                if (adapter != null) {
                    adapter.refresh("", tid, oid, cid, callback);
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

        cateTV = (TextView) findViewById(R.id.cateTV);
        organizationTV = (TextView) findViewById(R.id.organizationTV);
        teacherTV = (TextView) findViewById(R.id.teacherTV);
        cateTV.setOnClickListener(this);
        organizationTV.setOnClickListener(this);
        teacherTV.setOnClickListener(this);

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
        adapter = new CourseAdapter(this);
        courseListView.setAdapter(adapter);

        adapter.refresh("", tid, oid, cid, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cateTV:
                initControls();
                if (!popupWindow.isShowing()) {
                    popupWindow.showAsDropDown(cateTV, 0, 0);
                }
                break;
            case R.id.organizationTV:
                break;
            case R.id.teacherTV:
                break;
            default:
                break;
        }

    }

    private void initControls() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.view_popup_window, null);

        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.item_popup_window,
                new String[]{"text"},
                new int[]{R.id.item});
        listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        popupWindow = new PopupWindow(view, mScreenWidth / 3,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_bg));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.update();
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);

    }

    private List<Map<String, String>> getData() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("text", "全部分类");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("text", "面试");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("text", "英语");
        list.add(map);

        map = new HashMap<String, String>();
        map.put("text", "数学");
        list.add(map);
        return list;
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
                    Toast.makeText(CourseListActivity.this, error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CourseListActivity.this, error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
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