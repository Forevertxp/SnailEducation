package com.snail.education.protocol;

/**
 * Created by tianxiaopeng on 15-1-5.
 */
public class SEDataRetriever {
    public void refresh(final SECallBack callback) {
        if (callback != null) {
            callback.success();
        }
    }

    public void loadMore(final SECallBack callback) {
        if (callback != null) {
            callback.success();
        }
    }
}