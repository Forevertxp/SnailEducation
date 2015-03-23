package com.snail.education.ui.index.activity;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.snail.education.R;
import com.snail.education.protocol.SECallBack;
import com.snail.education.protocol.manager.SEExamManager;
import com.snail.education.protocol.model.SERemind;
import com.snail.education.protocol.result.ServiceError;

/**
 * Created by Administrator on 2015/3/23.
 */
public class ExamHeaderView extends FrameLayout {

    private TextView countTextView;
    private ExamActivity examActivity;

    public ExamHeaderView(ExamActivity examActivity) {
        super(examActivity);
        construct(examActivity);
    }

    private void construct(ExamActivity examActivity) {
        this.examActivity = examActivity;
        LayoutInflater.from(examActivity).inflate(R.layout.view_exam_header, this, true);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        SEExamManager.getInstance().fetchExamTip(new SECallBack() {
            @Override
            public void success() {
                SERemind remindInfo = SEExamManager.getInstance().getRemindInfo();
                setupViews(remindInfo);
            }

            @Override
            public void failure(ServiceError error) {
            }
        });
    }

    private void setupViews(SERemind remindInfo) {
        countTextView = (TextView) findViewById(R.id.count);
        String n = remindInfo.getDiff();
        SpannableString ss = new SpannableString("还有" + n + "天");
        ss.setSpan(new ForegroundColorSpan(Color.RED), 2, 2 + n.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(2.0f), 2, 2 + n.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        countTextView.setText(ss);
    }
}

