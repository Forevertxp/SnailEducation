package com.snail.education.ui.index;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.common.SEAutoSlidingPagerView;
import com.snail.education.common.SESearchBox;
import com.snail.education.common.SETabBar;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SEIndexManager;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.protocol.model.SEIndexCount;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.MainFragment;
import com.snail.education.ui.index.activity.AuditionActivity;
import com.snail.education.ui.index.activity.ExamActivity;
import com.snail.education.ui.index.activity.MsgActivity;
import com.snail.education.ui.index.activity.OrganizationActivity;
import com.snail.education.ui.index.activity.SignInActivity;
import com.snail.education.ui.index.activity.StudentActivity;
import com.snail.education.ui.index.activity.SubjectActivity;
import com.snail.education.ui.index.activity.TeacherActivity;
import com.snail.education.ui.course.RelativeCourseAdapter;

public class IndexFragment extends Fragment implements View.OnClickListener {

    private SEAutoSlidingPagerView autoSlidingPagerView;
    private ImageView rightImage;
    private ImageView listeningImage, courseImage, subjectImage, examImage;
    private ImageView teacherImage, organizationImage, studentImage;
    private TextView signinText, teacherText, organizationText, studentText;
    private ListView courseListView;
    private final SEIndexManager indexManager = SEIndexManager.getInstance();

    public IndexFragment() {
    }

    public static IndexFragment newInstance() {
        return new IndexFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_index, container, false);

        SESearchBox searchBox = (SESearchBox) view.findViewById(R.id.search_box);
        final EditText editText = searchBox.getEditTextView();

        signinText = (TextView) view.findViewById(R.id.left_index);
        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SEAuthManager.getInstance().getAccessUser() == null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("您尚未登陆")
                            .setMessage("登陆后才能签到，是否去登陆？")
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
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });

        autoSlidingPagerView = (SEAutoSlidingPagerView) view.findViewById(R.id.autoSlidePagerView);
        int height = getActivity().getResources().getDisplayMetrics().heightPixels;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (height * 0.3));
        autoSlidingPagerView.setLayoutParams(layoutParams);
        //获得焦点
        autoSlidingPagerView.setFocusable(true);
        autoSlidingPagerView.requestFocus();
        autoSlidingPagerView.setFocusableInTouchMode(true);
        initAdInfo();

        rightImage = (ImageView) view.findViewById(R.id.right_index);
        listeningImage = (ImageView) view.findViewById(R.id.listening);
        courseImage = (ImageView) view.findViewById(R.id.course);
        subjectImage = (ImageView) view.findViewById(R.id.subject);
        examImage = (ImageView) view.findViewById(R.id.exam);

        rightImage.setOnClickListener(this);
        listeningImage.setOnClickListener(this);
        courseImage.setOnClickListener(this);
        subjectImage.setOnClickListener(this);
        examImage.setOnClickListener(this);

        teacherText = (TextView) view.findViewById(R.id.teacherText);
        organizationText = (TextView) view.findViewById(R.id.organizationText);
        studentText = (TextView) view.findViewById(R.id.studentText);
        initCountInfo();

        teacherImage = (ImageView) view.findViewById(R.id.teacherImage);
        organizationImage = (ImageView) view.findViewById(R.id.organizationImage);
        studentImage = (ImageView) view.findViewById(R.id.studentImage);

        teacherImage.setOnClickListener(this);
        organizationImage.setOnClickListener(this);
        studentImage.setOnClickListener(this);

        courseListView = (ListView) view.findViewById(R.id.indexCourse);
        initCourseList();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                editText.clearFocus();
                return false;
            }
        });
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (autoSlidingPagerView != null) {
            autoSlidingPagerView.setFocusable(true);
            autoSlidingPagerView.requestFocus();
            autoSlidingPagerView.setFocusableInTouchMode(true);
        }
    }

    /**
     * 加载名师、机构、学员数量信息
     */
    private void initCountInfo() {
        indexManager.fetchIndexCount(new SECallBack() {
            @Override
            public void success() {
                SEIndexCount countInfo = indexManager.getCountInfo();
                teacherText.setText(countInfo.getTeacher());
                organizationText.setText(countInfo.getService());
                studentText.setText(countInfo.getStudent());
            }

            @Override
            public void failure(ServiceError error) {

            }
        });
    }

    /**
     * 加载广告信息
     */
    private void initAdInfo() {
        indexManager.fetchAdInfo(1, new SECallBack() {
            @Override
            public void success() {
                autoSlidingPagerView.setAdapter(new ImagePagerAdapter(getActivity(), indexManager.getAdList()));
                autoSlidingPagerView.setOnPageChangeListener(new MyOnPageChangeListener());
                autoSlidingPagerView.setInterval(4000);
                autoSlidingPagerView.setScrollDurationFactor(2.0);

                autoSlidingPagerView.startAutoScroll();
            }

            @Override
            public void failure(ServiceError error) {

            }
        });
    }

    /**
     * 加载首页课程信息
     */
    private void initCourseList() {
        int limit = 5;
        indexManager.fetchIndexCourse(limit, new SECallBack() {
            @Override
            public void success() {
                RelativeCourseAdapter adapter = new RelativeCourseAdapter(getActivity(), indexManager.getCourseList());
                courseListView.setAdapter(adapter);
                setListViewHeightBasedOnChildren(courseListView);
            }

            @Override
            public void failure(ServiceError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_index:
                Intent msgIntent = new Intent(getActivity(), MsgActivity.class);
                startActivity(msgIntent);
                break;
            case R.id.listening:
                Intent listeningIntent = new Intent(getActivity(), AuditionActivity.class);
                startActivity(listeningIntent);
                break;
            case R.id.course:
                MainFragment.newInstance().switchToPage(2);
                break;
            case R.id.subject:
                Intent subjectIntent = new Intent(getActivity(), SubjectActivity.class);
                startActivity(subjectIntent);
                break;
            case R.id.exam:
                Intent examIntent = new Intent(getActivity(), ExamActivity.class);
                startActivity(examIntent);
                break;
            case R.id.teacherImage:
                Intent teacherIntent = new Intent(getActivity(), TeacherActivity.class);
                startActivity(teacherIntent);
                break;
            case R.id.organizationImage:
                Intent organizationIntent = new Intent(getActivity(), OrganizationActivity.class);
                startActivity(organizationIntent);
                break;
            case R.id.studentImage:
                Intent studentIntent = new Intent(getActivity(), StudentActivity.class);
                startActivity(studentIntent);
                break;
        }

    }


    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /**
     * 根据listview内容设置其高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
