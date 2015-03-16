package com.snail.education.protocol.result;

import retrofit.RetrofitError;

/**
 * Created by tianxiaopeng on 15-1-1.
 */
public class ServiceError {


    public Integer code;
    public String message;

    public ServiceError(RetrofitError retrofitError) {
        code = -999;
        message = retrofitError.getMessage();
    }

    public ServiceError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageWithPrompt(String prompt) {
        if (message == null || message.isEmpty()) {
            return prompt;
        } else if (code == 1) {
            return prompt + "：" + message;
        } else {
            return prompt + "：请检查您的网络。";
        }
    }
}

