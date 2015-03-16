package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SECourse implements Serializable{

    private int id;
    private String name;
    private String student;
    private String collect;
    private String praise;
    private String top;
    private String price;
    private String cid;
    private String cname;
    private String tid;
    private String tfid;
    private String tname;
    private String oid;
    private String oname;
    private String tag;
    private String sort;
    private String icon;
    private String _free;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStudent() {
        return student;
    }

    public String getCollect() {
        return collect;
    }

    public String getPraise() {
        return praise;
    }

    public String getTop() {
        return top;
    }

    public String getPrice() {
        return price;
    }

    public String getCid() {
        return cid;
    }

    public String getCname() {
        return cname;
    }

    public String getTid() {
        return tid;
    }

    public String getTfid() {
        return tfid;
    }

    public String getTname() {
        return tname;
    }

    public String getOid() {
        return oid;
    }

    public String getOname() {
        return oname;
    }

    public String getTag() {
        return tag;
    }

    public String getSort() {
        return sort;
    }

    public String getIcon() {
        return icon;
    }

    public String get_free() {
        return _free;
    }
}
