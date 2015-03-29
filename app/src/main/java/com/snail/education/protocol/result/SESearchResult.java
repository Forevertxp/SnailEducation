package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SESearch;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-3-29.
 */
public class SESearchResult extends ServiceResult {
    public boolean state;
    public String msg;
    public ArrayList<SESearch> data;

}
