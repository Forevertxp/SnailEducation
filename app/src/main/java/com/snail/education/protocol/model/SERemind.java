package com.snail.education.protocol.model;

/**
 * Created by tianxiaopeng on 15-3-22.
 */
public class SERemind {
    private String id;
    private String name;
    private String tips;
    private String date;
    private long time;
    private int diff;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTips() {
        return tips;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public int getDiff() {
        return diff;
    }
}
