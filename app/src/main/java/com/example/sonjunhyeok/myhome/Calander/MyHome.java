package com.example.sonjunhyeok.myhome.Calander;

import android.app.Application;

import java.io.Serializable;
import java.util.ArrayList;

public class MyHome extends Application implements Serializable {
    private ArrayList<Cal_line> Cal_line = new ArrayList<>();
    private User_class User_personal;

    public void Input_Cal(Calander_struct Input_Cs){
        int start = 0, end = Cal_line.size(), mid = 0;
        Calander_Time_milestione tmp_cal_tm = new Calander_Time_milestione();
        while(start <= end){
            mid = (start + end)/2;
            if(Cal_line.get(mid).Output_Starttime() == Input_Cs.Cal_Output_start_time_chek()){
                tmp_cal_tm.Input_Cs(Input_Cs);
                Cal_line.get(mid).Input_Cal_Tm(tmp_cal_tm,Input_Cs.Cal_Output_start_time_chek(),Input_Cs.Cal_Output_End_time_check());
            }else{
                if(Cal_line.get(mid).Output_Starttime() < Input_Cs.Cal_Output_start_time_chek()){
                    start = mid;
                }
                else{
                    end = mid;
                }
            }
        }
    }
    public void Input_User(User_class Input_User){
        this.User_personal = Input_User;
    }
    public ArrayList<Calander_Time_milestione> Output_Cal(int day){
       ArrayList<Calander_Time_milestione> tmp_cal = new ArrayList<>();
        int start = 0, end = Cal_line.size() , mid = 0;
        //int tmp_size = Cal_Tm.size(), start;
        if(Cal_line.isEmpty() != true){
            while(start <= end){
                mid = (start + end)/2;
                if(Cal_line.get(mid).Output_Starttime() == day){
                    return Cal_line.get(mid).Output_Cal_Tm();
                }else{
                    if(Cal_line.get(mid).Output_Starttime() < day){
                        start = mid;
                    }
                    else{
                        end = mid;
                    }
                }
            }
        }
        else{
            return null;
        }
        return null;
    }
    public User_class Output_User(){
        return User_personal;
    }
}
class Cal_line{
    private ArrayList<Calander_Time_milestione> Cal_Tm = new ArrayList<>();
    private int start_time = 0,end_time = 0;

    public void Input_Cal_Tm(Calander_Time_milestione Cs,int Start_time, int End_time){
        Cal_Tm.add(Cs);
        start_time = Start_time;
        end_time = End_time;
    }
    public ArrayList<Calander_Time_milestione> Output_Cal_Tm(){
        return this.Cal_Tm;
    }
    public int Output_Starttime(){
        return this.start_time;
    }
    public int Output_Endtime(){
        return this.end_time;
    }
}
