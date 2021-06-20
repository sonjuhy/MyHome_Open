package com.example.sonjunhyeok.myhome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.Calander.MyHome;
import com.example.sonjunhyeok.myhome.Light.LightListitem;
import com.example.sonjunhyeok.myhome.Notice.Notice_ListItem;
import com.example.sonjunhyeok.myhome.Weather.WeatherMainActivity;
import com.example.sonjunhyeok.myhome.Weather.Weather_Class;
import com.example.sonjunhyeok.myhome.Weather.Weather_Data;
import com.example.sonjunhyeok.myhome.Weather.Weather_ListItem;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadingActivity extends AppCompatActivity {
    public static boolean Login_check = false;
    public static String name = null;

    private int MAINACTIVITY_CODE = 100;
    private int LOGOUT_CODE = 101;
    private int LOGINACTIVITY_CODE = 200;
    private int FINISH_CODE = -1;

    private String fnumber;
    private String auto_id, auto_pw;
    private boolean AutoLoginCheck = false;
    public static SharedPreferences auto;
    public static Weather_Class weather_class = new Weather_Class();

    private Weather_Data UltraNcst;
    private ArrayList<Weather_Data> weather_data_VilageFcst = null;
    private ArrayList<Notice_ListItem> notice_listItems = null;

    private ExecutorService TRHEAD_POOL = Executors.newFixedThreadPool(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Intent intent;

        auto = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
        auto_id = auto.getString("inputID",null);
        auto_pw = auto.getString("inputPW",null);
        AutoLoginCheck = auto.getBoolean("AutoLoginCheck", false);

        if(auto_id != null && auto_pw != null && AutoLoginCheck){
            LoadingThreadClass loading = new LoadingThreadClass();
            loading.executeOnExecutor(TRHEAD_POOL);
        }
        if(!AutoLoginCheck){
            intent = new Intent(LoadingActivity.this, LoginActivity.class);
            startLoading(false, intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("request code : " + requestCode);
        System.out.println("result code : " + resultCode);
        if(requestCode == MAINACTIVITY_CODE){
            if(resultCode == FINISH_CODE){
                finish();
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
            }
            else if(resultCode == LOGOUT_CODE){
                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startLoading(false, intent);
            }
        }
        else if(requestCode == LOGINACTIVITY_CODE){
            if(resultCode == FINISH_CODE){
                finish();
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
            }
        }

    }

    private boolean AutoLogin_fun(String ID, String PW){
        String result;
        String[] tmp_result;
        System.out.println("Auto login id : " + ID + " PW : " + PW);
        Network network = new Network();
        try {
            result = network.execute("login",ID, PW).get();
            tmp_result = result.split("/");
            if(tmp_result.length > 1) {
                result = tmp_result[1];
            }
            JSONObject jsonObject;
            String check;
            jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("Login");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                check = jsontmp.getString("check");
                if(check.equals("Failed")){
                    Login_check = false;
                    break;
                }
                name = jsontmp.getString("name");
                fnumber = jsontmp.getString("fnumber");
                Login_check = true;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(Login_check == false){
            return false;
        }
        else{
            return true;
        }
    }
    private ArrayList<Notice_ListItem> Get_Notice(){
        Network network = new Network();
        String result;
        ArrayList<Notice_ListItem> list = new ArrayList<>();
        try{
            result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Get_Notice").get();
            System.out.println("notice : " + result);
            JSONObject jsonObject;
            jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("Notice");
            for(int i=0;i<jsonArray.length();i++){
                Notice_ListItem tmp = new Notice_ListItem();
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                tmp.setContent(jsontmp.getString("content"));
                tmp.setName(jsontmp.getString("name"));
                tmp.setTime(jsontmp.getString("time"));
                tmp.setNum(jsontmp.getString("number"));
                tmp.setTitle(jsontmp.getString("Title"));
                list.add(tmp);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    private void startLoading(final boolean mode, final Intent intent) {//true : main. false : login
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mode) {
                    startActivityForResult(intent, MAINACTIVITY_CODE);
                    overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                } else {
                    startActivityForResult(intent, LOGINACTIVITY_CODE);
                    overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                }
            }
        }, 1000);
    }
    private class LoadingThreadClass extends AsyncTask<Void, Void, Void>{
        Intent intent;
        boolean Login_check;

        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("Loading class in background");
            Login_check = AutoLogin_fun(auto_id,auto_pw);
            if(Login_check){
                Network network = new Network();
                ArrayList<LightListitem> list = null;
                try {
                    String result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Light_State").get();
                    list = LightlistParser(result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                notice_listItems = Get_Notice();
                weather_class.Weather_Search_Direct(91,76);
                UltraNcst = weather_class.Get_Weather_UtlraNcst();
                //weather_data_UltraFcst = weather_class.Get_Weather_UtlraFcst();
                weather_data_VilageFcst = weather_class.Get_Weather_VilageFcst();

                intent = new Intent(LoadingActivity.this, MainActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("ID",auto_id);
                intent.putExtra("PW",auto_pw);
                intent.putExtra("fnumber",fnumber);
                intent.putExtra("Ultra", UltraNcst);
                intent.putExtra("Vilage", weather_data_VilageFcst);
                intent.putExtra("notice", notice_listItems);
                intent.putExtra("light", list);
            }
            else{
                SharedPreferences.Editor autoLogindelete = auto.edit();
                autoLogindelete.clear();
                autoLogindelete.commit();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(Login_check){
                Toast.makeText(getApplicationContext(),auto_id+" 아이디로 자동로그인 되었습니다",Toast.LENGTH_LONG).show();
                startLoading(true, intent);
            }
            else{
                Toast.makeText(getApplicationContext(),"계정 정보가 틀립니다.",Toast.LENGTH_LONG).show();
                intent = new Intent(LoadingActivity.this, LoginActivity.class);
                startLoading(false, intent);
            }
        }
        private ArrayList<LightListitem> LightlistParser(String data){
            ArrayList<LightListitem> list = new ArrayList<>();
            try {
                JSONObject jsonObject;
                jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("Light_State");
                for(int i=0;i<jsonArray.length();i++){
                    LightListitem tmp = new LightListitem();
                    JSONObject jsontmp = jsonArray.getJSONObject(i);
                    tmp.setName(jsontmp.getString("Room"));
                    tmp.setName_kor(jsontmp.getString("Kor"));
                    tmp.setState(jsontmp.getString("State"));
                    tmp.setCategory(jsontmp.getString("Category"));
                    tmp.setPlace(i+1);
                    tmp.setAlarm_count(0);
                    list.add(tmp);
                }
                return list;
            } catch (Exception e) {
                System.out.println("error in loading Light JSON : " + e.getMessage());
                return null;
            }
        }
    }
}