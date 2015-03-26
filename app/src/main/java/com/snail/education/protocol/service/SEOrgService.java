package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SEOrgResult;

import retrofit.Callback;
import retrofit.http.GET;
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
}
