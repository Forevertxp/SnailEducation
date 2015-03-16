package com.snail.education.protocol.service;

import com.snail.education.protocol.result.SEUserInfoResult;
import com.snail.education.protocol.result.SEUserResult;

import java.io.File;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public interface SEUserService {


    /**
     * 注册
     *
     * @param sn user pass repass
     * @param cb
     */
    @Multipart
    @POST("/api/user/reg")
    public void regUser(@Part("sn") String sn,
                        @Part("user") String user,
                        @Part("pass") String pass,
                        @Part("repass") String repass,
                        Callback<SEUserResult> cb);

    /**
     * 更新
     *
     * @param uid name say
     * @param cb
     */
    @Multipart
    @POST("/api/user/update")
    public void updateUser(@Part("uid") String uid,
                           @Part("nickname") String nickname,
                           @Part("say") String say,
                           @Part("name") String name,
                           @Part("mail") String mail,
                           Callback<SEUserResult> cb);

    /**
     * 上传头像
     *
     * @param uid image
     * @param cb
     */
    @Multipart
    @POST("/api/userUpIcon")
    public void updateUserIcon(@Part("uid") String uid,
                               @Part("image") TypedFile image,
                               Callback<SEUserResult> cb);


    /**
     * 查询用户的 学习进度 订单 收藏等信息
     *
     * @param uid
     * @param cb
     */
    @GET("/api/user/userHouseCount")
    public void fetchInformation(@Query("uid") int uid,
                                 Callback<SEUserInfoResult> cb);

}
