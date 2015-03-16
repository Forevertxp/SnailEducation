package com.snail.education.protocol.model;

import java.io.Serializable;

/**
 * Created by tianxiaopeng on 15-1-20.
 */
public class SEAdvertisement implements Serializable {

    private String id;
    private String type;
    private String img;
    private String sort;
    private SEAdEvent event;

    public SEAdEvent getEvent() {
        return event;
    }

    public String getImg() {
        return img;
    }
}