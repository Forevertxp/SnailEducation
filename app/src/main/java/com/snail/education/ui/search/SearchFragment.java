package com.snail.education.ui.search;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.snail.education.R;
import com.snail.education.common.IconCenterEditText;
import com.snail.education.protocol.manager.SECourseManager;
import com.snail.education.protocol.model.MCKeyword;
import com.snail.education.protocol.result.MCKeywordResult;
import com.snail.education.ui.BaseSearchActivity;
import com.snail.svprogresshud.SVProgressHUD;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchFragment extends Fragment {

    private IconCenterEditText icet_search;
    private ListView keywordListView;
    private SearchWordAdapter adapter;
    private ArrayList<MCKeyword> keywordArrayList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mMainView = inflater.inflate(R.layout.fragment_search, container, false);
        keywordListView = (ListView) mMainView.findViewById(R.id.keywordListView);
        setupNavBar(mMainView);

        icet_search = (IconCenterEditText) mMainView.findViewById(R.id.icet_search);

        // 实现TextWatcher监听即可
        icet_search.setOnSearchClickListener(new IconCenterEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                String keyword = icet_search.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    startSearch(keyword);
                }
            }
        });

        initData();

        return mMainView;
    }

    private void setupNavBar(View rootView) {
        RelativeLayout titleRL = (RelativeLayout) rootView.findViewById(R.id.titleRL);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titleRL.getLayoutParams();
        lp.height = getNavigationBarHeight();
        titleRL.setLayoutParams(lp);
    }

    private int getNavigationBarHeight() {
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    private void startSearch(String keyword) {
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(BaseSearchActivity.MENU_SEARCH).setVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (keywordArrayList == null)
            initData();
    }

    private void initData() {
        SECourseManager courseManager = SECourseManager.getInstance();
        courseManager.fetchKeyWordList(new Callback<MCKeywordResult>() {
            @Override
            public void success(MCKeywordResult result, Response response) {
                if (!result.apicode.equals("10000")) {
                    SVProgressHUD.showInViewWithoutIndicator(getActivity(), result.message, 2.0f);
                } else {
                    keywordArrayList = result.keywordList;
                    adapter = new SearchWordAdapter(getActivity(), keywordArrayList);
                    keywordListView.setAdapter(adapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}