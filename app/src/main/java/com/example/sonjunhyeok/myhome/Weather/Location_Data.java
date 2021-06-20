package com.example.sonjunhyeok.myhome.Weather;

import java.io.Serializable;

public class Location_Data implements Serializable {
    private String name;
    private String code;
    private int x_code;
    private int y_code;

    public Location_Data(String name){
        this.name = name;
    }
    public Location_Data(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getX_code() {
        return x_code;
    }

    public void setX_code(int x_code) {
        this.x_code = x_code;
    }

    public int getY_code() {
        return y_code;
    }

    public void setY_code(int y_code) {
        this.y_code = y_code;
    }
}
