package com.example.sonjunhyeok.myhome.Light;

import java.io.Serializable;

public class LightReserveListitem implements Serializable {
    private String room;
    private String roomkor;
    private String name;
    private String repeat;
    private String action;
    private int hour;
    private int min;
    private int primary_key;
    private String[] days = new String[7];

    public LightReserveListitem(){}
    public LightReserveListitem(String room, String name, String repeat, String action, int hour, int min, String[] days){
        this.room = room;
        this.name = name;
        this.repeat = repeat;
        this.action = action;
        this.hour = hour;
        this.min = min;
        this.days = days;
    }
    public int getPrimary_key(){return primary_key;}

    public void setPrimary_key(int primary_key){this.primary_key = primary_key;}

    public String getRoomkor(){return roomkor;}

    public void setRoomkor(String roomkor) {this.roomkor = roomkor;}

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String[] getDays() {
        return days;
    }

    public void setDays(String[] days) {
        this.days = days;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
