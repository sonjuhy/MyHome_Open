package com.example.sonjunhyeok.myhome.FileServer;

public class file_class {
    private String name;
    private boolean type; //true is folder, false is file

    public String Get_name(){
        return this.name;
    }
    public boolean Get_type(){
        return this.type;
    }
    public void Set_name(String name){
        this.name = name;
    }
    public void Set_type(boolean type){
        this.type = type;
    }
}
