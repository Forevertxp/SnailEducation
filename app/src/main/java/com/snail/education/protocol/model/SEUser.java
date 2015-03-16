package com.snail.education.protocol.model;


import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SEUser implements Serializable {

    private String id;
    private String user;
    private String pass;
    private String icon;
    private String name;
    private String nickname;
    private String mail;
    private String say;
    private String sex;
    private String point;
    private String passcode;
    private String findpass;
    private String sys_msg_lastid;
    private long regtime;
    private String sn;
    private String sina_id;
    private String weixin_id;
    private String qq_id;
    private boolean loginok;

    private boolean _isDataDirty = false;

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMail() {
        return mail;
    }

    public String getSay() {
        return say;
    }

    public String getSex() {
        return sex;
    }

    public String getPoint() {
        return point;
    }

    public String getPasscode() {
        return passcode;
    }

    public String getFindpass() {
        return findpass;
    }

    public String getSys_msg_lastid() {
        return sys_msg_lastid;
    }

    public long getRegtime() {
        return regtime;
    }

    public String getSn() {
        return sn;
    }

    public String getSina_id() {
        return sina_id;
    }

    public String getWeixin_id() {
        return weixin_id;
    }

    public String getQq_id() {
        return qq_id;
    }

    public boolean isLoginok() {
        return loginok;
    }

    public boolean is_isDataDirty() {
        return _isDataDirty;
    }

    public void markDataDirty() {
        _isDataDirty = true;
    }

    public boolean checkAndCleanDataDirty() {
        boolean isDataDirty = _isDataDirty;
        _isDataDirty = false;
        return isDataDirty;
    }

}

