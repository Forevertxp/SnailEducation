package com.snail.education.ui.organization;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.MCOrgInfo;
import com.snail.education.protocol.service.SEOrgService;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrgInfoFragment extends Fragment {

    private TextView orgInfoTV;

    private static final String ORG_INFO = "org_info";

    public OrgInfoFragment() {
        // Required empty public constructor
    }

    public static OrgInfoFragment newInstance(MCOrgInfo arg) {
        OrgInfoFragment fragment = new OrgInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ORG_INFO, arg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_org_info, container, false);
        orgInfoTV = (TextView) rootView.findViewById(R.id.orgInfo);
        initTeacherBasicInfo();
        return rootView;
    }

    private void initTeacherBasicInfo() {
        MCOrgInfo orgInfo = (MCOrgInfo)getArguments().getSerializable(ORG_INFO);
        if (orgInfo!=null){
            orgInfoTV.setText("\u3000\u3000"+ orgInfo.profile);
        }
    }
}
