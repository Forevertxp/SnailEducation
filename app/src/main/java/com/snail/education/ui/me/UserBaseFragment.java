package com.snail.education.ui.me;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.ui.BaseSearchActivity;

/**
 * Created by tianxiaopeng on 15-1-26.
 */


public class UserBaseFragment extends Fragment {
    private MyPagerAdapter1 mAdapter;
    private NoScrollViewPager mPager;
    private static int FRAGMENT_LOGOUT = 0;
    private static int FRAGMENT_LOGIN = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_base, container, false);
        mAdapter = new MyPagerAdapter1(getFragmentManager());
        mPager = (NoScrollViewPager) v.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setNoScroll(true);

        if(SEAuthManager.getInstance().isAuthenticated()){
            updateFragment(FRAGMENT_LOGIN);
        }else {
            updateFragment(FRAGMENT_LOGOUT);
        }
        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(BaseSearchActivity.MENU_SEARCH).setVisible(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.snail.user.login");
        intentFilter.addAction("com.snail.user.logout");
        getActivity().registerReceiver(userStateChangedBroad, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(userStateChangedBroad);
    }

    public void updateFragment(int type) {
        mPager.setCurrentItem(type);
    }

    /**
     * 监听用户登录 登出 ，用于切换fragment
     */
    private final BroadcastReceiver userStateChangedBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.snail.user.login"))
                updateFragment(FRAGMENT_LOGIN);
            if (intent.getAction().equals("com.snail.user.logout"))
                updateFragment(FRAGMENT_LOGOUT);
        }
    };


    private class MyPagerAdapter1 extends FragmentStatePagerAdapter {

        private int mCount = 2;

        public MyPagerAdapter1(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (0 == position) {
                return new UserLoginFragment();
            } else if (1 == position) {
                return new UserMeFragment();
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