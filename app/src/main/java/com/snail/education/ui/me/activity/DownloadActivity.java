package com.snail.education.ui.me.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.me.NoScrollViewPager;
import com.snail.education.ui.me.adapter.DownloadedAdapter;
import com.snail.education.ui.me.adapter.DownloadingAdapter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends SEBaseActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = "DxFragmentActivity";

    public static final String EXTRA_TAB = "tab";
    public static final String EXTRA_QUIT = "extra.quit";
    public static final String SDPATH = "/sdcard/SnialData";

    protected int mCurrentTab = 0;
    protected int mLastTab = -1;

    //存放选项卡信息的列表
    protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    //viewpager adapter
    protected MyAdapter myAdapter = null;

    //viewpager
    protected NoScrollViewPager mPager;

    //选项卡控件
    protected TitleIndicator mIndicator;

    private LinearLayout editLL;
    private TextView chooseTV, deleteTV;
    private boolean isEditing = false;
    private boolean isChooseAll = false;

    public TitleIndicator getIndicator() {
        return mIndicator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitleText("我的缓存");

        initViews();

        //设置viewpager内部页面之间的间距
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin_width));
        //设置viewpager内部页面间距的drawable
        //mPager.setPageMarginDrawable(R.color.page_viewer_margin_color);

    }

    public class MyAdapter extends FragmentPagerAdapter {
        ArrayList<TabInfo> tabs = null;
        Context context = null;

        public MyAdapter(Context context, FragmentManager fm, ArrayList<TabInfo> tabs) {
            super(fm);
            this.tabs = tabs;
            this.context = context;
        }

        @Override
        public Fragment getItem(int pos) {
            Fragment fragment = null;
            if (tabs != null && pos < tabs.size()) {
                TabInfo tab = tabs.get(pos);
                if (tab == null)
                    return null;
                fragment = tab.createFragment();
            }
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            if (tabs != null && tabs.size() > 0)
                return tabs.size();
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabInfo tab = tabs.get(position);
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            tab.fragment = fragment;
            return fragment;
        }
    }


    @Override
    public void onDestroy() {
        mTabs.clear();
        mTabs = null;
        myAdapter.notifyDataSetChanged();
        myAdapter = null;
        mPager.setAdapter(null);
        mPager = null;
        mIndicator = null;

        super.onDestroy();
    }


    private final void initViews() {
        // 这里初始化界面
        mCurrentTab = supplyTabs(mTabs);
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentTab = intent.getIntExtra(EXTRA_TAB, mCurrentTab);
        }
        Log.d(TAG, "mTabs.size() == " + mTabs.size() + ", cur: " + mCurrentTab);
        myAdapter = new MyAdapter(this, DownloadActivity.this.getSupportFragmentManager(), mTabs);

        mPager = (NoScrollViewPager) findViewById(R.id.pager);
        mPager.setNoScroll(true);
        mPager.setAdapter(myAdapter);
        mPager.setOnPageChangeListener(this);
        mPager.setOffscreenPageLimit(mTabs.size());

        mIndicator = (TitleIndicator) findViewById(R.id.pagerindicator);
        mIndicator.init(mCurrentTab, mTabs, mPager);

        mPager.setCurrentItem(mCurrentTab);
        mLastTab = mCurrentTab;

        chooseTV = (TextView) findViewById(R.id.tv_choose);
        deleteTV = (TextView) findViewById(R.id.tv_delete);
        editLL = (LinearLayout) findViewById(R.id.editLL);

        setRightText("编辑");
        setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEditing) {

                    switch (mCurrentTab) {
                        case 0:
                            if (((DownloadedFragment) myAdapter.getItem(0)).adapter != null) {
                                DownloadedAdapter.CHECKBOS_VISIBLE = false;
                                ((DownloadedFragment) myAdapter.getItem(0)).adapter.notifyDataSetChanged();
                                editLL.setVisibility(View.INVISIBLE);
                                setRightText("编辑");
                                isEditing = false;
                            }
                            break;
                        case 1:
                            if (((DownloadingFragment) myAdapter.getItem(1)).adapter != null) {
                                DownloadingAdapter.CHECKBOS_VISIBLE = false;
                                ((DownloadingFragment) myAdapter.getItem(1)).adapter.notifyDataSetChanged();
                                editLL.setVisibility(View.INVISIBLE);
                                setRightText("编辑");
                                isEditing = false;
                            }
                            break;
                    }
                } else {
                    switch (mCurrentTab) {
                        case 0:
                            if (((DownloadedFragment) myAdapter.getItem(0)).adapter != null) {
                                DownloadedAdapter.CHECKBOS_VISIBLE = true;
                                ((DownloadedFragment) myAdapter.getItem(0)).adapter.notifyDataSetChanged();
                                setRightText("取消");
                                editLL.setVisibility(View.VISIBLE);
                                isEditing = true;
                            }
                            break;
                        case 1:
                            if (((DownloadingFragment) myAdapter.getItem(1)).adapter != null) {
                                DownloadingAdapter.CHECKBOS_VISIBLE = true;
                                ((DownloadingFragment) myAdapter.getItem(1)).adapter.notifyDataSetChanged();
                                setRightText("取消");
                                editLL.setVisibility(View.VISIBLE);
                                isEditing = true;
                            }
                            break;
                    }
                }
            }
        });

        chooseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChooseAll) {
                    isChooseAll = true;
                    chooseTV.setText("取消");
                } else {
                    isChooseAll = false;
                    chooseTV.setText("全选");
                }
                // 分别调用Fragment中方法
                switch (mCurrentTab) {
                    case 0:
                        ((DownloadedFragment) myAdapter.getItem(0)).chooseOrDeChoose();
                        break;
                    case 1:
                        ((DownloadingFragment) myAdapter.getItem(1)).chooseOrDeChoose();
                        break;
                }

            }
        });

        deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DownloadActivity.this)
                        .setTitle("请确认")
                        .setMessage("确定要删除所选课程？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (mCurrentTab) {
                                    case 0:
                                        ((DownloadedFragment) myAdapter.getItem(0)).deleteCourse();
                                        break;
                                    case 1:
                                        ((DownloadingFragment) myAdapter.getItem(1)).deleteCourse();
                                        break;
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });

    }

    /**
     * 添加一个选项卡
     *
     * @param tab
     */
    public void addTabInfo(TabInfo tab) {
        mTabs.add(tab);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * 从列表添加选项卡
     *
     * @param tabs
     */
    public void addTabInfos(ArrayList<TabInfo> tabs) {
        mTabs.addAll(tabs);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mIndicator.onScrolled((mPager.getWidth() + mPager.getPageMargin()) * position + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        mIndicator.onSwitched(position);
        mCurrentTab = position;
        // 切换tab时，取消之前的编辑状态
        if (isEditing) {
            if (((DownloadedFragment) myAdapter.getItem(0)).adapter != null) {
                DownloadedAdapter.CHECKBOS_VISIBLE = false;
                ((DownloadedFragment) myAdapter.getItem(0)).adapter.notifyDataSetChanged();
            }
            if (((DownloadingFragment) myAdapter.getItem(1)).adapter != null) {
                DownloadingAdapter.CHECKBOS_VISIBLE = false;
                ((DownloadingFragment) myAdapter.getItem(1)).adapter.notifyDataSetChanged();
            }
            editLL.setVisibility(View.INVISIBLE);
            setRightText("编辑");
            isEditing = false;
            isChooseAll = false;
            chooseTV.setText("全选");
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mLastTab = mCurrentTab;
        }
    }

    protected TabInfo getFragmentById(int tabId) {
        if (mTabs == null) return null;
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            TabInfo tab = mTabs.get(index);
            if (tab.getId() == tabId) {
                return tab;
            }
        }
        return null;
    }

    /**
     * 跳转到任意选项卡
     *
     * @param tabId 选项卡下标
     */
    public void navigate(int tabId) {
        for (int index = 0, count = mTabs.size(); index < count; index++) {
            if (mTabs.get(index).getId() == tabId) {
                mPager.setCurrentItem(index);
            }
        }
    }


    /**
     * 在这里提供要显示的选项卡数据
     */
    private int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(0, "已缓存",
                DownloadedFragment.class));
        tabs.add(new TabInfo(1, "正在缓存",
                DownloadingFragment.class));
        return 1;
    }

    /**
     * 单个选项卡类，每个选项卡包含名字，图标以及提示（可选，默认不显示）
     */
    public static class TabInfo implements Parcelable {

        private int id;
        private int icon;
        private String name = null;
        public boolean hasTips = false;
        public Fragment fragment = null;
        public boolean notifyChange = false;
        @SuppressWarnings("rawtypes")
        public Class fragmentClass = null;

        @SuppressWarnings("rawtypes")
        public TabInfo(int id, String name, Class clazz) {
            this(id, name, 0, clazz);
        }

        @SuppressWarnings("rawtypes")
        public TabInfo(int id, String name, boolean hasTips, Class clazz) {
            this(id, name, 0, clazz);
            this.hasTips = hasTips;
        }

        @SuppressWarnings("rawtypes")
        public TabInfo(int id, String name, int iconid, Class clazz) {
            super();

            this.name = name;
            this.id = id;
            icon = iconid;
            fragmentClass = clazz;
        }

        public TabInfo(Parcel p) {
            this.id = p.readInt();
            this.name = p.readString();
            this.icon = p.readInt();
            this.notifyChange = p.readInt() == 1;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setIcon(int iconid) {
            icon = iconid;
        }

        public int getIcon() {
            return icon;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Fragment createFragment() {
            if (fragment == null) {
                Constructor constructor;
                try {
                    constructor = fragmentClass.getConstructor(new Class[0]);
                    fragment = (Fragment) constructor.newInstance(new Object[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return fragment;
        }

        public static final Parcelable.Creator<TabInfo> CREATOR = new Parcelable.Creator<TabInfo>() {
            public TabInfo createFromParcel(Parcel p) {
                return new TabInfo(p);
            }

            public TabInfo[] newArray(int size) {
                return new TabInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel p, int flags) {
            p.writeInt(id);
            p.writeString(name);
            p.writeInt(icon);
            p.writeInt(notifyChange ? 1 : 0);
        }

    }

}

