package com.snail.education.common;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.ui.search.SearchResultActivity;
import com.snail.svprogresshud.SVProgressHUD;

/**
 * SearchText
 * 搜索编辑框
 *
 * @author tianxiaopeng
 */
public class SESearchBox extends LinearLayout {
    //两个按钮
    private ImageView ib_searchtext_delete;
    private EditText et_searchtext_search;

    public SESearchBox(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        //从一个打气筒获得一个view
        View view = LayoutInflater.from(context).inflate(R.layout.view_search, null);
        //把获得的view加载到这个控件中
        addView(view);
        //把两个按钮从布局文件中找到
        ib_searchtext_delete = (ImageView) view.findViewById(R.id.ib_searchtext_delete);
        et_searchtext_search = (EditText) view.findViewById(R.id.et_searchtext_search);
        //给删除按钮添加点击事件
        ib_searchtext_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                et_searchtext_search.setText("");
            }
        });
        //给编辑框添加文本改变事件
        et_searchtext_search.addTextChangedListener(new MyTextWatcher());
        et_searchtext_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(et_searchtext_search.getText().toString())) {
                        SVProgressHUD.showInViewWithoutIndicator(context, "请输入关键字", 2.0f);
                        return true;
                    }
                    Intent intent = new Intent(context, SearchResultActivity.class);
                    intent.putExtra("keywords", et_searchtext_search.getText().toString());
                    context.startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    public EditText getEditTextView() {
        return et_searchtext_search;
    }

    //文本观察者
    private class MyTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        //当文本改变时候的操作
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            //如果编辑框中文本的长度大于0就显示删除按钮否则不显示
            if (s.length() > 0) {
                ib_searchtext_delete.setVisibility(View.VISIBLE);
            } else {
                ib_searchtext_delete.setVisibility(View.GONE);
            }
        }

    }
}