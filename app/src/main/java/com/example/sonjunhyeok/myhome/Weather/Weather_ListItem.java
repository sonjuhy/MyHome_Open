package com.example.sonjunhyeok.myhome.Weather;

public class Weather_ListItem {
    private String Name;
    private String Status;
    private String Unit;
    private String Data;


    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }
}
