package com.snail.education.protocol;

import com.snail.education.protocol.result.ServiceError;

/**
 * Created by tianxiaopeng on 15-1-5.
 */
public interface SECallBack {
    public void success();

    public void failure(ServiceError error);
}

