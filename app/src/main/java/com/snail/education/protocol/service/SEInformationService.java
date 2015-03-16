package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SEInfoDetailResult;
import com.snail.education.protocol.result.SEInfoPraiseResult;
import com.snail.education.protocol.result.SEInformationResult;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by tianxiaopeng on 15-1-1.
 */
public interface SEInformationService {


    /**
     * 获取 资讯信息
     *
     * @param page
     * @param length
     * @param limit
     * @param cid    新闻类型id  6 MBA资讯
     * @param cb
     */
    @GET("/api/news/list")
    public void fetchInformation(@Query("page") int page,
                                 @Query("length") int length,
                                 @Query("limit") int limit,
                                 @Query("cid") int cid,
                                 Callback<SEInformationResult> cb);

    /**
     * 获取 资讯详情
     *
     * @param id 资讯id
     * @param cb
     */
    @GET("/api/newsHtml")
    public void fetchInformationDetail(@Query("id") int id,
                                       Callback<SEInfoDetailResult> cb);

    /**
     * 获取 资讯信息点赞数
     *
     * @param id 资讯id
     * @param cb
     */
    @GET("/api/newsZan")
    public void fetchInformationPraise(@Query("id") int id,
                                       Callback<SEInfoPraiseResult> cb);

}
