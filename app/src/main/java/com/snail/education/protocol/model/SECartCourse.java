package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/3/16.
 */
public class SECartCourse implements Serializable {
    private String grade;
    private String icon;
    private String name;
    private String oname;
    private String price;
    private String tname;
    private String top;

    public String getGrade() {
        return grade;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getOname() {
        return oname;
    }

    public String getPrice() {
        return price;
    }

    public String getTname() {
        return tname;
    }

    public String getTop() {
        return top;
    }
}
