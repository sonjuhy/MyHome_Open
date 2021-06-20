package com.example.sonjunhyeok.myhome;

public class USER {
    private String name;
    private String ID;
    private String PW;
    private String fnumber;

    public USER(String name,String ID, String PW, String fnumber){
        this.name = name;
        this.ID = ID;
        this.PW = PW;
        this.fnumber = fnumber;
    }
    public String Get_name(){
        return this.name;
    }
    public String Get_ID(){
        return this.ID;
    }
    public String Get_PW(){
        return this.PW;
    }
    public String Get_fnumber(){return this.fnumber;}
    public void Set_name(String name){
        this.name =name;
    }
    public void Set_ID(String ID){
        this.ID = ID;
    }
    public void Set_PW(String PW){
        this.PW = PW;
    }
    public void Set_fnumber(String fnumber){
        this.fnumber = fnumber;
    }
}
