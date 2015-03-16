package com.snail.education.protocol.service;

import com.snail.education.protocol.result.AccessTokenResult;
import com.snail.education.protocol.result.SEUserResult;
import com.snail.education.protocol.result.ServiceResult;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by tianxiaopeng on 15-1-6.
 */
public interface AuthService {


    @FormUrlEncoded
    @POST("/auth/sms")
    public void requestSMSAuthCode(@Field("cellphone") String cellphone,
                                   @Field("client_id") String clientID,
                                   Callback<ServiceResult> cb);

    /**
     * 手机加验证码登录  （咱未使用）
     * @param cellphone
     * @param clientID
     * @param verificationCode
     * @param cb
     */
    @FormUrlEncoded
    @POST("/auth/sms")
    public void verifySMSAuthCode(@Field("cellphone") String cellphone,
                                  @Field("client_id") String clientID,
                                  @Field("verification_code") String verificationCode,
                                  Callback<AccessTokenResult> cb);

    /**
     * 用户名密码登录
     * @param user
     * @param pass
     * @param cb
     */
    @FormUrlEncoded
    @POST("/api/user/login")
    public void authWithUsernamePassword(@Field("user") String user,
                                         @Field("pass") String pass,
                                         Callback<SEUserResult> cb);
}

