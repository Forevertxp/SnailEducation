package com.snail.education.ui.story;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.BaseSearchActivity;
import com.snail.photo.activity.UploadPicActivity;
import com.snail.photo.upload.Constants;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;

public class StoryFragment extends Fragment {


    private PullToRefreshListView storyListView;
    private StoryAdapter adapter;
    private SEDataRetriever dataRetriver;

    private static int UPLOAD = 0x12;

    public StoryFragment() {
        super();
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mMainView = inflater.inflate(R.layout.fragment_story, container, false);
        storyListView = (PullToRefreshListView) mMainView.findViewById(R.id.storyListView);
        storyListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                performRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                performLoadMore();
            }
        });
        adapter = new StoryAdapter(getActivity());
        storyListView.setAdapter(adapter);

        return mMainView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(BaseSearchActivity.MENU_SEARCH).setVisible(false);
        MenuItem item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "发表");
        item.setIcon(R.drawable.ic_add_photo);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Menu.NONE) {
            if (!SEAuthManager.getInstance().isAuthenticated()) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("您尚未登录")
                        .setMessage("登录后才能购买，是否去登录？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            }
            Intent intent = new Intent(getActivity(), UploadPicActivity.class);
            Constants.upload_uid = SEAuthManager.getInstance().getAccessUser().getId();
            Constants.upload_result = false;
            startActivityForResult(intent, UPLOAD);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshIfNeeded();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD && Constants.upload_result) {
            performRefresh();
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
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("刷新失败"), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), error.getMessageWithPrompt("无法加载更多"), Toast.LENGTH_SHORT).show();
                    stopRefreshAnimation();
                }
            });
        }
    }

    public void setDataRetriver(SEDataRetriever dataRetriver) {
        this.dataRetriver = dataRetriver;
    }

    private void startRefreshAnimation() {
        if (storyListView != null) {
            storyListView.setRefreshing(true);
        }
    }

    private void stopRefreshAnimation() {
        if (storyListView != null) {
            storyListView.onRefreshComplete();
        }
    }

}


