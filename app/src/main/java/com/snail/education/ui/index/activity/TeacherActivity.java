package com.snail.education.ui.index.activity;

import android.content.Context;
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
import com.snail.education.protocol.model.SETeacher;
import com.snail.education.protocol.model.SETeacherCate;
import com.snail.education.protocol.result.SETeacherResult;
import com.snail.education.protocol.service.SETeacherService;
import com.snail.education.ui.activity.SEBaseActivity;
import com.snail.education.ui.index.adapter.TeacherAdapter;
import com.snail.sortlistview.CharacterParser;
import com.snail.sortlistview.ClearEditText;
import com.snail.sortlistview.PinyinComparator;
import com.snail.sortlistview.SideBar;
import com.snail.sortlistview.SortAdapter;
import com.snail.sortlistview.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TeacherActivity extends SEBaseActivity {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private TeacherAdapter adapter;
    private ClearEditText mClearEditText;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        setTitleText("名师推荐");
        initViews();
        fetchTeacherList();
    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

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
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sortListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mClearEditText.getWindowToken(), 0);
                return false;
            }
        });
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
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
        SETeacherService teacherService = SERestManager.getInstance().create(SETeacherService.class);
        teacherService.fetchTeacher(new Callback<SETeacherResult>() {
            @Override
            public void success(SETeacherResult result, Response response) {
                if (result.state) {
                    ArrayList<SETeacherCate> cateList = result.data;
                    ArrayList<SETeacher> teacherArrayList = new ArrayList<SETeacher>();
                    for (SETeacherCate cate : cateList) {
                        ArrayList<SETeacher> subList = cate.getData();
                        teacherArrayList.addAll(subList);
                    }
                    SourceDateList = filledData(teacherArrayList);

                    // 根据a-z进行排序源数据
                    Collections.sort(SourceDateList, pinyinComparator);
                    adapter = new TeacherAdapter(TeacherActivity.this, SourceDateList);
                    sortListView.setAdapter(adapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(TeacherActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 为ListView填充数据
     *
     * @param teacherArrayList
     * @return
     */
    private List<SortModel> filledData(ArrayList<SETeacher> teacherArrayList) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < teacherArrayList.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(teacherArrayList.get(i).getName());
            sortModel.setOname(teacherArrayList.get(i).getOname());
            sortModel.setJob(teacherArrayList.get(i).getJob());
            sortModel.setIcon(teacherArrayList.get(i).getIcon());
            sortModel.setCount(teacherArrayList.get(i).getClasscount());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(teacherArrayList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

}
