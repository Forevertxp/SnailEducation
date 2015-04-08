package com.snail.education.protocol.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-3-22.
 */
public class SETeacherInfo {
    private String id;
    private String fid;
    private String sid;
    private String name;
    private String sort;
    private String job;
    private String comm;
    private String collect;
    private String praise;
    private String sname;
    private String oname;
    private String py;
    private String icon;
    @SerializedName("class")
    private ArrayList<SECourse> classList;

    public String getId() {
        return id;
    }

    public String getFid() {
        return fid;
    }

    public String getSid() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public String getSort() {
        return sort;
    }

    public String getJob() {
        return job;
    }

    public String getComm() {
        return comm;
    }

    public String getCollect() {
        return collect;
    }

    public String getPraise() {
        return praise;
    }

    public String getSname() {
        return sname;
    }

    public String getOname() {
        return oname;
    }

    public String getPy() {
        return py;
    }

    public String getIcon() {
        return icon;
    }

    public ArrayList<SECourse> getClassList() {
        return classList;
    }
}
