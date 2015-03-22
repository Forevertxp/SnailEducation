package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SESubItemResult;
import com.snail.education.protocol.result.SESubjectResult;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by tianxiaopeng on 15-3-22.
 */
public interface SESubjectService {
    /**
     * 蜗牛题库列表
     *
     * @param cb
     */
    @GET("/api/qaBank/cage")
    public void fetchSubject(@Query("page") int page,
                             @Query("limit") int limit,
                             @Query("length") int length,
                             Callback<SESubjectResult> cb);

    /**
     * 题库子项列表
     *
     * @param cb
     */
    @GET("/api/qaBank/list")
    public void fetchSubItem(@Query("page") int page,
                             @Query("limit") int limit,
                             @Query("length") int length,
                             @Query("cid") String cid,
                             Callback<SESubItemResult> cb);
}
