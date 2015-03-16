package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-1-20.
 */
public class SEIndexCount implements Serializable {

    private String teacher;
    private String service;
    private String student;

    public String getTeacher() {
        return teacher;
    }

    public String getService() {
        return service;
    }

    public String getStudent() {
        return student;
    }
}
