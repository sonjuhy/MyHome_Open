package com.example.sonjunhyeok.myhome.Weather;

import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class Location_Class extends AsyncTask<String, Void, String > implements Serializable {
    private String url_top = "https://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt";
    private String url_middle = "https://www.kma.go.kr/DFSROOT/POINT/DATA/mdl.";
    private String url_leaf = "https://www.kma.go.kr/DFSROOT/POINT/DATA/leaf.";
    private URL url;
    private URLConnection conn;
    private BufferedReader br;

    private ArrayList<Location_Data> location_state = new ArrayList<>();
    private ArrayList<Location_Data> location_city = new ArrayList<>();
    private ArrayList<Location_Data> location_leaf;

    private WeatherPlace_RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private String mode_str = null;
    private int mode = 0;

    public void setMode(int mode) { this.mode = mode; }
    public void setRecyclerAdapter(WeatherPlace_RecyclerAdapter recyclerAdapter) { this.recyclerAdapter = recyclerAdapter; }
    public void setRecyclerView(RecyclerView recyclerView) { this.recyclerView = recyclerView; }
    public void setLocaion(ArrayList<Location_Data> locaion){
        this.location_leaf = locaion;
    }

    @Override
    protected String doInBackground(String... strings) {
        String result, code_string = strings[0];
        System.out.println("code string : " + code_string);
        mode_str = strings[1];
        System.out.println("mode_str : "+ mode_str);

        ArrayList<Location_Data> result_code = null;

        try {
            switch (mode){
                case 1:
                    url = new URL(url_top);
                    conn = url.openConnection();
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    result = br.readLine().toString();
                    System.out.println("result : "+result);
                    br.close();
                    //code = XmlParsing(result, state);
                    location_state = XmlParsing_All(result);

                    //System.out.println("state code : "+ code);
                    //location_data.setCode(code);
                    break;
                case 2:
                    url = new URL(url_middle+code_string+".json.txt");
                    conn = url.openConnection();
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    result = br.readLine().toString();
                    System.out.println("result : "+result);
                    br.close();
                    //code = XmlParsing(result, city);
                    location_city = XmlParsing_All(result);

                    //System.out.println("city code : "+ code);
                    //location_data.setCode(code);
                    break;
                case 3:
                    url = new URL(url_leaf+code_string+".json.txt");
                    System.out.println("url : "+url_leaf+code_string+".json.txt");
                    conn = url.openConnection();
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    result = br.readLine().toString();
                    System.out.println("result : "+result);
                    br.close();
                    //code = XmlParsing_leaf(result, location_data);
                    if("Direct".equals(mode_str)) {
                        location_leaf.add(XmlParsing_leaf(result, code_string));
                    }
                    else{
                        location_leaf = XmlParsing_leaf_All(result);
                    }

                    break;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if("Direct".equals(mode_str)){
            return "end";
        }
        else{
            return "Success";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if("Success".equals(s)){
            System.out.println("location class post in");
            recyclerAdapter = Sort_List();

            recyclerView.setAdapter(recyclerAdapter);
        }
    }

    private int XmlParsing(String parsing, String search){
        try{
            System.out.println("search is : "+search);
            JSONArray jsonArray = new JSONArray(parsing);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                if(search.equals(jsontmp.getString("value"))){
                    return jsontmp.getInt("code");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private Location_Data XmlParsing_leaf(String parsing, String code){
        try{
            JSONArray jsonArray = new JSONArray(parsing);
            Location_Data tmp_data = new Location_Data();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                if(code.equals(jsontmp.getString("code"))){
                    tmp_data.setName(jsontmp.getString("value"));
                    tmp_data.setX_code(jsontmp.getInt("x"));
                    tmp_data.setY_code(jsontmp.getInt("y"));
                    tmp_data.setCode(jsontmp.getString("code"));
                    return tmp_data;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private ArrayList<Location_Data> XmlParsing_All(String parsing){
        try{
            JSONArray jsonArray = new JSONArray(parsing);
            for(int i=0;i<jsonArray.length();i++){
                Location_Data tmp_data = new Location_Data();
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                tmp_data.setName(jsontmp.getString("value"));
                tmp_data.setCode(jsontmp.getString("code"));
                if(mode == 1) {
                    location_state.add(tmp_data);
                }
                else if(mode == 2){
                    location_city.add(tmp_data);
                }
                else{
                    System.out.println("error on mode info");
                }
            }
            if(mode == 1) {
                return location_state;
            }
            else if(mode == 2){
                location_city.add(new Location_Data("위로"));
                Collections.swap(location_city,0,location_city.size()-1);
                return location_city;
            }
            else{
                System.out.println("error on mode info");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private ArrayList<Location_Data> XmlParsing_leaf_All(String parsing){
        try{
            JSONArray jsonArray = new JSONArray(parsing);
            location_leaf.add(new Location_Data("위로"));
            for(int i=0;i<jsonArray.length();i++){
                Location_Data tmp_data = new Location_Data();
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                tmp_data.setName(jsontmp.getString("value"));
                tmp_data.setX_code(jsontmp.getInt("x"));
                tmp_data.setY_code(jsontmp.getInt("y"));
                tmp_data.setCode(jsontmp.getString("code"));

                location_leaf.add(tmp_data);
            }
            return location_leaf;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private WeatherPlace_RecyclerAdapter Sort_List(){
        switch(mode){
            case 1:
                for(int i=0;i<location_state.size();i++){
                    Weather_Place_ListItem weather_place_listItem = new Weather_Place_ListItem();
                    weather_place_listItem.setName(location_state.get(i).getName());
                    weather_place_listItem.setCode(location_state.get(i).getCode());
                    recyclerAdapter.addItem(weather_place_listItem);
                }
                break;
            case 2:
                for (int i=0; i<location_city.size(); i++){
                    Weather_Place_ListItem weather_place_listItem = new Weather_Place_ListItem();
                    weather_place_listItem.setName(location_city.get(i).getName());
                    weather_place_listItem.setCode(location_city.get(i).getCode());
                    recyclerAdapter.addItem(weather_place_listItem);
                }
                break;
            case 3:
                for (int i=0; i<location_leaf.size(); i++){
                    Weather_Place_ListItem weather_place_listItem = new Weather_Place_ListItem();
                    weather_place_listItem.setName(location_leaf.get(i).getName());
                    weather_place_listItem.setCode(location_leaf.get(i).getCode());
                    recyclerAdapter.addItem(weather_place_listItem);
                }
                break;
            default:
                System.out.println("error on mode info");
                break;
        }
        return recyclerAdapter;
    }
}
