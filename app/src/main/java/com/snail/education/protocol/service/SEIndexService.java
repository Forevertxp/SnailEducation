package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SEAdResult;
import com.snail.education.protocol.result.SECourseResult;
import com.snail.education.protocol.result.SEIndexCountResult;
import com.snail.education.protocol.result.SESearchResult;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by tianxiaopeng on 15-1-20.
 */
public interface SEIndexService {

    /**
     * 查询搜索
     *
     * @param cb
     */
    @GET("/api/search")
    public void search(@Query("opt") String opt,
                       @Query("key") String key,
                       @Query("page") int page,
                       @Query("limit") int limit,
                       Callback<SESearchResult> cb);

    /**
     * 查询首页 名师、机构、学员数量
     *
     * @param cb
     */
    @GET("/api/indexCount")
    public void fetchIndexCount(
            Callback<SEIndexCountResult> cb);

    /**
     * 广告接口
     *
     * @param type 广告类型 1 首页 2 MBA咨询 3 蜗牛课程
     * @param cb
     */
    @GET("/api/ad")
    public void fetchAdInfo(@Query("type") int type,
                            Callback<SEAdResult> cb);

    /**
     * 首页课程列表
     *
     * @param limit
     * @param cb
     */
    @GET("/api/indexTopClass")
    public void fetchIndexCourse(@Query("limit") int limit,
                                 Callback<SECourseResult> cb);
}
