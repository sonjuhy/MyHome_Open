package com.example.sonjunhyeok.myhome.Calander;

import java.util.ArrayList;

public class User_class {
    private String User_name;
    private int Alarm;
    private ArrayList<Calander_struct> Cs;

    User_class(){
        User_name = "User1";
        Alarm = 0;
        Cs = new ArrayList<>();
    }
    public void Cal_Insert(Calander_struct C){
        this.Cs.add(C);
    }
    public void Cal_Output(){
    }
    public void Name_Input(String Input_name){
        this.User_name = Input_name;
    }
    public String Name_Output(){
        return this.User_name;
    }
}
class Calander_Time_milestione{
    private int start_time;
    private int End_time;
    private Calander_struct Cs;

    public void Input_time(int Start_day, int End_day){
        this.start_time = Start_day;
        this.End_time = End_day;
    }
    public void Input_Cs(Calander_struct C){
        this.Cs = C;
    }
    public int Output_time(){
        return this.start_time;
    }
    public Calander_struct Output_Cs(){
        return this.Cs;
    }
}
class Calander_struct {
    private String Cal_name, Cal_contant;
    private int start_year,start_month, start_day, start_hour, start_minute;
    private int end_year, end_month, end_day, end_hour, end_minute;
    private int start_time, end_time;
    private boolean Cal_alarm = false;

    public void Cal_insert_String(String name, String contant){
        Cal_name = name;
        Cal_contant = contant;
    }
    public void Cal_insert_Starttime(int year, int month, int day, int hour, int minute){
        start_year = year;
        start_month = month;
        start_day = day;
        start_hour = hour;
        start_minute = minute;
        start_time = year*1000+month*100+day;
    }
    public void Cal_insert_Endtime(int year, int month, int day, int hour, int minute){
        end_year = year;
        end_month = month;
        end_day = day;
        end_hour = hour;
        end_minute = minute;
        end_time = year*1000+month*100+day;
    }
    public void Cal_Alarm(boolean select){
        if(select){
            Cal_alarm = true;
        }
        else{
            Cal_alarm = false;
        }
    }
    public int Cal_Output_start_time_chek(){
        return start_time;
    }
    public int Cal_Output_End_time_check(){
        return end_time;
    }
    public String Cal_Output_Name_time(){
        return Cal_name+String.valueOf(start_hour)+" 시"+String.valueOf(start_minute)+" 분 ~ "+String.valueOf(end_hour)+" 시"+String.valueOf(end_minute)+" 분";
    }
    public boolean Cal_Output_alarm(){
        return Cal_alarm;
    }
    public String Cal_Output_Calname(){
        return Cal_name;
    }
}