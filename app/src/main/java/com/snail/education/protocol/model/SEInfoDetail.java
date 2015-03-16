package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-1-27.
 */
public class SEInfoDetail implements Serializable {

    private String id;
    private String fid;
    private String title;
    private String info;
    private long time;
    private int view;
    private String zan;
    private String date;
    private String host;

    public String getId() {
        return id;
    }

    public String getFid() {
        return fid;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public long getTime() {
        return time;
    }

    public int getView() {
        return view;
    }

    public String getZan() {
        return zan;
    }

    public String getDate() {
        return date;
    }

    public String getHost() {
        return host;
    }
}
