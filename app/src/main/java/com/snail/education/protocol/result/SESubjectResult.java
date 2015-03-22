package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SESubject;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-3-22.
 */
public class SESubjectResult extends ServiceResult {
    public boolean state;
    public String msg;
    public ArrayList<SESubject> data;

}
