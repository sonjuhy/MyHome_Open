package com.example.sonjunhyeok.myhome.Light;

import java.io.Serializable;

public class LightListitem implements Serializable {
    private String name;
    private String state;
    private String name_kor;
    private String category;
    private int place;
    private int alarm_count;

    public LightListitem(String name, String state, int count, int place){
        this.name = name;
        this.state = state;
        this.alarm_count = count;
        this.place = place;
    }
    public LightListitem(){}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName_kor() {
        return name_kor;
    }

    public void setName_kor(String name_kor) {
        this.name_kor = name_kor;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAlarm_count(){return alarm_count;}
    public void setAlarm_count(int alarm_count){this.alarm_count = alarm_count;}
    public int getPlace(){return place;}
    public void setPlace(int Place){this.place = Place;}
}
