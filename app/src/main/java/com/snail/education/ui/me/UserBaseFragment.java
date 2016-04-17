package com.snail.education.ui.me;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.snail.education.R;
import com.snail.education.app.SEConfig;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.ui.BaseSearchActivity;
import com.snail.education.ui.IndexActivity;
import com.snail.education.ui.me.activity.UserEnrollActivity;
import com.snail.education.ui.me.activity.UserLoginActivity;
import com.snail.education.ui.me.activity.UserUpdateActivity;
import com.snail.education.ui.me.fragment.ChenxingFragment;
import com.snail.education.ui.me.fragment.UserCollectionFragment;
import com.snail.education.ui.me.fragment.UserDownloadFragment;
import com.snail.education.ui.setting.SettingActivity;
import com.squareup.picasso.Picasso;

import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tianxiaopeng on 15-1-26.
 */


public class UserBaseFragment extends Fragment implements View.OnClickListener {

    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private static final int USER_UPDATE = 0x0103;

    private SEUser currentUser;

    private MyPagerAdapter mAdapter;
    private NoScrollViewPager mPager;

    private RelativeLayout headRL;
    private SegmentedGroup segmentedGroup;

    private RoundedImageView avator;
    private TextView nameTV, localTV;
    private TextView collectionTV, downloadTV;
    private ImageView collectIV;
    private ImageView iv_setting;
    private RadioButton segmentButton1;
    private RadioButton segmentButton2;
    private RadioButton segmentButton3;

    private Button btn_login;

    private SEAuthManager am;

