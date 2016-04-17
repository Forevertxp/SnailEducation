package com.snail.education.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.snail.education.ui.information.InformationFragment;
import com.snail.education.ui.course.CourseFragment;
import com.snail.education.ui.index.IndexFragment;
import com.snail.education.ui.me.UserBaseFragment;
import com.snail.education.ui.me.UserMeFragment;
import com.snail.education.ui.organization.OrganizationFragment;
import com.snail.education.ui.search.SearchFragment;
import com.snail.education.ui.story.StoryFragment;

/**
 * Created by tianxiaopeng on 15-1-2.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {


    private IndexFragment indexFragment;
    private InformationFragment consultFragment;
    private StoryFragment storyFragment;

    private OrganizationFragment organizationFragment;
    private SearchFragment searchFragment;
    private CourseFragment courseFragment;
    private UserBaseFragment userFragment;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        construct();
    }

    private void construct() {
        indexFragment = new IndexFragment();
        consultFragment = new InformationFragment();
        storyFragment = new StoryFragment();

        courseFragment = new CourseFragment();
        searchFragment = new SearchFragment();
        organizationFragment = new OrganizationFragment();
        userFragment = new UserBaseFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return courseFragment;
            case 1:
                return organizationFragment;
            case 2:
                return searchFragment;
            case 3:
                return userFragment;
            default:
                return null;
        }
    }

}

