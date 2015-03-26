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
                                     Callback<UploadResult> cb);
}
