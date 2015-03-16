package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SECart;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-2-9.
 */
public class SECartResult extends ServiceResult {
    public boolean state;
    public String total;
    public String msg;
    public ArrayList<SECart> data;
}
