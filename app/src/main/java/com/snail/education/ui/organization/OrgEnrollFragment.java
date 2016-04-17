package com.snail.education.ui.organization;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.snail.education.R;
import com.snail.education.protocol.manager.MCOrgManager;
import com.snail.education.protocol.manager.SEAuthManager;
import com.snail.education.protocol.model.MCCheckInfo;
import com.snail.education.protocol.model.MCOrgInfo;
import com.snail.education.protocol.result.MCCheckInfoResult;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.svprogresshud.SVProgressHUD;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrgEnrollFragment extends Fragment {

    private static final String ORG_INFO = "org_info";

    private EditText phoneET;
    private TextView dateTV;
    private TimePickerView pvTime;
    private Button enrollBtn;
    private ImageView iv_choose_mc, iv_choose_cx;

    private MCOrgInfo orgInfo;
    private int enrollType = 1;

    public OrgEnrollFragment() {
        // Required empty public constructor
    }

    public static OrgEnrollFragment newInstance(MCOrgInfo arg) {
        OrgEnrollFragment fragment = new OrgEnrollFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ORG_INFO, arg);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_org_enroll, container, false);
        phoneET = (EditText) rootView.findViewById(R.id.et_phone);
        dateTV = (TextView) rootView.findViewById(R.id.tv_date);
        iv_choose_mc = (ImageView) rootView.findViewById(R.id.ic_choose_mc);
        iv_choose_cx = (ImageView) rootView.findViewById(R.id.ic_choose_cx);
        iv_choose_mc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_choose_mc.setBackgroundResource(R.drawable.ic_chosen);
                iv_choose_cx.setBackgroundResource(R.drawable.ic_unchosen);
                enrollType = 1;
            }
        });

        iv_choose_cx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_choose_cx.setBackgroundResource(R.drawable.ic_chosen);
                iv_choose_mc.setBackgroundResource(R.drawable.ic_unchosen);
                enrollType = 2;
            }
        });

        //时间选择器
        pvTime = new TimePickerView(getActivity(), TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
//        Calendar calendar = Calendar.getInstance();
//        pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                dateTV.setText(getTime(date));
            }
        });
        //弹出时间选择器
        dateTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pvTime.show();
            }
        });


        enrollBtn = (Button) rootView.findViewById(R.id.btn_enroll);
        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enroll();
            }
        });

        orgInfo = (MCOrgInfo) getArguments().getSerializable(ORG_INFO);
        setupEnrollState();
        return rootView;
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void enroll() {
        if (TextUtils.isEmpty(phoneET.getText().toString())) {
            SVProgressHUD.showInViewWithoutIndicator(getActivity(), "请填写手机号", 2.0f);
            return;
        }
        if (TextUtils.isEmpty(dateTV.getText().toString())) {
            SVProgressHUD.showInViewWithoutIndicator(getActivity(), "请选择日期", 2.0f);
            return;
        }
        MCOrgManager orgManager = MCOrgManager.getInstance();
        orgManager.enroll(orgInfo.id, phoneET.getText().toString(), "", enrollType + "", dateTV.getText().toString(), new Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {
                if (result.apicode.equals("10000")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("报名成功")
                            .setMessage("稍后客服与您取得联系")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    updateCheckInCount();
                    enrollBtn.setBackgroundResource(R.drawable.ic_enrolled);
                    enrollBtn.setEnabled(false);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void setupEnrollState() {
        SEAuthManager am = SEAuthManager.getInstance();
        if (am.isAuthenticated()) {
            phoneET.setText(am.getAccessUser().getPhone());
            MCOrgManager om = MCOrgManager.getInstance();
            om.getEnrollInfo(am.getAccessUser().getPhone(), orgInfo.id, new Callback<MCCheckInfoResult>() {
                @Override
                public void success(MCCheckInfoResult result, Response response) {
                    MCCheckInfo checkInfo = result.checkInfo;
                    if (checkInfo != null) {
                        enrollBtn.setBackgroundResource(R.drawable.ic_enrolled);
                        enrollBtn.setEnabled(false);
                    } else {
                        enrollBtn.setBackgroundResource(R.drawable.ic_enroll);
                        enrollBtn.setEnabled(true);
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }

    }

    // 更新机构报名人数
    private void updateCheckInCount() {
        MCOrgManager om = MCOrgManager.getInstance();
        om.updateEnrollCount(orgInfo.id, "1", new Callback<MCCommonResult>() {
            @Override
            public void success(MCCommonResult result, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
