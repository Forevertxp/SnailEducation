package com.snail.education.ui.index.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.snail.education.R;
import com.snail.education.protocol.manager.SERestManager;
import com.snail.education.protocol.model.SEOrg;
import com.snail.education.protocol.model.SEOrgCate;
import com.snail.education.protocol.result.SEOrgResult;
import com.snail.education.protocol.service.SEOrgService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.index.adapter.OrganizationAdapter;
import com.snail.education.ui.organization.OrganizationInfoActivity;
import com.snail.sortlistview.CharacterParser;
import com.snail.sortlistview.ClearEditText;
import com.snail.sortlistview.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OrganizationActivity extends SEBaseActivity {
    private ListView orgListView;
    private SideBar sideBar;
    private TextView dialog;
    private OrganizationAdapter adapter;
    private ClearEditText mClearEditText;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private ArrayList<SEOrg> orgArrayList = new ArrayList<SEOrg>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);
        setTitleText("服务机构");
        initViews();
        fetchTeacherList();
    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    orgListView.setSelection(position);
                }

            }
        });

        orgListView = (ListView) findViewById(R.id.orgListView);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        orgListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mClearEditText.getWindowToken(), 0);
                return false;
            }
        });
        orgListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Intent intent = new Intent(OrganizationActivity.this, OrganizationInfoActivity.class);
                intent.putExtra("id", ((SEOrg) adapter.getItem(position)).getId());
                startActivity(intent);
            }
        });

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fetchTeacherList() {
        SEOrgService orgService = SERestManager.getInstance().create(SEOrgService.class);
        orgService.fetchOrganization("", new Callback<SEOrgResult>() {
            @Override
            public void success(SEOrgResult result, Response response) {
                if (result.state) {
                    ArrayList<SEOrgCate> cateList = result.data;
                    for (SEOrgCate cate : cateList) {
                        ArrayList<SEOrg> subList = cate.getData();
                        for (SEOrg org : subList) {
                            //汉字转换成拼音
                            String pinyin = characterParser.getSelling(org.getName());
                            String sortString = pinyin.substring(0, 1).toUpperCase();

                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                org.setSortLetters(sortString.toUpperCase());
                            } else {
                                org.setSortLetters("#");
                            }
                            orgArrayList.add(org);
                        }
                    }
                    // 根据a-z进行排序源数据
                    Collections.sort(orgArrayList, new PinyinComparator());
                    adapter = new OrganizationAdapter(OrganizationActivity.this, orgArrayList);
                    orgListView.setAdapter(adapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(OrganizationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SEOrg> filterDateList = new ArrayList<SEOrg>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = orgArrayList;
        } else {
            filterDateList.clear();
            for (SEOrg orgModel : orgArrayList) {
                String name = orgModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(orgModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, new PinyinComparator());
        if (adapter != null)
            adapter.updateListView(filterDateList);
    }

    protected class PinyinComparator implements Comparator<SEOrg> {

        public int compare(SEOrg o1, SEOrg o2) {
            if (o1.getSortLetters().equals("@")
                    || o2.getSortLetters().equals("#")) {
                return -1;
            } else if (o1.getSortLetters().equals("#")
                    || o2.getSortLetters().equals("@")) {
                return 1;
            } else {
                return o1.getSortLetters().compareTo(o2.getSortLetters());
            }
        }

    }
}