package com.snail.education.bean;

/**
 * Created by Sai on 15/11/22.
 */
public class ProvinceBean {
    private long id;
    private String name;
    private String description;
    private long pid;

    public ProvinceBean(long id, String name, String description, long pid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pid = pid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    //这个用来显示在PickerView上面的字符串,PickerView会通过反射获取getPickerViewText方法显示出来。
    public String getPickerViewText() {
        //这里还可以判断文字超长截断再提供显示
        return name;
    }
}
