package com.snail.education.protocol.model;

import com.snail.education.protocol.manager.SEComment;

import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15-1-17.
 */
public class SEStory {
    private String id;
    private String uid;
    private String msg;
    private ArrayList<String> pics;
    private String user_nickname;
    private String user_say;
    private String user_icon;
    private ArrayList<SEComment> rep;
    private String _time;
    private int praise;
    private int rep_count;
    private int _praised;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getMsg() {
        return msg;
    }

    public ArrayList<String> getPics() {
        return pics;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_say() {
        return user_say;
    }

    public String getUser_icon() {
        return user_icon;
    }

    public ArrayList<SEComment> getRep() {
        return rep;
    }

    public String get_time() {
        return _time;
    }

    public int getPraise() {
        return praise;
    }

    public int getRep_count() {
        return rep_count;
    }

    public int get_praised() {
        return _praised;
    }

    public void set_praised(int _praised) {
        this._praised = _praised;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }
}
