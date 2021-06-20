package com.example.sonjunhyeok.myhome.FileServer;

import android.graphics.drawable.Drawable;

import com.example.sonjunhyeok.myhome.R;

public class ListItem {
    private String name;
    private String size;
    private String PrePath;
    private long size_long;
    private boolean type;// true is folder false is file

    public ListItem(String name, String size, String path, boolean type){
        this.type = type;
        this.name = name;
        this.size = size;
        this.PrePath = path;
    }

    public void set_sizelong(long size){this.size_long = size;}
    public long get_sizelong(){return this.size_long;}
    public String getName() {
        return name;
    }
    public boolean getType(){
        return this.type;
    }
    public void setType(boolean type){
        this.type = type;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }
    public String getPrePath(){
        return PrePath;
    }
    public void setSize(String size) {
        this.size = size;
    }
}
