package com.snail.education.protocol.service;

import com.snail.education.protocol.result.MCCheckInfoResult;
import com.snail.education.protocol.result.MCCommonResult;
import com.snail.education.protocol.result.MCEnrollListResult;
import com.snail.education.protocol.result.MCOrgListResult;
import com.snail.education.protocol.result.MCTeacherResult;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by tianxiaopeng on 15/12/20.
 */
public interface MCOrgService {
    /**
     * 服务机构列表
     *
     * @param cb
     */
    @GET("/swift/organization/getAllOrganization")
    public void fetchOrganizationList(Callback<MCOrgListResult> cb);

    /**
     * 更新机构关注数量
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/organization/updateAttendCount")
    public void updateAttendCount(@Part("orgId") String orgId,
                                  Callback<MCCommonResult> cb);

    /**
     * 获取报名状态
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/organization/getInfoByUserPhoneAndOrg")
    public void getEnrollInfo(@Part("userPhone") String userPhone,
                              @Part("orgId") String orgId,
                              Callback<MCCheckInfoResult> cb);

    /**
     * 获取报名列表
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/organization/getListByPhone")
    public void fetchEnrollList(@Part("userPhone") String userPhone,
                              Callback<MCEnrollListResult> cb);

    /**
     * 删除报名信息
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/organization/cancelCheckIn")
    public void removeEnroll(@Part("checkId") String checkId,
                                Callback<MCCommonResult> cb);

    /**
     * 更新机构报名数量
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/organization/updateCheckInCount")
    public void updateEnrollCount(@Part("orgId") String orgId,
                                  @Part("type") String type,
                                  Callback<MCCommonResult> cb);

    /**
     * 机构教师列表
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/organization/getTeacherListByOrgId")
    public void fetchTeacherList(@Part("orgId") String orgId,
                                 Callback<MCTeacherResult> cb);

    /**
     * 报名
     *
     * @param cb
     */
    @Multipart
    @POST("/swift/organization/checkin")
    public void enroll(@Part("orgId") String orgId,
                       @Part("userPhone") String userPhone,
                       @Part("userLocal") String userLocal,
                       @Part("type") String type,
                       @Part("checkinTime") String checkinTime,
                       Callback<MCCommonResult> cb);
}
