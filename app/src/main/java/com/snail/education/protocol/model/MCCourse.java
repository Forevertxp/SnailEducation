package com.snail.education.protocol.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15/12/21.
 */
public class MCCourse implements Serializable{
    public String courId;
    public String courName;
    public String courProfile;
    public String playcount;
    @SerializedName("child")
    public ArrayList<MCSubCourse> courseArrayList;
    @SerializedName("children")
    public ArrayList<MCCourSection> sectionArrayList;
}
