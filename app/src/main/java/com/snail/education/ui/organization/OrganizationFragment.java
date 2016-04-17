package com.snail.education.ui.organization;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.snail.education.R;
import com.snail.education.protocol.manager.MCOrgManager;
import com.snail.education.protocol.model.MCOrgInfo;
import com.snail.education.protocol.result.MCOrgListResult;
import com.snail.education.ui.BaseSearchActivity;
import com.snail.pulltorefresh.PullToRefreshBase;
import com.snail.pulltorefresh.PullToRefreshListView;
import com.snail.svprogresshud.SVProgressHUD;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrganizationFragment extends Fragment {


    private PullToRefreshListView orgListView;
    private OrgInfoAdapter adapter;
    private ArrayList<MCOrgInfo> orgArrayList;

    public final static String ORG_KEY = "cn.swiftacademy.organization";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mMainView = inflater.inflate(R.layout.fragment_organization, container, false);

        setupNavBar(mMainView);

        orgListView = (PullToRefreshListView) mMainView.findViewById(R.id.orgListView);
        initData();
        orgListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });

        orgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), OrganizationInfoActivity.class);
                MCOrgInfo orgInfo = orgArrayList.get((int) id);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ORG_KEY, orgInfo);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return mMainView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(BaseSearchActivity.MENU_SEARCH).setVisible(false);
    }

    private void setupNavBar(View rootView) {
        RelativeLayout titleRL = (RelativeLayout) rootView.findViewById(R.id.titleRL);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titleRL.getLayoutParams();
        lp.height = getNavigationBarHeight();
        titleRL.setLayoutParams(lp);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initData() {
        MCOrgManager orgManager = MCOrgManager.getInstance();
        orgManager.fetchOrganizationList(new Callback<MCOrgListResult>() {
            @Override
            public void success(MCOrgListResult result, Response response) {
                if (!result.apicode.equals("10000")) {
                    SVProgressHUD.showInViewWithoutIndicator(getActivity(), result.message, 2.0f);
                } else {
                    orgArrayList = result.orgList;
                    adapter = new OrgInfoAdapter(getActivity(), orgArrayList);
                    orgListView.setAdapter(adapter);
                }
                orgListView.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                orgListView.onRefreshComplete();
            }
        });
    }

    private int getNavigationBarHeight() {
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}