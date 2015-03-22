package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SETeacherResult;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by tianxiaopeng on 15-3-22.
 */
public interface SETeacherService {

    /**
     * 名师推荐列表
     *
     * @param cb
     */
    @GET("/api/commTeacherList")
    public void fetchTeacher(Callback<SETeacherResult> cb);
}
