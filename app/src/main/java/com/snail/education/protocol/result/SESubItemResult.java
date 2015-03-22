package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SESubItem;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-3-22.
 */
public class SESubItemResult extends ServiceResult {
    public boolean state;
    public String cage;
    public String msg;
    public ArrayList<SESubItem> data;
}
