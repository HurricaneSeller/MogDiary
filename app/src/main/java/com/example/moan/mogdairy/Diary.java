package com.example.moan.mogdairy;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;

public class Diary extends LitePalSupport implements Serializable {
    private int Id;
    private String title;
    private String content;
    private boolean hasClock;
    private String priority;
    private String hour;
    private String minute;
    private String day;
    private String month;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }


    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }


    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public boolean isHasClock() {
        return hasClock;
    }

    public void setHasClock(boolean hasClock) {
        this.hasClock = hasClock;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

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

}
