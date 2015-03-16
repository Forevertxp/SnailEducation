package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-2-2.
 */
public class SEStudent implements Serializable {

    private String id;
    private String name;
    private String speak;
    private String icon;
    private String sta;
    private String school;
    private String url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpeak() {
        return speak;
    }

    public String getIcon() {
        return icon;
    }

    public String getSta() {
        return sta;
    }

    public String getSchool() {
        return school;
    }

    public String getUrl() {
        return url;
    }
}
