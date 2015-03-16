package com.snail.education.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.snail.education.ui.information.InformationFragment;
import com.snail.education.ui.course.CourseFragment;
import com.snail.education.ui.index.IndexFragment;
import com.snail.education.ui.me.UserBaseFragment;
import com.snail.education.ui.me.UserMeFragment;
import com.snail.education.ui.story.StoryFragment;

/**
 * Created by tianxiaopeng on 15-1-2.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {


    private IndexFragment indexFragment;
    private InformationFragment consultFragment;
    private CourseFragment courseFragment;
    private StoryFragment storyFragment;
    private UserBaseFragment userFragment;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        construct();
    }

    private void construct() {
        indexFragment = new IndexFragment();
        consultFragment = new InformationFragment();
        courseFragment = new CourseFragment();
        storyFragment = new StoryFragment();
        userFragment = new UserBaseFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return indexFragment;
            case 1:
                return consultFragment;
            case 2:
                return courseFragment;
            case 3:
                return storyFragment;
            case 4:
                return userFragment;
            default:
                return null;
        }
    }

}

