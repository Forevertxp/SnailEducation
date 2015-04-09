package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SEOrgInfoResult;
import com.snail.education.protocol.result.SEOrgResult;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Administrator on 2015/3/26.
 */
public interface SEOrgService {
    /**
     * 服务机构列表
     *
     * @param cb
     */
    @GET("/api/orgList")
    public void fetchOrganization(@Query("cid") String cid,
                             Callback<SEOrgResult> cb);

    /**
     * 服务机构信息
     *
     * @param cb
     */
    @GET("/api/orgInfo")
    public void fetchOrgInfo(@Query("id") String id,
                                 Callback<SEOrgInfoResult> cb);

    /**
     * 服务机构简介
     *
     * @param cb
     */
    @GET("/api/orgInfoHtml/{id}")
    public void fetchOrgInfoHtml(@Path("id") String id,
                                     Callback<Response> cb);
}
