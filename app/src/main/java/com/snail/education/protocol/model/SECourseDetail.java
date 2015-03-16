package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-1-18.
 */
public class SECourseDetail implements Serializable {

    private String id;
    private String cid;
    private String tid;
    private String name;
    private String price;
    private String praise;
    private String student;
    private String collect;
    private String tag;
    private String top;
    private String sort;
    private String video;
    private String thumb;
    private String info;
    private String free;
    private String _collect;
    private String _praise;
    private String _buy;
    private int _watch_time;

    public String getId() {
        return id;
    }

    public String getCid() {
        return cid;
    }

    public String getTid() {
        return tid;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getPraise() {
        return praise;
    }

    public String getStudent() {
        return student;
    }

    public String getCollect() {
        return collect;
    }

    public String getTag() {
        return tag;
    }

    public String getTop() {
        return top;
    }

    public String getSort() {
        return sort;
    }

    public String getVideo() {
        return video;
    }

    public String getThumb() {
        return thumb;
    }

    public String getInfo() {
        return info;
    }

    public String getFree() {
        return free;
    }

    public String get_collect() {
        return _collect;
    }

    public String get_praise() {
        return _praise;
    }

    public String get_buy() {
        return _buy;
    }

    public int get_watch_time() {
        return _watch_time;
    }
}
