package com.snail.education.protocol.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by tianxiaopeng on 15/12/21.
 */
public class MCCourSection implements Serializable {
    public String childId;
    public String childName;
    public ArrayList<MCCourPoint> pointList;
}
