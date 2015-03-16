package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SECourse;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SECourseResult extends ServiceResult {
    public boolean state;
    public ArrayList<SECourse> data;
    public String msg;

}
