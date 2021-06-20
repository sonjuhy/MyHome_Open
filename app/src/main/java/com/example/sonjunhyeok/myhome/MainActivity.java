package com.example.sonjunhyeok.myhome;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.Calander.CalenderActivity;
import com.example.sonjunhyeok.myhome.Calander.MyHome;
import com.example.sonjunhyeok.myhome.DevLog.DevLogActivity;
import com.example.sonjunhyeok.myhome.FileServer.FileActivity;
import com.example.sonjunhyeok.myhome.Light.LightActivity;
import com.example.sonjunhyeok.myhome.Light.LightListMainAdapter;
import com.example.sonjunhyeok.myhome.Light.LightListitem;
import com.example.sonjunhyeok.myhome.Notice.NoticeMainActivity;
import com.example.sonjunhyeok.myhome.Notice.Notice_ListItem;
import com.example.sonjunhyeok.myhome.Setting.SettingActivity;
import com.example.sonjunhyeok.myhome.Weather.WeatherMainActivity;
import com.example.sonjunhyeok.myhome.Weather.Weather_Data;
import com.example.sonjunhyeok.myhome.Weather.Weather_ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static double Version = 0.9;
    public static USER user;
    public MyHome MH = new MyHome();

    private final int SETTING_CODE = 10;
    private final int LOGOUT_CODE = 11;
    private final int NOTICE_CODE = 20;
    private final int NOTICE_REFRESH = 21;
    private final int LIGHT_CODE = 30;
    private final int LIGHT_REFRESH = 31;
    private int light_now = 0;

    private String Local_Path;
    private String folder_device;
    private String folder_server;
    private String filename;
    private String name,Id,PW,fnumber;
    private String room_select;

    private File dir;
    private ImageView weather_image;
    private TextView Username, UserID, weather_temp, weather_reh, weather_wsd, weather_vec, weather_pty, notice_content;

    private Weather_Data UltraNcst = null;
    private ArrayList<Weather_ListItem> weather_listItems = null;
    private ArrayList<Weather_Data> weather_data_VilageFcst = null;
    private ArrayList<Notice_ListItem> notice_listItems = null;
    private ArrayList<LightListitem> light_listItems = null;
    private ArrayList<String> markrooms = null;

    private LightListMainAdapter light_adpater;
    private RecyclerView recyclerView;
    private Messenger mServiceMessenger = null;
    private boolean mIsBound;
    private boolean mqtt_check = false;

    private TextView text_all, text_living, text_kitchen, text_bath, text_balcony, text_big, text_middle, text_small, text_mark;
    private TextView text_light_title, text_light_state;

    private Intent service_intent = null;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setSupportActionBar(toolbar);

        service_intent = new Intent(MainActivity.this, MQTT_Main.class);
        service_intent.putExtra("select",1);
        startService(service_intent);

        final Intent intent = getIntent();
        name = intent.getStringExtra("Name");
        Id = intent.getStringExtra("ID");
        PW = intent.getStringExtra("PW");
        fnumber = intent.getStringExtra("fnumber");
        UltraNcst = (Weather_Data) intent.getSerializableExtra("Ultra");
        weather_data_VilageFcst = (ArrayList<Weather_Data>) intent.getSerializableExtra("Vilage");
        notice_listItems = (ArrayList<Notice_ListItem>) intent.getSerializableExtra("notice");
        light_listItems = (ArrayList<LightListitem>) intent.getSerializableExtra("light");

        sharedPreferences = MainActivity.this.getSharedPreferences("roomMark",MODE_PRIVATE);
        markrooms = markRoomJSONParsing();

        System.out.println("fnumber : " + fnumber);
        user = new USER(name, Id, PW, fnumber);
        System.out.println("main activity : " + user.Get_fnumber());

        Local_Path = Environment.getExternalStorageDirectory().getAbsolutePath();
        folder_device = Local_Path + "/MyHome/";//route for make folder in android device
        folder_server = "/home/disk1/home/private/";//point of server folder with ad video
        filename = "/";//add filename(ad video name)
        dir = new File(folder_device);
        Folder_Setting();//make folder and allow permission for use storage


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerview = navigationView.getHeaderView(0);

        //find view id part(weather, light, User info)
        weather_reh = findViewById(R.id.main_weather_reh);
        weather_temp = findViewById(R.id.main_weather_temperature);
        weather_vec = findViewById(R.id.main_weather_vec);
        weather_wsd = findViewById(R.id.main_weather_wsd);
        weather_pty = findViewById(R.id.main_weather_pty);
        weather_image = findViewById(R.id.main_weather_image);
        notice_content = findViewById(R.id.main_notice_contentview);

        text_all = findViewById(R.id.main_light_all_textview);
        text_mark = findViewById(R.id.main_light_mark_textview);
        text_living = findViewById(R.id.main_light_living_textview);
        text_kitchen  = findViewById(R.id.main_light_kitchen_textview);
        text_balcony = findViewById(R.id.main_light_balcony_textview);
        text_bath = findViewById(R.id.main_light_bathroom_textview);
        text_big = findViewById(R.id.main_light_bigroom_textview);
        text_middle = findViewById(R.id.main_light_middleroom_textview);
        text_small = findViewById(R.id.main_light_smallroom_textview);
        text_light_title = findViewById(R.id.light_cardview_name_text);
        text_light_state = findViewById(R.id.light_cardview_state_text);

        Username = headerview.findViewById(R.id.UserNametextView);
        UserID = headerview.findViewById(R.id.USerIDtextView);

        //light part
        recyclerView = findViewById(R.id.main_light_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        light_now = 0;
        light_adpater = new LightListMainAdapter();
        light_adpater.addContext(this);
        recyclerView.setAdapter(light_adpater);

        light_fun(light_now);

        //User ID part
        Username.setText(name+"님");
        UserID.setText("로그인 된 아이디 : " + Id);

        if(TextUtils.isEmpty(UltraNcst.getPTY()) || weather_data_VilageFcst.size() == 0){
            weather_pty.setText("--");
            weather_reh.setText("--");
            weather_temp.setText("--");
            weather_vec.setText("--");
            weather_wsd.setText("--");
            weather_image.setImageResource(R.drawable.weather_sun_black);
        }
        else{
            weather_pty.setText(weather_data_VilageFcst.get(0).getSKY());
            weather_reh.setText(UltraNcst.getREH()+"%");
            weather_temp.setText(UltraNcst.getT1H()+"℃");
            weather_vec.setText(UltraNcst.getVEC()+"풍");
            weather_wsd.setText(UltraNcst.getWSD()+"m/s");
            switch(UltraNcst.getPTY()){
                case "비":
                    weather_image.setImageResource(R.drawable.weather_rain);
                case "눈/비":
                    break;
                case "눈":
                    weather_image.setImageResource(R.drawable.weather_snow_black);
                    break;
                case "없음":
                    weather_image.setImageResource(R.drawable.weather_sun_black);
                    break;
            }
        }
        notice_content.setText(notice_listItems.get(notice_listItems.size()-1).getContent());
        {
            text_mark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 0;
                    light_fun(light_now);
                }
            });
            text_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 1;
                    light_fun(light_now);
                }
            });
            text_living.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 2;
                    light_fun(light_now);
                }
            });
            text_kitchen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 3;
                    light_fun(light_now);
                }
            });
            text_balcony.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 4;
                    light_fun(light_now);
                }
            });
            text_bath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 5;
                    light_fun(light_now);
                }
            });
            text_big.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 6;
                    light_fun(light_now);
                }
            });
            text_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 7;
                    light_fun(light_now);
                }
            });
            text_small.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    light_now = 8;
                    light_fun(light_now);
                }
            });
        }

        recyclerView.addOnItemTouchListener(new MainActivity.RecyclerTouchListener(getApplicationContext(), recyclerView, new MainActivity.ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                LightListitem lightlistitem = (LightListitem)light_adpater.getItem(position);
                Log.w("main switch click", lightlistitem.getName());
                Log.w("main switch click", lightlistitem.getState());
                //MQTT_Main mqtt = new MQTT_Main();
                MQTT_Sub mqtt = new MQTT_Sub();
                //mqtt.setMQTT(lightlistitem.getName(), 1, lightlistitem.getCategory());
                if(lightlistitem.getState().equals("On")){
                    mqtt.on_publishInMain(lightlistitem.getName(), lightlistitem.getCategory(), "OFF");
                }
                else if(lightlistitem.getState().equals("Off")){
                    mqtt.on_publishInMain(lightlistitem.getName(), lightlistitem.getCategory(), "ON");
                }
                room_select = lightlistitem.getName();
            }
            @Override
            public void OnLongClick(View view, int position) {

            }
        }));
    }
    private boolean light_fun(int mode){
        String select = null;
        switch(mode){
            case 0:
                select = "mark";
                break;
            case 1:
                select = "all";
                break;
            case 2:
                select = "living Room";
                break;
            case 3:
                select = "kitchen";
                break;
            case 4:
                select = "balcony";
                break;
            case 5:
                select = "bath Room";
                break;
            case 6:
                select = "Big Room";
                break;
            case 7:
                select = "middle Room";
                break;
            case 8:
                select = "small Room";
                break;
            default:
                return false;
        }
        light_adpater.clearList();

        if(mode == 0){
            for(int i=0;i<light_listItems.size();i++){
                for(int j=0;j<markrooms.size();j++){
                    if(light_listItems.get(i).getName().equals(markrooms.get(j))){
                        light_adpater.addItem(light_listItems.get(i));
                    }
                }
            }
        }
        else if(mode == 1){
            for(int i=0;i<light_listItems.size();i++){
                light_adpater.addItem(light_listItems.get(i));
            }
        }
        else {
            for (int i = 0; i < light_listItems.size(); i++) {
                if (light_listItems.get(i).getCategory().equals(select)) {
                    light_adpater.addItem(light_listItems.get(i));
                }
            }
        }
        light_adpater.notifyDataSetChanged();

        return true;
    }
    private void markRoomInput(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<markrooms.size();i++){
            jsonArray.put(markrooms.get(i));
        }
        editor.putString("roomMark",jsonArray.toString());
        editor.apply();
    }
    private ArrayList<String> markRoomJSONParsing(){
        String jsonStr = sharedPreferences.getString("roomMark",null);
        ArrayList<String> roomNames = new ArrayList<>();
        if(jsonStr != null){
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for(int i=0;i<jsonArray.length();i++){
                    roomNames.add(jsonArray.optString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return roomNames;
    }
    public void mOnClickMainWeather(View view){
        System.out.println("Touch!!");
        Intent intent_weather = new Intent(MainActivity.this, WeatherMainActivity.class);
        startActivity(intent_weather);
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }
    public void mOnClickMainNotice(View view){
        Intent intent_notice = new Intent(MainActivity.this, NoticeMainActivity.class);
        intent_notice.putExtra("notice", notice_listItems);
        startActivityForResult(intent_notice, NOTICE_CODE);
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
        return;
    }
    public interface ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.OnLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)){
                clickListener.OnClick(child, recyclerView.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //Nav_setting_clicker
        final Intent Cal_intent =new Intent(MainActivity.this, CalenderActivity.class);
        final Intent file_intent = new Intent(MainActivity.this, FileActivity.class);
        final Intent DevLog_intent = new Intent(MainActivity.this, DevLogActivity.class);
        final Intent Setting_intent = new Intent(MainActivity.this, SettingActivity.class);
        final Intent Light_intent = new Intent(MainActivity.this, LightActivity.class);
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putSerializable("GV" , MH);
        //intent.putExtras(bundle);
        Cal_intent.putExtra("GV", MH);
        if(id == R.id.nav_chat){
            Toast.makeText(this, "아직 기능 구현 중 입니다.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_calender) {
            Toast.makeText(this, "아직 기능 구현 중 입니다.", Toast.LENGTH_SHORT).show();
            startActivity(Cal_intent);
            overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
        } else if (id == R.id.nav_FileServer) {
            file_intent.putExtra("folder_device",folder_device);
            file_intent.putExtra("Loacl_device",Local_Path);
            file_intent.putExtra("fnumber",user.Get_fnumber());
            startActivity(file_intent);
            overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
        }
        else if(id == R.id.nav_Light){
            System.out.println("nav light : " +LIGHT_CODE);
            Light_intent.putExtra("markRooms", markrooms);
            startActivityForResult(Light_intent, LIGHT_CODE);
            overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
        }else if (id == R.id.nav_setting) {
            System.out.println("nav setting");
            startActivityForResult(Setting_intent,SETTING_CODE);
            overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
        }
        else if(id == R.id.nav_devlog){
            startActivity(DevLog_intent);
            overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("requestCode : " + requestCode);
        if(requestCode == SETTING_CODE){
            if(resultCode == LOGOUT_CODE){
                user = null;
                setStopService();
                setResult(101);
                finish();
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
            }
        }
        else if(requestCode == NOTICE_CODE){
            if(resultCode == NOTICE_REFRESH){
                notice_listItems = (ArrayList<Notice_ListItem>) data.getSerializableExtra("notice_list");
                notice_content.setText(notice_listItems.get(notice_listItems.size()-1).getContent());
            }
        }
        else if(requestCode == LIGHT_CODE){
            if(resultCode == LIGHT_REFRESH){
                markrooms = data.getStringArrayListExtra("roomMark");
                light_listItems = (ArrayList<LightListitem>) data.getSerializableExtra("list");
                markRoomInput();
                light_fun(light_now);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("onStart","MainActivity");
        setStartService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop","MainActivity");
        setStopService();
    }
    @Override
    protected void onDestroy() {
        setStopService();
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        System.out.println("Main back press");
        setStopService();
        setResult(-1);
        finish();
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }
    private void Folder_Setting(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //if storage permission isn't allow, popup to allow selecting
            System.out.println("Storage Permission is denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        else{
            System.out.println("Storage Permission is allowed");
        }
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            //check existing about out storage
            System.out.println("Can use SD");
        }
        if(!dir.exists()){
            System.out.println("folder isn't exist");
            //make folder
            dir.mkdirs();
        }
        else{
            System.out.println("already exist");
        }
    }

    private void setStopService() {
        Log.w("main mqtt service","stop");
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        stopService(service_intent);

    }
    private void setStartService() {
        bindService(new Intent(this, MQTT_Main.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.w("main service","connected");
            mServiceMessenger = new Messenger(iBinder);
            mIsBound = true;
            try {
                Message msg = Message.obtain(null, MQTT_Main.MSG_SEND_TO_LIGHT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("main service","disconnected");
            mServiceMessenger = null;
            mIsBound = false;
        }
    };

    /** Service 로 부터 message를 받음 */
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MQTT_Main.MSG_SEND_TO_NOTICE:
                    Network network = new Network();
                    String result;
                    notice_listItems = new ArrayList<>();
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
                            notice_listItems.add(tmp);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            notice_content.setText(notice_listItems.get(notice_listItems.size()-1).getContent());
                        }
                    });
                    break;
                case MQTT_Main.MSG_SEND_TO_LIGHT:
                    Bundle bundle = msg.getData();
                    if(bundle.getString("error").equals("yes")){
                        Toast.makeText(getApplicationContext(),"오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String sender = bundle.getString("sender");
                        String message = bundle.getString("message");
                        String destination = bundle.getString("destination");
                        Log.w("main light", destination);
                        Log.w("main light", message);
                        Log.w("main light", sender);

                        String message_toast = "";
                        if(message.equals("already On")){
                            message_toast="이미 켜져있습니다.";
                            message = "On";
                        }
                        else if(message.equals("already Off")){
                            message_toast="이미 꺼져있습니다.";
                            message = "Off";
                        }
                        else{
                            message_toast="전등이 작동했습니다.";
                        }
                        if(sender.equals(user.Get_name())){
                            Toast.makeText(getApplicationContext(),message_toast,Toast.LENGTH_SHORT).show();
                        }
                        for(int i=0;i<light_listItems.size();i++){
                            if(light_listItems.get(i).getName().equals(destination)){
                                Log.w("find desination","here");
                                light_listItems.get(i).setState(message);
                            }
                        }
                        light_fun(light_now);
                    }
                    break;
                case MQTT_Main.MSG_SEND_TO_DISCONNECT:
                    Log.w("main mqtt connect","reconnecting...");
                    Toast.makeText(getApplicationContext(),"서버와 연결이 끊어졌습니다. 5초뒤 다시 시도하세요.",Toast.LENGTH_SHORT).show();
                    setStartService();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    }));
}