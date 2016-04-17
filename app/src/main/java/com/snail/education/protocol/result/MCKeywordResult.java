package com.snail.education.protocol.result;

import com.google.gson.annotations.SerializedName;
import com.snail.education.protocol.model.MCKeyword;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15/12/19.
 */
public class MCKeywordResult extends ServiceResult{
    public String apicode;
    public String message;
    @SerializedName("result")
    public ArrayList<MCKeyword> keywordList;
}
