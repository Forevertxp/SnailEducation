package com.snail.education.ui.story;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.SEDataRetriever;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SEComment;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.SEStoryRepResult;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.protocol.service.SEStoryService;
import com.snail.education.ui.BaseSearchActivity;
import com.snail.photo.activity.UploadPicActivity;
import com.snail.photo.upload.Constants;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;
import com.snail.svprogresshud.SVProgressHUD;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class StoryFragment extends Fragment {


    private PullToRefreshListView storyListView;
    private StoryAdapter adapter;
    private SEDataRetriever dataRetriver;

    private PopupWindow pop = null;
    private RelativeLayout ll_popup;

    private static int UPLOAD = 0x12;
    private boolean isRegister = false;

    private String storyId;
    private String toUserId;
    private int position;

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

        initPop();
        register();

        return mMainView;
    }

    public void initPop() {
        pop = new PopupWindow(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_popup_comment, null);
        ll_popup = (RelativeLayout) view.findViewById(R.id.ll_popup);


        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        final EditText commentText = (EditText) view
                .findViewById(R.id.commentText);
        parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);
                return false;
            }
        });
        Button bt = (Button) view
                .findViewById(R.id.send);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String comment = commentText.getText().toString();
                sendMessage(comment);
            }
        });
    }

    private void sendMessage(String comment) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        pop.dismiss();
        ll_popup.clearAnimation();
        SEStoryService storyService = SERestManager.getInstance().create(SEStoryService.class);
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        if (user == null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("您尚未登录")
                    .setMessage("登录后才能评论，是否去登录？")
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
            return;
        }
        storyService.repStory(storyId, user.getId(), toUserId, comment, new Callback<SEStoryRepResult>() {
            @Override
            public void success(SEStoryRepResult result, Response response) {
                if (result.state) {
                    adapter.updateView(position, result.data);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                SVProgressHUD.showInViewWithoutIndicator(getActivity(), error.getMessage(), 2.0f);
            }
        });

    }

    private void register() {
        if (!isRegister) {
            IntentFilter filter = new IntentFilter("com.snailedu.sendmessage");
            getActivity().registerReceiver(messageBroad, filter);
            isRegister = true;
        }
    }

    private void unregister() {
        if (isRegister) {
            getActivity().unregisterReceiver(messageBroad);
            isRegister = false;
        }
    }

    private BroadcastReceiver messageBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            storyId = intent.getStringExtra("id");
            toUserId = intent.getStringExtra("toid");
            position = intent.getIntExtra("position", 0);
            View parentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_story, null);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.activity_translate_in));
            pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        }
    };

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
    public void onDestroy() {
        super.onDestroy();
        unregister();
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


