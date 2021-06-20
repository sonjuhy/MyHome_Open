package com.example.sonjunhyeok.myhome.Weather;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.FileServer.FileActivity;
import com.example.sonjunhyeok.myhome.FileServer.ListItem;
import com.example.sonjunhyeok.myhome.FileServer.RecyclerAdapter;
import com.example.sonjunhyeok.myhome.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Weather_Class implements Serializable, Parcelable{
    private Weather_Data UtlraNcst;
    private ArrayList<Weather_Data> UtlraFcst;
    private ArrayList<Weather_Data> VilageFcst;
    private ArrayList<Location_Data> location_leaf = new ArrayList<>();
    private Location_Data location_selected = new Location_Data();
    private Weather_Get weather_get;
    private Location_Class location_class;
    private Location_Data location_data;
    private RecyclerView recyclerView;
    private WeatherPlace_RecyclerAdapter recyclerAdapter;
    private int location_count = 0;

    private String time, date;
    public Weather_Class(RecyclerView recyclerView, WeatherPlace_RecyclerAdapter recyclerAdapter){
        this.UtlraNcst = new Weather_Data();
        this.UtlraFcst = new ArrayList<>();
        this.VilageFcst = new ArrayList<>();
        this.location_data = new Location_Data();
        this.recyclerView = recyclerView;
        this.recyclerAdapter = recyclerAdapter;

        Setlocation_class();

        date = Api_time();
        time = Api_timeChange(date);
    }
    public Weather_Class(){
        this.UtlraNcst = new Weather_Data();
        this.UtlraFcst = new ArrayList<>();
        this.VilageFcst = new ArrayList<>();
        this.location_data = new Location_Data();

        Setlocation_class();

        date = Api_time();
        time = Api_timeChange(date);
    }
    public void setRecycler(RecyclerView recycler, WeatherPlace_RecyclerAdapter recyclerAdapter){
        this.recyclerView = recycler;
        this.recyclerAdapter = recyclerAdapter;
    }

    protected Weather_Class(Parcel in) {
        location_count = in.readInt();
        time = in.readString();
        date = in.readString();
    }

    public static final Creator<Weather_Class> CREATOR = new Creator<Weather_Class>() {
        @Override
        public Weather_Class createFromParcel(Parcel in) {
            return new Weather_Class(in);
        }

        @Override
        public Weather_Class[] newArray(int size) {
            return new Weather_Class[size];
        }
    };

    private void Setlocation_class(){
        this.location_class = new Location_Class();
        this.location_class.setLocaion(location_leaf);
        this.location_class.setRecyclerView(this.recyclerView);
        this.location_class.setRecyclerAdapter(this.recyclerAdapter);
    }
    public int Weather_Search_Direct(int code_x, int code_y){
        location_data.setX_code(code_x);
        location_data.setY_code(code_y);
        try{
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int Weather_Search(String name, boolean up_down, WeatherPlace_RecyclerAdapter recyclerAdapter){ // false : up(위로 올라가기), true : down(세부 선택)
        Setlocation_class();
        System.out.println("location count : " + location_count);
        if(up_down){
            System.out.println("count ++");
            location_count++;
        }
        else{
            System.out.println("count --");
            location_count--;
        }
        if(location_count == 4){
            for(int i=0; i<location_leaf.size(); i++){
                if(name.equals(location_leaf.get(i).getCode())){
                    location_selected = location_leaf.get(i);
                    location_data = location_selected;
                    System.out.println("Select name : "+location_selected.getName());
                    System.out.println("Select code : "+location_selected.getCode());
                    this.UtlraNcst = this.Get_Weather_UtlraNcst();
                    if(this.UtlraFcst == null){
                        break;
                    }
                    try{
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    location_count = 0;
                    location_leaf = new ArrayList<>();
                    return 1;
                }
            }
        }
        else if(location_count >= 0 && location_count < 4){
            location_class.setMode(location_count);
            location_class.setRecyclerAdapter(recyclerAdapter);
            location_class.execute(name, "local");
        }
        return 0;
    }
    public Weather_Data Get_Weather_UtlraNcst(){
        this.weather_get = new Weather_Get();
        String result = null;
        weather_get.setWeather(this.UtlraNcst);
        try{
            result = weather_get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "UltraNcst","8",date.substring(0,8),time,
                    Integer.toString(location_data.getX_code()),
                    Integer.toString(location_data.getY_code())).get();
            Thread.sleep(500);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if("Success".equals(result)) {
            UtlraNcst = weather_get.getWeather();
            //UtlraNcst.print_UltraNcst();
            return this.UtlraNcst;
        }
        return null;
    }
    public ArrayList<Weather_Data> Get_Weather_UtlraFcst(){
        this.weather_get = new Weather_Get();
        String result = null;
        weather_get.setWeathers_ultra(this.UtlraFcst);

        try{
            result = weather_get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "UltraFcst","10",date.substring(0,8),time,
                    Integer.toString(location_data.getX_code()),
                    Integer.toString(location_data.getY_code())).get();
            Thread.sleep(100);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if("Success".equals(result)) {
            UtlraFcst = weather_get.getWeather_ultra();
            return this.UtlraFcst;
        }
        return null;
    }
    public ArrayList<Weather_Data> Get_Weather_VilageFcst(){
        this.weather_get = new Weather_Get();
        String result = null;
        weather_get.setWeathers_vliage(this.VilageFcst);
        try{
            result = weather_get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "VilageFcst","216",date.substring(0,8),time,
                    Integer.toString(location_data.getX_code()),
                    Integer.toString(location_data.getY_code())).get();
            Thread.sleep(500);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if("Success".equals(result)) {
            VilageFcst = weather_get.getWeathers_vilage();
            //VilageFcst.get(0).print_VilageFcst();
            return this.VilageFcst;
        }
        return null;
    }

    public String Api_time() {

        SimpleDateFormat Format = new SimpleDateFormat("yyyyMMdd HHmmss");
        Date time = new Date();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate date = LocalDate.now();
            String date_string = Integer.toString(date.getYear()) + date.getMonthValue() + date.getDayOfMonth();
            System.out.println("local date : " + date_string);
        }

        String timedata = Format.format(time);
        System.out.println("time data : " + time);
        return timedata;

    }
    /**********************************************************************
     * 현재시간을 가져와서 ex) 1000 형태로 만들어줌
     * 3시간 마다 업데이트 되기 때문에 각 시간에 따라 업데이트 시간으로 설정
     * @param timedata
     * @return
     * *********************************************************************/
    public String Api_timeChange(String timedata) {
        String hh = timedata.substring(9, 11);
        String baseTime = "";
        hh = hh + "00";

        // 현재 시간에 따라 데이터 시간 설정(3시간 마다 업데이트) //
        switch (hh) {

            case "0200":
            case "0300":
            case "0400":
                baseTime = "0200";
                break;
            case "0500":
            case "0600":
            case "0700":
                baseTime = "0500";
                break;
            case "0800":
            case "0900":
            case "1000":
                baseTime = "0800";
                break;
            case "1100":
            case "1200":
            case "1300":
                baseTime = "1100";
                break;
            case "1400":
            case "1500":
            case "1600":
                baseTime = "1400";
                break;
            case "1700":
            case "1800":
            case "1900":
                baseTime = "1700";
                break;
            case "2000":
            case "2100":
            case "2200":
                baseTime = "2000";
                break;
            default:
                baseTime = "2300";

        }
        return baseTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(location_count);
        dest.writeString(time);
        dest.writeString(date);
    }

    class Weather_Get extends AsyncTask<String, Void, String> implements Serializable{
        private String key = "8uiEDcNjEfxFOoq%2BIjRY2M7MAEKuW7AwNs9%2FyHFZUqmzm4Ci2hyvtfZdgZ7vGHBI6RjxsgBlnq%2BogcZfanSA%2Bw%3D%3D";
        private String url_UltraNcst = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst"; //need key, pageNo, numOfRows, base_date, base_time, x, y | 초단기실황
        private String url_UltraFcst = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst"; //need key, pageNo, numOfRows, base_date, base_time, x, y | 초단기예보
        private String url_VilageFcst = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";// need key, pageNo, numOfRows, dataType, base_date, base_time, x, y | 동네예보조회
        private String url_main = null;

        private String numOfRows = null;
        private String pageNo = null;
        private String base_date = null;
        private String base_time = null;
        private String place_x = null;
        private String place_y = null;

        private URLConnection conn;
        private BufferedReader br;

        private Weather_Data weather = null;
        private ArrayList<Weather_Data> weathers_vilage = null;
        private ArrayList<Weather_Data> weather_ultra = null;
        String ConnectValue = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            this.numOfRows = strings[1];
            this.base_date = strings[2];
            this.base_time = strings[3];
            this.place_x = strings[4];
            this.place_y = strings[5];
            int loop_max = 0;
            int mode = 0;
            if("UltraNcst".equals(strings[0])){
                mode = 0;
                loop_max = 1;
            }else if("UltraFcst".equals(strings[0])){
                mode = 1;
                loop_max = 4;
            }
            else if("VilageFcst".equals(strings[0])){
                mode = 2;
                loop_max = 1;
            }
            else{
                mode = -1;
                loop_max = 0;
            }
            pageNo = Integer.toString(loop_max);

            switch(mode){
                case 0:
                    url_main = url_UltraNcst + "?serviceKey=" + key + "&pageNo=" + this.pageNo + "&numOfRows=" + this.numOfRows + "&dataType=JSON&base_date=" + this.base_date + "&base_time=" + this.base_time + "&nx=" + this.place_x + "&ny=" + this.place_y;
                    break;
                case 1:
                    url_main = url_UltraFcst + "?serviceKey=" + key + "&pageNo=" + this.pageNo + "&numOfRows=" + this.numOfRows + "&dataType=JSON&base_date=" + this.base_date + "&base_time=" + this.base_time + "&nx=" + this.place_x + "&ny=" + this.place_y;
                    break;
                case 2:
                    url_main = url_VilageFcst + "?serviceKey=" + key + "&pageNo=" + this.pageNo + "&numOfRows=" + this.numOfRows + "&dataType=JSON&base_date=" + this.base_date + "&base_time=" + this.base_time + "&nx=" + this.place_x + "&ny=" + this.place_y;
                    break;
                default:
                    System.out.println("Weather class mode error");
                    break;
            }
            try {
                for(int i=0;i<loop_max;i++) {
                    URL url = new URL(url_main);
                    conn = url.openConnection();
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String ResData = br.readLine();
                    System.out.println("url : " + url_main);
                    if (ResData == null) {
                        System.out.println("응답데이터 == NULL");
                    } else {
                        System.out.println("br ResData(" +i+") : "+ResData);
                        ConnectValue = fn_Jsonp(ResData, mode);
                        if(!"Success".equals(ConnectValue)){
                            System.out.println("JSON data is error : " + ConnectValue);
                            break;
                        }
                        //System.out.println("connect value : " + ConnectValue);

                    }
                    br.close();
                }
            } catch (Exception e) {
                System.out.println("error : " + e.getMessage());
            }

            return ConnectValue;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        public void setWeather(Weather_Data weather) {
            this.weather = weather;
        }
        public void setWeathers_vliage(ArrayList<Weather_Data> weathers){
            this.weathers_vilage = weathers;
        }
        public void setWeathers_ultra(ArrayList<Weather_Data> weathers){
            this.weather_ultra = weathers;
        }
        public Weather_Data getWeather(){ return this.weather; }
        public ArrayList<Weather_Data> getWeathers_vilage() { return this.weathers_vilage; }
        public ArrayList<Weather_Data> getWeather_ultra() { return this.weather_ultra; }

        public String fn_Jsonp(String Data, int mode)
        {
            try {
                JSONObject jsonObject = new JSONObject(Data);
                JSONObject jsonObject_res = (JSONObject) jsonObject.get("response");
                JSONObject jsonObject_header = (JSONObject)jsonObject_res.get("header");
                JSONObject jsonObject_body = (JSONObject) jsonObject_res.get("body");
                JSONObject jsonObject_item = (JSONObject) jsonObject_body.get("items");
                JSONArray jsonArray = jsonObject_item.getJSONArray("item");

                System.out.println("json array length : " + jsonArray.length());
                System.out.println("mode in fn_jsonp : " + mode);
                String result = null;
                result = jsonObject_header.get("resultCode").toString();
                if(!"00".equals(result)){
                    System.out.println("JSON data is error : " + result);
                    return result;
                }
                else{
                    if(mode == 0) {
                        this.weather = JsonParsing(jsonArray, mode);
                        System.out.println("fn_Jsonp*********");
                        this.weather.print_UltraNcst();
                    }
                    else {
                        JsonParsing(jsonArray, mode);
                    }
                }
            } catch (Exception e) {
                System.out.println("error in fn_Jsnop : " + e.getMessage());
            }

            return "Success";
        }

        public Weather_Data JsonParsing(JSONArray jsonArray, int mode) {
            JSONObject WeatherData;
            String DataValue = "";
            String info = "";
            String resultcode = "";
            String fsct_date = null;
            String fsct_time = null;
            int loop_max = 1;
            Weather_Data tmp_weather = new Weather_Data();
            ArrayList<Weather_Data> tmp_weathers = new ArrayList<>(4);
            if(mode == 1){
                loop_max = 4;
            }
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    for(int j=0; j<loop_max; j++) {
                        WeatherData = (JSONObject) jsonArray.get(i);
                        info = WeatherData.get("category").toString();
                        if (mode == 0) {
                            DataValue = WeatherData.get("obsrValue").toString();
                            System.out.println("info value : " + info);
                            System.out.println("Data value : " + DataValue);
                        } else {
                            DataValue = WeatherData.get("fcstValue").toString();
                            fsct_time = WeatherData.get("fcstTime").toString();
                            fsct_time = fsct_time.substring(0,2)+":00";
                            if("00:00".equals(fsct_time)){
                                fsct_time = "24:00";
                            }
                            fsct_date = WeatherData.get("fcstDate").toString();
                            tmp_weather.setFcstDate(fsct_date);
                            tmp_weather.setFcstTime(fsct_time);
                        }

                        switch (mode) {
                            case 0:// T1H, RN1, UUU, VVV, REH, PTY, VEC, WSD
                            {
                                if (info.equals("WSD")) {
                                    info = "풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setWSD(DataValue);
                                }
                                if (info.equals("RN1")) {
                                    info = "시간당강수량";
                                    DataValue = DataValue;// + " mm";
                                    tmp_weather.setRN1(DataValue);
                                }
                                if (info.equals("REH")) {
                                    info = "습도";
                                    DataValue = DataValue;// + "%";
                                    tmp_weather.setREH(DataValue);
                                }
                                if (info.equals("UUU")) {
                                    info = "동서성분풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setUUU(DataValue);
                                }
                                if (info.equals("VVV")) {
                                    info = "남북성분풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setVVV(DataValue);
                                }
                                if (info.equals("T1H")) {
                                    info = "기온";
                                    DataValue = DataValue;// + "℃";
                                    tmp_weather.setT1H(DataValue);
                                }
                                if (info.equals("PTY")) {
                                    info = "강수형태";
                                    if (DataValue.equals("0")) {
                                        DataValue = "없음";
                                    } else if (DataValue.equals("1")) {
                                        DataValue = "비";
                                    } else if (DataValue.equals("2")) {
                                        DataValue = "눈/비";
                                    } else if (DataValue.equals("3")) {
                                        DataValue = "눈";
                                    }
                                    tmp_weather.setPTY(DataValue);
                                }
                                if (info.equals("VEC")) {
                                    info = "풍향";
                                    DataValue = DataValue;// + " m/s";
                                    DataValue = Cal_VEC(DataValue);
                                    tmp_weather.setVEC(DataValue);
                                }
                                weather = tmp_weather;
                            }
                            break;
                            case 1: //T1H, RN1, SKY, UUU, VVV, REH, PTY, LGT, VEC, WSD
                            {
                                if (info.equals("LGT")) {
                                    info = "낙뢰";
                                    if (DataValue.equals("0")) {
                                        tmp_weather.setLGT("없음");
                                    } else if (DataValue.equals("1")) {
                                        tmp_weather.setLGT("있음");
                                    }
                                }
                                if (info.equals("WSD")) {
                                    info = "풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setWSD(DataValue);
                                }
                                if (info.equals("RN1")) {
                                    info = "시간당강수량";
                                    DataValue = DataValue;// + " mm";
                                    tmp_weather.setRN1(DataValue);
                                }
                                if (info.equals("REH")) {
                                    info = "습도";
                                    DataValue = DataValue;// + "%";
                                    tmp_weather.setREH(DataValue);
                                }
                                if (info.equals("SKY")) {
                                    info = "하늘상태";
                                    if (DataValue.equals("1")) {
                                        DataValue = "맑음";
                                    } else if (DataValue.equals("2")) {
                                        DataValue = "비";
                                    } else if (DataValue.equals("3")) {
                                        DataValue = "구름많음";
                                    } else if (DataValue.equals("4")) {
                                        DataValue = "흐림";
                                    }
                                    tmp_weather.setSKY(DataValue);
                                }
                                if (info.equals("UUU")) {
                                    info = "동서성분풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setUUU(DataValue);
                                }
                                if (info.equals("VVV")) {
                                    info = "남북성분풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setVVV(DataValue);
                                }
                                if (info.equals("T1H")) {
                                    info = "기온";
                                    DataValue = DataValue;// + "℃";
                                    tmp_weather.setT1H(DataValue);
                                }
                                if (info.equals("PTY")) {
                                    info = "강수형태";
                                    if (DataValue.equals("0")) {
                                        DataValue = "없음";
                                    } else if (DataValue.equals("1")) {
                                        DataValue = "비";
                                    } else if (DataValue.equals("2")) {
                                        DataValue = "눈/비";
                                    } else if (DataValue.equals("3")) {
                                        DataValue = "눈";
                                    }
                                    tmp_weather.setPTY(DataValue);
                                }
                                if (info.equals("VEC")) {
                                    info = "풍향";
                                    DataValue = DataValue;// + " m/s";
                                    DataValue = Cal_VEC(DataValue);
                                    tmp_weather.setVEC(DataValue);
                                }
                                tmp_weathers.set(i, tmp_weather);
                                break;
                            }
                            case 2:// *POP, *PTY, *R06, *REH, *S06, *SKY, *T3H, *TMN, *TMX, *UUU, *VVV, *WAV, *VEC, *WSD
                            {
                                if (info.equals("POP")) {
                                    info = "강수확률";
                                    DataValue = DataValue;// + " %";
                                    tmp_weather.setPOP(DataValue);
                                }
                                if (info.equals("REH")) {
                                    info = "습도";
                                    DataValue = DataValue;// + " %";
                                    tmp_weather.setREH(DataValue);
                                }
                                if (info.equals("SKY")) {
                                    info = "하늘상태";
                                    if (DataValue.equals("1")) {
                                        DataValue = "맑음";
                                    } else if (DataValue.equals("2")) {
                                        DataValue = "비";
                                    } else if (DataValue.equals("3")) {
                                        DataValue = "구름많음";
                                    } else if (DataValue.equals("4")) {
                                        DataValue = "흐림";
                                    }
                                    tmp_weather.setSKY(DataValue);
                                }
                                if (info.equals("UUU")) {
                                    info = "동서성분풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setUUU(DataValue);
                                }
                                if (info.equals("VVV")) {
                                    info = "남북성분풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setVVV(DataValue);
                                }
                                if (info.equals("R06")) {
                                    info = "6시간강수량";
                                    DataValue = DataValue;// + " mm";
                                    tmp_weather.setR06(DataValue);
                                }
                                if (info.equals("S06")) {
                                    info = "6시간적설량";
                                    DataValue = DataValue;// + " mm";
                                    tmp_weather.setS06(DataValue);
                                }
                                if (info.equals("PTY")) {
                                    info = "강수형태";
                                    if (DataValue.equals("0")) {
                                        DataValue = "없음";
                                    } else if (DataValue.equals("1")) {
                                        DataValue = "비";
                                    } else if (DataValue.equals("2")) {
                                        DataValue = "눈/비";
                                    } else if (DataValue.equals("3")) {
                                        DataValue = "눈";
                                    }
                                    tmp_weather.setPTY(DataValue);
                                }
                                if (info.equals("T3H")) {
                                    info = "3시간기온";
                                    DataValue = DataValue;// + " ℃";
                                    tmp_weather.setT3H(DataValue);
                                }
                                if (info.equals("VEC")) {
                                    info = "풍향";
                                    DataValue = DataValue;// + " m/s";
                                    DataValue = Cal_VEC(DataValue);
                                    tmp_weather.setVEC(DataValue);
                                }
                                /*if (info.equals("WAV")) {
                                    info = "파고";
                                    DataValue = DataValue + " M";
                                    weather.setWSD(DataValue);
                                }*/
                                if(info.equals("TMN")) {
                                    info = "아침최저기온";
                                    //DataValue = DataValue + " ℃";
                                    tmp_weather.setTMN(DataValue);
                                }
                                if(info.equals("TMX")) {
                                    info = "낮최고기온";
                                    //DataValue = DataValue + " ℃";
                                    tmp_weather.setTMX(DataValue);
                                }
                                if (info.equals("WSD")) {
                                    info = "풍속";
                                    DataValue = DataValue;// + " m/s";
                                    tmp_weather.setWSD(DataValue);

                                    this.weathers_vilage.add(tmp_weather);
                                    tmp_weather = new Weather_Data();
                                    //tmp_weather.print_VilageFcst();
                                }
                            }
                            break;
                        }
                    }
                }
                if(mode == 1){
                    for(int i=0;i<4;i++){
                        this.weather_ultra.add(tmp_weathers.get(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tmp_weather;
        }
        private String Cal_VEC(String value){
            double value_double = Double.parseDouble(value);
            int value_int = (int) Math.round(((value_double + 22.5*0.5)/22.5));
            String vec = null;
            switch(value_int){
                case 0:
                case 16:
                    vec = "북";
                    break;
                case 1:
                case 2:
                case 3:
                    vec = "북동";
                    break;
                case 4:
                    vec = "동";
                    break;
                case 5:
                case 6:
                case 7:
                    vec = "남동";
                    break;
                case 8:
                    vec = "남";
                    break;
                case 9:
                case 10:
                case 11:
                    vec = "남서";
                    break;
                case 12:
                    vec = "서";
                    break;
                case 13:
                case 14:
                case 15:
                    vec = "북서";
                    break;

            }
            return vec;
        }
    }
}
