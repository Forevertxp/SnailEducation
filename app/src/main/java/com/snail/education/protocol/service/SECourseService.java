package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SEAddCartResult;
import com.snail.education.protocol.result.SECartResult;
import com.snail.education.protocol.result.SECourseCateResult;
import com.snail.education.protocol.result.SECourseDetailResult;
import com.snail.education.protocol.result.SECourseResult;
import com.snail.education.protocol.result.SEOrderResult;

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
}
