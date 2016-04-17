package com.snail.education.protocol.service;

import com.snail.education.protocol.result.MCBannerResult;
import com.snail.education.protocol.result.MCCollectionResult;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.protocol.result.MCCourSectionResult;
import com.snail.education.protocol.result.MCCourseListResult;
import com.snail.education.protocol.result.MCKeywordResult;
import com.snail.education.protocol.result.MCSearchResult;
import com.snail.education.protocol.result.MCVideoResult;
import com.snail.education.protocol.result.SEAddCartResult;
import com.snail.education.protocol.result.SECartResult;
import com.snail.education.protocol.result.SECourseCateResult;
import com.snail.education.protocol.result.SECourseDetailResult;
import com.snail.education.protocol.result.SECourseResult;
import com.snail.education.protocol.result.SEOrderResult;
import com.snail.education.protocol.result.SEWXPayInfoResult;
import com.snail.education.protocol.result.VideoCollectionResult;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * Created by tianxiaopeng on 15-1-1.
 */
public interface SECourseService {

    /**
     * 课程类别列表
     *
     * @param cb
     */
    @GET("/api/classCageList")
    public void fetchCourse(Callback<SECourseCateResult> cb);


    /**
     * 具体课程列表
     *
     * @param cb
     */
    @GET("/api/classList")
    public void fetchCourseList(@Query("free") String free,
                                @Query("tid") int tid,
                                @Query("oid") int oid,
                                @Query("cid") int cid,
                                Callback<SECourseResult> cb);

    /**
     * 我的学习列表
     *
     * @param cb
     */
    @GET("/api/myLearnList")
    public void fetchMyCourseList(@Query("sta") int sta,
                                  @Query("uid") int uid,
                                  @Query("page") int page,
                                  @Query("limit") int limit,
                                  Callback<SECourseResult> cb);

    /**
     * 具体课程信息
     *
     * @param cb
     */
    @GET("/api/classInfo")
    public void getCourseDetail(@Query("id") int id,
                                @Query("uid") int uid,
                                Callback<SECourseDetailResult> cb);

    /**
     * 添加到购物车
     *
     * @param cb
     */
    @GET("/api/classToCart")
    public void addToCart(@Query("id") String id,
                          @Query("uid") String uid,
                          Callback<SEAddCartResult> cb);

    /**
     * 购物车列表
     *
     * @param cb
     */
    @GET("/api/cartList")
    public void fetchCartList(@Query("uid") int uid,
                              Callback<SECartResult> cb);

    /**
     * 购买课程
     *
     * @param cb
     */
    @GET("/api/classBuy")
    public void buyCourse(@Query("id") String id,
                          @Query("uid") String uid,
                          Callback<SEAddCartResult> cb);

    /**
     * 创建订单
     *
     * @param cb
     */
    @Multipart
    @POST("/api/orderCreate")
    public void createOrder(@Part("uid") int uid,
                            Callback<SEOrderResult> cb);


    /**
     * 获取微信支付的相关信息
     *
     * @param cb
     */
    @GET("/api/apppay")
    public void fetchWXPayInfo(@Query("id") String id,
                               Callback<SEWXPayInfoResult> cb);

    /**
     * 首页轮播图
     *
     * @param cb
     */
    @GET("/swift/cour/getHomeVideoList")
    public void fetchHomeBanner(Callback<MCBannerResult> cb);

    /**
     * 知识点对应的视频
     *
     * @param cb
     */
    @GET("/swift/cour/getVideoByPoi")
    public void fetchVideoInfo(@Query("pointId") String pointId,
                               Callback<MCVideoResult> cb);


    /**
     * 首页课程列表
     *
     * @param cb
     */
    @GET("/swift/cour/getCourList")
    public void fetchHomeCourseList(@Query("pid") String pid,
                                    Callback<MCCourseListResult> cb);

    /**
     * 课程子分类
     *
     * @param cb
     */
    @GET("/swift/cour/getCourListSpe")
    public void fetchCourseSection(@Query("pid") String pid,
                                   Callback<MCCourSectionResult> cb);


    /**
     * 获取搜索关键词
     *
     * @param cb
     */
    @GET("/swift/cour/getKeywordList")
    public void fetchKeywordList(Callback<MCKeywordResult> cb);


    /**
     * 视频检索
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/cour/getVideoByKeyWord")
    public void searchVideoList(@Part("keyword") String keyword,
                                Callback<MCSearchResult> cb);

    /**
     * 获取收藏状态
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/collection/getColletionState")
    public void getColletionState(@Part("userId") String userId,
                                  @Part("collectionId") String collectionId,
                                  @Part("type") String type,
                                  Callback<VideoCollectionResult> cb);

    /**
     * 添加到收藏
     *
     * @param state 收藏／取消
     * @param type  文件夹／课程
     * @param cb
     */
    @Multipart
    @POST("/swift/collection/collectionVideo")
    public void collectVideo(@Part("collectionId") String collectionId,
                             @Part("state") String state,
                             @Part("userId") String userId,
                             @Part("type") String type,
                             Callback<MCCommonResult> cb);

    /**
     * 移除所有收藏
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/collection/removeColletionByUserId")
    public void removeAllCollection(@Part("userId") String userId,
                                    Callback<MCCommonResult> cb);

    /**
     * 更新观看数量
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/cour/updateCourseInfo")
    public void updateCourseInfo(@Part("courseId") String courseId,
                                 Callback<MCCommonResult> cb);

    /**
     * 收藏列表
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/collection/getColletionByUserId")
    public void fetchCollectionVideoList(@Part("userId") String userId,
                                         Callback<MCCollectionResult> cb);
}
