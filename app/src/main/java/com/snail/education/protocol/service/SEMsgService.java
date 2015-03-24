package com.snail.education.protocol.service;

import com.snail.education.protocol.model.SEStory;
import com.snail.education.protocol.result.SEMsgResult;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by tianxiaopeng on 15-3-23.
 */
public interface SEMsgService {

    /**
     * 查询通知消息
     *
     * @param uid
     * @param cb
     */
    @GET("/api/getMsgListByUid")
    public void fetchMsg(@Query("uid") int uid,
                           @Query("page") int limit,
                           Callback<SEMsgResult> cb);

    /**
     * 新通知
     *
     * @param
     * @param cb
     */
    @FormUrlEncoded
    @POST("/api/msgCount")
    public void fetchMsgCount(Callback<ArrayList<SEStory>> cb);
}
