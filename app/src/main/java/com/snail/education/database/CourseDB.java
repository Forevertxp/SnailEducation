package com.snail.education.database;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

/**
 * Created by Administrator on 2015/3/18.
 */
public class CourseDB {

    @Id // 如果主键没有命名名为id或_id的时，需要为主键添加此注解
    @NoAutoIncrement // int,long类型的id默认自增，不想使用自增时添加此注解
    private int id;

    @Column(column = "name") //为列名加上注解 可以针对命名不统一和防止混淆
    private String name;  //课程名

    @Column(column = "thumb")
    private String thumb;  //课程图片地址

    @Column(column = "video")
    private String video;  //课程视频地址

    @Column(column = "size")
    private long size;  //已经下载的课程大小

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
