package com.example.moan.mogdairy;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Diary extends LitePalSupport implements Serializable {
    private int Id;
    private String title;
    private String content;
    private boolean hasClock;
    private String priority;
    private int hour;
    private int minute;
    private int day;
    private int month;
    private long total;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }


    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
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
