package com.snail.education.protocol.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15/12/21.
 */
public class MCSubCourse {
    public String id;
    public String name;
    public String pid;
    public String profile;
    public String type;
    public String address;
    public String playcount;
    @SerializedName("subList")
    public ArrayList<MCSubCourse> subCourseArrayList;
}
