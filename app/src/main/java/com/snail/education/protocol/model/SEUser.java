package com.snail.education.protocol.model;


import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-1-10.
 */
public class SEUser implements Serializable {

    private String id;
    private String phone;
    private String passwd;
    private String avator;
    private String name;
    private String sign;
    private String email;
    private String age;
    private String sex;
    private String local;

    private boolean _isDataDirty = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
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

