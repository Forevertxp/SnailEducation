package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SECourseCate;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-1.
 */
public class SECourseCateResult extends ServiceResult {

    public boolean state;
    public ArrayList<SECourseCate> data;
    public String msg;
}