    private static int USER_LOGIN = 0x0102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_base, container, false);

        // 解决使用SurfaceView切換時閃一下黑屏的问题
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);

        mAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (NoScrollViewPager) v.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setNoScroll(true);

        headRL = (RelativeLayout) v.findViewById(R.id.headRL);
        segmentedGroup = (SegmentedGroup) v.findViewById(R.id.segmented);

        iv_setting = (ImageView) v.findViewById(R.id.iv_setting);
        collectIV = (ImageView) v.findViewById(R.id.iv_collect_list);

        avator = (RoundedImageView) v.findViewById(R.id.iv_avatar);
        nameTV = (TextView) v.findViewById(R.id.tv_name);
        localTV = (TextView) v.findViewById(R.id.tv_local);

        collectionTV = (TextView) v.findViewById(R.id.tv_collection);
        downloadTV = (TextView) v.findViewById(R.id.tv_download);

        segmentButton1 = (RadioButton) v.findViewById(R.id.segment_button1);
        segmentButton2 = (RadioButton) v.findViewById(R.id.segment_button2);
        segmentButton3 = (RadioButton) v.findViewById(R.id.segment_button3);

        segmentButton1.setChecked(true);

        btn_login = (Button) v.findViewById(R.id.button_login);

        am = SEAuthManager.getInstance();
        if (am.isAuthenticated()) {
            currentUser = am.getAccessUser();
            if (currentUser != null) {
                Picasso.with(getActivity())
                        .load(SEConfig.getInstance().getAPIBaseURL() + "/upload/" + currentUser.getAvator())
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .resize(150, 150)
                        .centerCrop()
                        .into(avator);
                if (!TextUtils.isEmpty(currentUser.getName())) {
                    nameTV.setText(currentUser.getName());
                } else {
                    nameTV.setText("Swift Academy");
                }

                if (!TextUtils.isEmpty(currentUser.getLocal())) {
                    localTV.setText(currentUser.getLocal());
                } else {
                    setupLocalInfo();
                }
            }
            btn_login.setBackgroundResource(R.drawable.ic_edit);
        } else {
            setupLocalInfo();
            btn_login.setBackgroundResource(R.drawable.ic_login_blue);
        }

        iv_setting.setOnClickListener(this);

        segmentButton1.setOnClickListener(this);
        segmentButton2.setOnClickListener(this);
        segmentButton3.setOnClickListener(this);

        btn_login.setOnClickListener(this);
        collectIV.setOnClickListener(this);

        register();

        return v;
    }

    private void setupLocalInfo() {
        locationClient = new LocationClient(getActivity());
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);                                //是否打开GPS
        option.setIsNeedAddress(true);
        option.setAddrType("all");
        option.setCoorType("bd09ll");                           //设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst);  //设置定位优先级
        option.setProdName("极速学院");                          //设置产品线名称。
        option.setScanSpan(UPDATE_TIME);                        //设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);

        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                if (!TextUtils.isEmpty(location.getAddrStr())) {
                    localTV.setText(location.getAddrStr());
                    locationClient.stop();
                }
            }
        });

        locationClient.start();
        locationClient.requestLocation();
    }

    private void register() {
        IntentFilter filter1 = new IntentFilter("com.swiftacademy.screen.changed");
        getActivity().registerReceiver(screenReceirver, filter1);

        IntentFilter filter2 = new IntentFilter("com.swiftacademy.count.changed");
        getActivity().registerReceiver(countReceirver, filter2);

    }

    private void unregister() {
        getActivity().unregisterReceiver(screenReceirver);
        getActivity().unregisterReceiver(countReceirver);
    }

    // 监听视频横竖屏
    private BroadcastReceiver screenReceirver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean enterFullScreen = intent.getBooleanExtra("enterFullScreen", false);
            if (enterFullScreen) {
                headRL.setVisibility(View.GONE);
                segmentedGroup.setVisibility(View.GONE);
            } else {
                headRL.setVisibility(View.VISIBLE);
                segmentedGroup.setVisibility(View.VISIBLE);
            }
        }
    };

    // 监听收藏和下载数量变化
    private BroadcastReceiver countReceirver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int collectionCount = intent.getIntExtra("collectionCount", -1);
            int downloadCount = intent.getIntExtra("downloadCount", -1);
            if (collectionCount != -1)
                collectionTV.setText(collectionCount + " 已收藏");
            if (downloadCount != -1)
                downloadTV.setText(downloadCount + " 已下载");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
        unregister();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.segment_button1:
                updateFragment(0);
                break;
            case R.id.segment_button2:
                updateFragment(1);
                break;
            case R.id.segment_button3:
                updateFragment(2);
                break;
            case R.id.iv_collect_list:
                Intent collectIntent = new Intent(getActivity(), UserEnrollActivity.class);
                startActivity(collectIntent);
                break;
            case R.id.button_login:
                if (am.isAuthenticated()) {
                    Intent intent = new Intent(getActivity(), UserUpdateActivity.class);
                    startActivityForResult(intent, USER_UPDATE);
                } else {
                    Intent intent = new Intent(getActivity(), UserLoginActivity.class);
                    intent.putExtra("isVisitor", 1);
                    startActivityForResult(intent, USER_LOGIN);
                }
                break;
            case R.id.iv_setting:
                showBottomPopWindow();
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(BaseSearchActivity.MENU_SEARCH).setVisible(false);
    }

    private void showBottomPopWindow() {
        final View popView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_popwindow_bottom, null);
        final PopupWindow popWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        TextView tv_collect = (TextView) popView.findViewById(R.id.pop_first);
        TextView tv_download = (TextView) popView.findViewById(R.id.pop_second);
        TextView tv_setting = (TextView) popView.findViewById(R.id.pop_third);
        TextView tv_logout = (TextView) popView.findViewById(R.id.pop_fourth);
        TextView tv_cancel = (TextView) popView.findViewById(R.id.pop_cancel);
        View rootView = popView.findViewById(R.id.pop_root);

        if (!SEAuthManager.getInstance().isAuthenticated()) {
            tv_logout.setVisibility(View.GONE);
        }

        tv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllCollection();
                popWindow.dismiss();
            }
        });
        tv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllDownload();
                popWindow.dismiss();
            }
        });
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                logout();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
        popWindow.setTouchable(true);
        popWindow.setOutsideTouchable(true);
        //影响返回键所需
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void removeAllDownload(){
        Intent intent = new Intent("com.swiftacademy.download.clear");
        getActivity().sendBroadcast(intent);
    }

    private void removeAllCollection() {
        SEAuthManager am = SEAuthManager.getInstance();
        if (am.isAuthenticated()) {
            SECourseManager cm = SECourseManager.getInstance();
            cm.removeAllCollection(am.getAccessUser().getId(), new Callback<MCCommonResult>() {
                @Override
                public void success(MCCommonResult result, Response response) {
                    Intent intent = new Intent("com.swiftacademy.course.collection");
                    getActivity().sendBroadcast(intent);
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void logout() {
        SEAuthManager.getInstance().updateUserInfo(null);
        Intent intent = new Intent(getActivity(), IndexActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            SEUser user = (SEUser) data.getSerializableExtra("userInfo");
            if (!TextUtils.isEmpty(user.getName()))
                nameTV.setText(user.getName());
            if (!TextUtils.isEmpty(user.getLocal()))
                localTV.setText(user.getLocal());
            if (!TextUtils.isEmpty(user.getAvator())) {
                Picasso.with(getActivity())
                        .load(SEConfig.getInstance().getAPIBaseURL() + "/upload/" + user.getAvator())
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .resize(150, 150)
                        .centerCrop()
                        .into(avator);
            }
            btn_login.setBackgroundResource(R.drawable.ic_edit);
        }
    }


    public void updateFragment(int type) {
        mPager.setCurrentItem(type);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private int mCount = 3;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (0 == position) {
                return new UserCollectionFragment();
            } else if (1 == position) {
                return new UserDownloadFragment();
            } else if (2 == position) {
                return new ChenxingFragment();
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }

    }

}