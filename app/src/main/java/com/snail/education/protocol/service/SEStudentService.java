package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SEStudentResult;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by tianxiaopeng on 15-2-2.
 */
public interface SEStudentService {
    /**
     * 学员风采列表
     *
     * @param cb
     */
    @GET("/api/studentList")
    public void fetchStudent(@Query("page") int page,
                             @Query("limit") int limit,
                             Callback<SEStudentResult> cb);

    /**
     * 学员详情
     *
     * @param cb
     */
    @GET("/api/studentInfo/{studentID}")
    public void fetchStudentInfo(@Path("studentID") int studentID,
                                 Callback<Response> cb);

}
