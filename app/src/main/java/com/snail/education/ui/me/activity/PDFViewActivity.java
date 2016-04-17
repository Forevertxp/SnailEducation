package com.snail.education.ui.me.activity;

import android.os.Bundle;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.snail.education.R;
import com.snail.education.ui.activity.SEBaseActivity;

public class PDFViewActivity extends SEBaseActivity implements OnPageChangeListener {

    private PDFView pdfView;

    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        setTitleText(getIntent().getStringExtra("title"));

        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromAsset(getIntent().getStringExtra("fileName"))
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }
}
