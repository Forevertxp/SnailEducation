package com.snail.education.ui;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snail.education.R;
import com.snail.education.common.SETabBar;
import com.snail.education.common.SEThemer;

/**
 * A simple {@link Fragment} subclass.
 */


public class MainFragment extends Fragment {
    private MainPagerAdapter _viewPagerAdapter;
    private SETabBar _tabBar;
    private static ViewPager _viewPager;

    private static Activity mActivity;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupActionBar();

        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        _viewPager = (ViewPager) fragmentView.findViewById(R.id.MainPager);
        _viewPager.setOffscreenPageLimit(999);
        _viewPagerAdapter = new MainPagerAdapter(getFragmentManager());
        _viewPager.setAdapter(_viewPagerAdapter);
        _viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int index) {
                handlePageSelected(index);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        _tabBar = (SETabBar) fragmentView.findViewById(R.id.TabBar);

        Typeface typeface = SEThemer.getInstance().getWTFont();
        _tabBar.getItemViewAt(0).setIconTextWithTypeface("\uf10d", typeface); // Alt Home Icon
        _tabBar.getItemViewAt(1).setIconTextWithTypeface("\uf10f", typeface); // Compass Icon
        _tabBar.getItemViewAt(2).setIconTextWithTypeface("\uf110", typeface); // Picture Icon
        _tabBar.getItemViewAt(3).setIconTextWithTypeface("\uf10a", typeface); // Alt Community Icon
        _tabBar.getItemViewAt(4).setIconTextWithTypeface("\uf10e", typeface); // Alt User Icon

        _tabBar.getItemViewAt(0).setTitleText("首页");
        _tabBar.getItemViewAt(1).setTitleText("MBA咨询");
        _tabBar.getItemViewAt(2).setTitleText("蜗牛课程");
        _tabBar.getItemViewAt(3).setTitleText("蜗牛故事");
        _tabBar.getItemViewAt(4).setTitleText("蜗牛房");

        _tabBar.setOnTabSelectionEventListener(new SETabBar.OnTabSelectionEventListener() {
            @Override
            public boolean onWillSelectTab(int tabIndex) {
                return handleOnWillSelectTab(tabIndex);
            }

            @Override
            public void onDidSelectTab(int tabIndex) {
                handleOnDidSelectTab(tabIndex);
            }
        });

        _tabBar.limitTabNum(5);
        switchToPage(0);

        return fragmentView;
    }

    private void setupActionBar() {
        ActionBar actionBar = mActivity.getActionBar();
        try {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        } catch (Exception exception) {
            // Too bad, the animation will be run ;(
        }
    }

    private void handlePageSelected(int index) {
        _tabBar.setSelectedTabIndex(index);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean handleOnWillSelectTab(int tabIndex) {
        if (tabIndex == 4) {
//            MWTAuthManager am = MWTAuthManager.getInstance();
//            if (!am.isAuthenticated())
//            {
//                Intent intent = new Intent(getActivity(), MWTAuthSelectActivity.class);
//                startActivityForResult(intent, 1);
//                return false;
//            }
//            else
//            {
//                return true;
//            }
        }

        return true;
    }

    private void handleOnDidSelectTab(int tabIndex) {
        switchToPage(tabIndex);
    }

    public void switchToPage(int tabIndex) {
        switch (tabIndex) {
            case 0:
                setActionBarVisible(false);
                _viewPager.setCurrentItem(tabIndex, false);
                ((MainActivity) mActivity).setTitleText("");
                break;
            case 1:
                setActionBarVisible(true);
                _viewPager.setCurrentItem(tabIndex, false);
                ((MainActivity) mActivity).setTitleText("MBA咨询");
                // getActivity().getActionBar().setTitle("MBA咨询");
                break;
            case 2:
                setActionBarVisible(true);
                _viewPager.setCurrentItem(tabIndex, false);
                ((MainActivity) mActivity).setTitleText("蜗牛课程");
                // getActivity().getActionBar().setTitle("蜗牛课程");
                break;
            case 3:
                setActionBarVisible(true);
                _viewPager.setCurrentItem(tabIndex, false);
                ((MainActivity) mActivity).setTitleText("            蜗牛故事");
                // getActivity().getActionBar().setTitle("蜗牛故事");
                break;
            case 4:
                setActionBarVisible(true);
                _viewPager.setCurrentItem(tabIndex, false);
                ((MainActivity) mActivity).setTitleText("蜗牛房");
                // getActivity().getActionBar().setTitle("蜗牛房");
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
//                MWTAuthManager am = MWTAuthManager.getInstance();
//                if (am.isAuthenticated())
//                {
//                    _tabBar.setSelectedTabIndex(4);
//                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void setActionBarVisible(boolean actionBarVisible) {
        ActionBar actionBar = mActivity.getActionBar();
        try {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        } catch (Exception exception) {
            // Too bad, the animation will be run ;(
        }

        if (actionBarVisible) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }
}

