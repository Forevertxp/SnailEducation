package com.snail.education.protocol.model;

/**
 * Created by tianxiaopeng on 15-3-22.
 */
public class SERemind {
    private String id;
    private String name;
    private Object tips;
    private String date;
    private long time;
    private String diff;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Object getTips() {
        return tips;
    }

    public String getDiff() {
        return diff;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

}
