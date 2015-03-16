package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SEStudent;
import com.snail.education.protocol.model.SEUser;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-2-2.
 */
public class SEStudentResult extends ServiceResult {
    public String msg;
    public String page;
    public String limit;
    public boolean state;
    public ArrayList<SEStudent> data;
}
