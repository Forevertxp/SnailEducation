package com.snail.education.protocol.result;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2015/3/20.
 */
public class SEWXPayInfoResult {
    public String appid;
    public String noncestr;
    public String partnerid;
    public String prepayid;
    public String retcode;
    public String retmsg;
    public String sign;
    public long timestamp;
    @SerializedName("package")
    public String packageValue;
}
