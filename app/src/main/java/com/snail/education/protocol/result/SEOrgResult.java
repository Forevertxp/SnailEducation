package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SEOrgCate;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/3/26.
 */
public class SEOrgResult extends ServiceResult {
    public boolean state;
    public String msg;
    public ArrayList<SEOrgCate> data;
}
