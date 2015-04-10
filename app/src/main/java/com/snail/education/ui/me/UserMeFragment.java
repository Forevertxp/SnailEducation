package com.snail.education.ui.me;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.manager.SEUserManager;
import com.snail.education.protocol.model.SEUser;
import com.snail.education.protocol.model.SEUserInfo;
import com.snail.education.protocol.result.ServiceError;
import com.snail.education.ui.course.pay.CoursePayActivity;
import com.snail.education.ui.me.activity.DownloadActivity;
import com.snail.education.ui.me.activity.EvaluationActivity;
import com.snail.education.ui.me.activity.UserCourseActivity;
import com.snail.education.ui.me.activity.UserUpdateActivity;


public class UserMeFragment extends Fragment implements View.OnClickListener {
    private final static int EDIT_USER_INFO = 0x123123;

    private UserHeaderView headerView;
    private TextView orderText, saleText, processText, collectText;
    private Button logoutBtn;
    private RelativeLayout saleRL,processRL, downloadRL, profileRL, evaluationRL;

    public UserMeFragment() {
        // Required empty public constructor
    }

    public static UserMeFragment newInstance() {
        UserMeFragment fragment = new UserMeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headerView = new UserHeaderView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        FrameLayout userFrame = (FrameLayout) view.findViewById(R.id.userLayout);
        orderText = (TextView) view.findViewById(R.id.orderText);
        saleText = (TextView) view.findViewById(R.id.saleText);
        processText = (TextView) view.findViewById(R.id.processText);
        collectText = (TextView) view.findViewById(R.id.collectText);
        saleRL = (RelativeLayout) view.findViewById(R.id.saleRL);
        processRL = (RelativeLayout) view.findViewById(R.id.processRL);
        downloadRL = (RelativeLayout) view.findViewById(R.id.downloadRL);
        profileRL = (RelativeLayout) view.findViewById(R.id.profileRL);
        evaluationRL = (RelativeLayout) view.findViewById(R.id.evaluationRL);
        userFrame.addView(headerView);
        logoutBtn = (Button) view.findViewById(R.id.logoutBtn);
        saleRL.setOnClickListener(this);
        processRL.setOnClickListener(this);
        downloadRL.setOnClickListener(this);
        evaluationRL.setOnClickListener(this);
        profileRL.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saleRL:
                onUserSale();
                break;
            case R.id.processRL:
                onUserCourse();
                break;
            case R.id.downloadRL:
                onUserDownload();
                break;
            case R.id.profileRL:
                onEditUserInfo();
                break;
            case R.id.evaluationRL:
                onEvaluation();
                break;
            case R.id.logoutBtn:
                logout();
                break;
        }
    }

    private void logout() {
        new AlertDialog.Builder(getActivity())
                .setTitle("请确认")
                .setMessage("确认退出当前用户吗？")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SEUserManager.getInstance().logout();
                        Intent intent = new Intent();
                        intent.setAction("com.snail.user.logout");
                        getActivity().sendBroadcast(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            reloadFromUser();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reloadFromUser();
        }
    }

    public void reloadFromUser() {
        if (!SEAuthManager.getInstance().isAuthenticated()) {
            headerView.setUser(null);
            return;
        }
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        headerView.setUser(user);
        SEUserManager.getInstance().refreshCurrentUserInfo(new SECallBack() {
            @Override
            public void success() {
                SEUserInfo userInfo = SEUserManager.getInstance().getUserInfo();
                orderText.setText(userInfo.getOrder());
                saleText.setText(userInfo.getCart());
                processText.setText(userInfo.getLearn());
                collectText.setText(userInfo.getCollect());
            }

            @Override
            public void failure(ServiceError error) {

            }
        });
    }

    private void onUserSale() {
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        if (user != null) {
            Intent intent = new Intent(getActivity(), CoursePayActivity.class);
            startActivity(intent);
        }
    }

    private void onUserCourse() {
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        if (user != null) {
            Intent intent = new Intent(getActivity(), UserCourseActivity.class);
            startActivity(intent);
        }
    }


    private void onEvaluation() {
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        if (user != null) {
            Intent intent = new Intent(getActivity(), EvaluationActivity.class);
            startActivity(intent);
        }
    }

    protected void onEditUserInfo() {
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        if (user != null) {
            Intent intent = new Intent(getActivity(), UserUpdateActivity.class);
            startActivityForResult(intent, EDIT_USER_INFO);
        }
    }

    private void onUserDownload() {
        SEUser user = SEAuthManager.getInstance().getAccessUser();
        if (user != null) {
            Intent intent = new Intent(getActivity(), DownloadActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_USER_INFO && resultCode == Activity.RESULT_OK) {
            reloadFromUser();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

