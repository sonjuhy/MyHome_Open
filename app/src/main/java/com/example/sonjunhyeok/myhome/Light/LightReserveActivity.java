package com.example.sonjunhyeok.myhome.Light;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LightReserveActivity extends AppCompatActivity {
    private TextView textView_nodata;

    private LightReserveAdapter adpater;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_reserve);

        Toolbar toolbar = findViewById(R.id.light_reserveList_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.Light_ReserveList_linearLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        List<LightReserveListitem> data;
        textView_nodata = findViewById(R.id.light_reserveList_nodata_textview);

        Network network = new Network();
        try {
            String result_network = network.execute("Light_ReserveList").get();
            System.out.println("result Network in reserve : " + result_network);
            data = Parser(result_network);
            if(data.size() == 0){
                textView_nodata.setVisibility(View.VISIBLE);
            }
            else{
                textView_nodata.setVisibility(View.INVISIBLE);
            }
            adpater = new LightReserveAdapter(data);
            recyclerView.setAdapter(adpater);
            adpater.notifyDataSetChanged();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<LightReserveListitem> Parser(String data){//name, day, room, time ,action
        ArrayList<LightReserveListitem> list = new ArrayList<>();
        try {
            JSONObject jsonObject;
            jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("Reserve");
            for(int i=0;i<jsonArray.length();i++){
                LightReserveListitem tmp = new LightReserveListitem();
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                tmp.setAction(jsontmp.getString("action"));
                tmp.setName(jsontmp.getString("name"));
                tmp.setRoom(jsontmp.getString("room"));
                tmp.setRoomkor(jsontmp.getString("roomkor"));
                tmp.setPrimary_key(jsontmp.getInt("num"));
                tmp.setRepeat(jsontmp.getString("repeat"));
                String[] tmp_days, days = {"","","","","","",""},tmp_times;
                System.out.println("day data : " + jsontmp.getString("day"));
                if(!jsontmp.getString("day").equals("No data")){
                    tmp_days = jsontmp.getString("day").split(",");
                    System.out.println("days length : " + tmp_days.length);
                    int dow = 6;
                    for(int j=tmp_days.length-1; j>=0; j--){
                        System.out.println("days data : " + tmp_days[j]);
                        switch(dow){
                            case 6:
                                if(tmp_days[j].equals("일")){
                                    break;
                                }
                            case 5:
                                if(tmp_days[j].equals("토")){
                                    dow = 5;
                                    break;
                                }
                            case 4:
                                if(tmp_days[j].equals("금")){
                                    dow = 4;
                                    break;
                                }
                            case 3:
                                if(tmp_days[j].equals("목")){
                                    dow = 3;
                                    break;
                                }
                            case 2:
                                if(tmp_days[j].equals("수")){
                                    dow = 2;
                                    break;
                                }
                            case 1:
                                if(tmp_days[j].equals("화")){
                                    dow = 1;
                                    break;
                                }
                            case 0:
                                if(tmp_days[j].equals("월")){
                                    dow = 0;
                                    break;
                                }
                            default:
                                break;
                        }
                        days[dow] = tmp_days[j];
                    }
                    for(int j=0;j<7;j++){
                        System.out.println("days : " + days[j]);
                    }
                    tmp.setDays(days);
                }
                else{
                    tmp.setDays(new String[]{"", "", "", "", "", "", ""});
                }
                String time_tmp = jsontmp.getString("time");
                tmp_times = time_tmp.split(":");
                tmp.setHour(Integer.parseInt(tmp_times[0]));
                tmp.setMin(Integer.parseInt(tmp_times[1]));
                list.add(tmp);
            }
            return list;
        } catch (Exception e) {
            System.out.println("error in loading Light JSON : " + e.getMessage());
            return null;
        }
    }
}