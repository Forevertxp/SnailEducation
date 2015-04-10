package com.snail.education.protocol.service;

import com.snail.education.protocol.model.SEStory;
import com.snail.education.protocol.result.SEStoryPraiseResult;
import com.snail.education.protocol.result.SEStoryRepResult;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by tianxiaopeng on 15-1-17.
 */
public interface SEStoryService {

    /**
     * 查询蜗牛故事信息
     *
     * @param uid
     * @param cb
     */
    @FormUrlEncoded
    @POST("/api/storyAjax")
    public void fetchStory(@Field("page") int page,
                           @Field("limit") int limit,
                           @Field("uid") int uid,
                           Callback<ArrayList<SEStory>> cb);

    /**
     * 发表故事
     *
     * @param name
     * @param cb
     */
    @Multipart
    @POST("/api/test")
    public void deployStory(@Part("msg") int msg,
                            @Part("uid") int uid,
                            @Part("name") TypedFile name,
                            Callback<ArrayList<SEStory>> cb);

    /**
     * 故事点赞
     *
     * @param cb
     */
    @FormUrlEncoded
    @POST("/api/sotryPraise")
    public void praiseStory(@Field("id") String id,
                            @Field("uid") String uid,
                            Callback<SEStoryPraiseResult> cb);


    /**
     * 故事点赞
     *
     * @param cb
     */
    @FormUrlEncoded
    @POST("/api/storyRep")
    public void repStory(@Field("id") String id,
                         @Field("uid") String uid,
                         @Field("toid") String toid,
                         @Field("msg") String msg,
                         Callback<SEStoryRepResult> cb);

}
