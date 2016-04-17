package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15/12/29.
 */
public class MCCollectionItem implements Serializable{
    public String id;
    public String name;
    public String address;
    public String profile;
    public int collectionType; //1 视频分类（文件夹） 2 视频
}
