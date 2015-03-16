package com.snail.education.common;

import android.content.Context;
import android.util.AttributeSet;


/**
 * Created by tianxiaopeng on 15-1-5.
 */
public class SEAutoSlidingPagerView extends AutoScrollViewPager {


    private static final double VIEW_ASPECT_RATIO = 640.0 / 320.0;

    private SEViewAspectRatioMeasurer viewMeasurer = new SEViewAspectRatioMeasurer(VIEW_ASPECT_RATIO);

    public SEAutoSlidingPagerView(Context paramContext) {
        super(paramContext);
    }

    public SEAutoSlidingPagerView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewMeasurer.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewMeasurer.getMeasuredWidth(), viewMeasurer.getMeasuredHeight());
    }
}

