package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SECourse;
import com.snail.education.protocol.model.SECourseDetail;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-18.
 */
public class SECourseDetailResult extends ServiceResult {
    public boolean state;
    public SECourseDetail data;
    public ArrayList<SECourse> other;
    public String msg;
}
