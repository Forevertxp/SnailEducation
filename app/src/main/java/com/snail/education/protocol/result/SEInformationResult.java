package com.snail.education.protocol.result;

import com.snail.education.protocol.model.SEInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 15-1-1.
 */
public class SEInformationResult extends ServiceResult {

    public boolean state;
    public String cage;
    public ArrayList<SEInformation> data;
    public String msg;
}
