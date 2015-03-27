package com.snail.photo.upload;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by Administrator on 2015/3/26.
 */
public interface UploadService {

    @Multipart
    @POST("/api/test")
    public void deployMsg(@Part("uid") String uid,
                                     @Part("msg") String msg,
                                     @Part("photo0") TypedFile photo0,
                                     @Part("photo1") TypedFile photo1,
                                     @Part("photo2") TypedFile photo2,
                                     @Part("photo3") TypedFile photo3,
                                     @Part("photo4") TypedFile photo4,
                                     @Part("photo5") TypedFile photo5,
                                     @Part("photo6") TypedFile photo6,
                                     @Part("photo7") TypedFile photo7,
                                     @Part("photo8") TypedFile photo8,
                                     Callback<UploadResult> cb);
}
