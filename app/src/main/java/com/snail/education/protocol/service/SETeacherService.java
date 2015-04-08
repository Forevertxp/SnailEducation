package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SETeacherInfoResult;
import com.snail.education.protocol.result.SETeacherResult;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

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

    /**
     * 名师信息
     *
     * @param cb
     */
    @GET("/api/teacherInfo")
    public void fetchTeacherInfo(@Query("id") String id,
                                 Callback<SETeacherInfoResult> cb);

    /**
     * 名师简介
     *
     * @param cb
     */
    @GET("/api/teacherInfoHtml/{id}")
    public void fetchTeacherInfoHtml(@Path("id") String id,
                                     Callback<Response> cb);
}
