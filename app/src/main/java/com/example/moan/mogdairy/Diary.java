package com.example.moan.mogdairy;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

public class Diary extends LitePalSupport implements Serializable {
    private int Id;
    private String title;
    private String content;
    private Date date;

    public boolean isHasClock() {
        return hasClock;
    }

    public void setHasClock(boolean hasClock) {
        this.hasClock = hasClock;
    }

    private boolean hasClock;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
