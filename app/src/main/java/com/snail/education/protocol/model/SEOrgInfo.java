package com.snail.education.protocol.model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/9.
 */
public class SEOrgInfo {
    private String id;
    private String city;
    @SerializedName("techer_count")
    private String teacher_count;
    private String class_count;
    private String startyear;
    private String name;
    private String sort;
    private String collect;
    private String praise;
    private String py;
    private String icon;
    @SerializedName("class")
    private ArrayList<SECourse> classList;
    @SerializedName("tecacher")
    private ArrayList<SETeacher> teacherList;


    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getTeacher_count() {
        return teacher_count;
    }

    public String getClass_count() {
        return class_count;
    }

    public String getStartyear() {
        return startyear;
    }

    public String getName() {
        return name;
    }

    public String getSort() {
        return sort;
    }

    public String getCollect() {
        return collect;
    }

    public String getPraise() {
        return praise;
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

    public ArrayList<SETeacher> getTeacherList() {
        return teacherList;
    }
}
