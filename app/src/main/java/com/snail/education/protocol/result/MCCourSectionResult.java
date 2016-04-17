package com.snail.education.protocol.result;

import com.google.gson.annotations.SerializedName;
import com.snail.education.protocol.model.MCCourse;

/**
 * Created by tianxiaopeng on 15/12/21.
 */
public class MCCourSectionResult extends ServiceResult {

    public String apicode;
    public String message;
    @SerializedName("result")
    public MCCourse course;
}
