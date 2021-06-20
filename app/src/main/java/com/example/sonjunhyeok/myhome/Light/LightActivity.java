package com.example.sonjunhyeok.myhome.Light;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sonjunhyeok.myhome.MQTT_Main;
import com.example.sonjunhyeok.myhome.MQTT_Sub;
import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static com.example.sonjunhyeok.myhome.MainActivity.user;

public class LightActivity extends AppCompatActivity {

    private LightListAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private ArrayList<LightListitem> lightListitem = new ArrayList<>();
    private ArrayList<String> markRooms = new ArrayList<>();

    private int mode = 0;
    private final int LIGHT_CODE = 30;
    private final int LIGHT_REFRESH = 31;
    private String select_state = "Off";
    private String select_room = "";
    private String select_room_kor = "";
    private String select_category = "";

    private ImageButton switch_button;
    private Button alarm_button, category_button, refresh_button;
    private TextView select_textview;
    private ToggleButton select_togglebutton;
    private MQTT_Main mqtt;

    private Messenger mServiceMessenger = null;
    private boolean mIsBound;

    private Intent service_intent = null;
    private SharedPreferences sharedPreferences;

    public final static int RESERVEADD_CODE = 1;
    public final static int RESERVEADD_UPLOAD = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        //getSupportActionBar().setElevation(0);

        Toolbar toolbar = findViewById(R.id.light_toolbar);
        setSupportActionBar(toolbar);

        Intent intent_get = getIntent();
        markRooms = intent_get.getStringArrayListExtra("markRooms");

        select_category = "living Room";
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_light);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        sharedPreferences = getSharedPreferences("roomMark",MODE_PRIVATE);

        LightLoadingClass loading = new LightLoadingClass(this, lightListitem);
        loading.execute();

        recyclerAdapter = new LightListAdapter();
        recyclerView.setAdapter(recyclerAdapter);


        select_textview = findViewById(R.id.light_selectresultView);
        switch_button = findViewById(R.id.light_button_onoff);
        category_button = findViewById(R.id.light_room_category_button);
        refresh_button = findViewById(R.id.light_room_refresh_button);
        select_togglebutton = findViewById(R.id.light_selectmark_button);
        switch_button.setAlpha(0.5f);
        switch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == 0){
                    Toast.makeText(getApplicationContext(), "방을 선택하세요.",Toast.LENGTH_LONG).show();
                }
                else {
                    mqtt = new MQTT_Main();
                    mqtt.setMQTT(select_room, 2, select_category);
                    if(select_state.equals("On")){
                        mqtt.Mqtt_Publish(1, "OFF");
                    }
                    else if(select_state.equals("Off")){
                        mqtt.Mqtt_Publish(1, "ON");
                    }
                }
            }
        });
        alarm_button = findViewById(R.id.light_button_alarm);
        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode == 0){
                    Toast.makeText(getApplicationContext(), "방을 선택하세요.",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(LightActivity.this, LightReserveAddActivity.class);
                    intent.putExtra("Room_eng",select_room);
                    intent.putExtra("Room_kor",select_room_kor);
                    startActivityForResult(intent, RESERVEADD_CODE);
                }
            }
        });
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(LightActivity.this, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                Menu menu = popupMenu.getMenu();
                inflater.inflate(R.menu.light_etc, menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_light_etc_refresh:
                                lightListitem = new ArrayList<>();
                                LightLoadingClass loading = new LightLoadingClass(LightActivity.this, lightListitem);
                                loading.execute();
                                break;
                            case  R.id.action_light_etc_reservelist:
                                Intent intent_lightlist = new Intent(LightActivity.this, LightReserveActivity.class);
                                //startActivityForResult(intent_lightlist, 2);
                                startActivity(intent_lightlist);
                                break;
                            case R.id.action_light_etc_markclear:
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("roomMark");
                                editor.apply();
                                markRooms = new ArrayList<>();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        category_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(LightActivity.this, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                Menu menu = popupMenu.getMenu();
                inflater.inflate(R.menu.light_category, menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.action_light_category_balcony:
                                select_category = "balcony";
                                Light_Data(lightListitem);
                                category_button.setText("베란다");
                                return true;
                            case R.id.action_light_category_bathRoom:
                                select_category = "bath Room";
                                Light_Data(lightListitem);
                                category_button.setText("화장실");
                                return true;
                            case R.id.action_light_category_bigRoom:
                                select_category = "Big Room";
                                Light_Data(lightListitem);
                                category_button.setText("큰방");
                                return true;
                            case R.id.action_light_category_kitchen:
                                select_category = "kitchen";
                                Light_Data(lightListitem);
                                category_button.setText("부엌");
                                return true;
                            case R.id.action_light_category_livingRoom:
                                select_category = "living Room";
                                Light_Data(lightListitem);
                                category_button.setText("거실");
                                return true;
                            case R.id.action_light_category_middleRoom:
                                select_category = "middle Room";
                                Light_Data(lightListitem);
                                category_button.setText("중간방");
                                return true;
                            case R.id.action_light_category_smallRoom:
                                select_category = "small Room";
                                Light_Data(lightListitem);
                                category_button.setText("작은방");
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        select_togglebutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mode == 0){
                    Toast.makeText(getApplicationContext(), "방을 선택하세요.",Toast.LENGTH_LONG).show();
                }
                else {
                    if (isChecked) {
                        for(int i=0;i<markRooms.size();i++) {
                            if(markRooms.get(i).equals(select_room)){
                                return;
                            }
                        }
                        MarkRoomInput(select_room);
                    } else {
                        for(int i=0;i<markRooms.size();i++){
                            if(markRooms.get(i).equals(select_room)){
                                MarkRoomOutput(select_room);
                                break;
                            }
                        }
                    }
                }
            }
        });
        recyclerView.addOnItemTouchListener(new LightActivity.RecyclerTouchListener(getApplicationContext(), recyclerView, new LightActivity.ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                LightListitem lightlistitem = (LightListitem)recyclerAdapter.getItem(position);
                mode = lightlistitem.getPlace();
                select_room = lightlistitem.getName();
                select_room_kor = lightlistitem.getName_kor();
                select_state = lightlistitem.getState();
                select_textview.setText(lightlistitem.getName_kor());
                select_togglebutton.setChecked(false);
                System.out.println("markRoom size : " + markRooms.size());
                for(int i=0; i<markRooms.size();i++){
                    System.out.println("markRooms : " + markRooms.get(i));
                    if(markRooms.get(i).equals(select_room)){
                        select_togglebutton.setChecked(true);
                    }
                }
            }
            @Override
            public void OnLongClick(View view, int position) {

            }
        }));
    }
    private void MarkRoomInput(String room){
        System.out.println("MarkroomInput : " + room);
        for(int i=0;i<markRooms.size();i++){
            if(markRooms.get(i).equals(room)){
                return;
            }
        }
        markRooms.add(room);
    }
    private void MarkRoomOutput(String room){
        System.out.println("MarkroomOutput : " + room);
        for(int i=0;i<markRooms.size();i++){
            if(markRooms.get(i).equals(room))
                markRooms.remove(i);
        }
    }
    private void Light_Data(ArrayList<LightListitem> list){
        recyclerAdapter.clearList();
        for(int i =0;i < list.size(); i++){
            if(list.get(i).getCategory().equals(select_category)){
                recyclerAdapter.addItem(list.get(i));
            }
        }
        recyclerAdapter.notifyDataSetChanged();
    }
    private String DayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        int code = calendar.get(Calendar.DAY_OF_WEEK);
        switch (code){
            case 1:
                return "월";
            case 2:
                return "화";
            case 3:
                return "수";
            case 4:
                return "목";
            case 5:
                return "금";
            case 6:
                return "토";
            case 7:
                return "일";
            default:
                return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESERVEADD_CODE){
            if(resultCode == RESERVEADD_UPLOAD){
                Network network = new Network();//time day name room action
                network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Light_ReserveUpload",
                        data.getStringExtra("time"),data.getStringExtra("day"), data.getStringExtra("name"),
                        data.getStringExtra("room"),data.getStringExtra("roomkor"), data.getStringExtra("action"),data.getStringExtra("repeat"));

                if(data.getStringExtra("day").contains(DayOfWeek()) || data.getStringExtra("repeat").equals("False")){
                    try {
                        MQTT_Sub mqtt = new MQTT_Sub();
                        mqtt.on_publish("reserve");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if(requestCode == 2){//reserve list code for test
            Log.i("light activity","result from reserve list");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("onStart","Light Activity");
        bindService(new Intent(this, MQTT_Main.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop","Light Activity");
        if(mIsBound){
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("onDestory","Light Activity");
    }

    @Override
    public void onBackPressed() {
        Log.i("light activity", "finish");
        Intent intent_result = new Intent();
        intent_result.putExtra("list", lightListitem);
        intent_result.putExtra("roomMark",markRooms);
        setResult(LIGHT_REFRESH, intent_result);
        finish();
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }

    public interface ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private LightActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final LightActivity.ClickListener clickListener) {
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


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.w("light activity","onService Connected");
            mServiceMessenger = new Messenger(iBinder);
            mIsBound = true;
            try {
                Message msg = Message.obtain(null, MQTT_Main.MSG_SEND_TO_LIGHT_ACTIVITY);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("light activity service","disconnected");
            mServiceMessenger = null;
            mIsBound = false;
        }
    };
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case MQTT_Main.MSG_SEND_TO_LIGHT_ACTIVITY:
                    Bundle bundle = msg.getData();
                    Log.w("light activity","here!!");
                    if(bundle.getString("error").equals("yes")){
                        Toast.makeText(getApplicationContext(),"오류가 발생했습니다.",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String sender = bundle.getString("sender");
                        String message = bundle.getString("message");
                        String destination = bundle.getString("destination");
                        Log.w("light activity destination", destination);
                        Log.w("light activity message", message);
                        Log.w("light activity sender", sender);

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
                        for(int i=0;i<lightListitem.size();i++){
                            if(lightListitem.get(i).getName().equals(destination)){
                                lightListitem.get(i).setState(message);
                                select_state = message;
                            }
                        }
                        Light_Data(lightListitem);
                    }
                    break;
            }
            return false;
        }
    }));
    class LightLoadingClass extends AsyncTask<String, Void, String>{
        private ProgressDialog progressDialog;
        private Context context;
        private ArrayList<LightListitem> list;

        public LightLoadingClass(){}
        public LightLoadingClass(Context context, ArrayList<LightListitem> list){
            this.context = context;
            this.list = list;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);

            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

            progressDialog.setMessage("로딩 중 입니다.");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Network network = new Network();
            String result = "";
            try {
                result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Light_State").get();
                System.out.println("light loading : " + result);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject;
                jsonObject = new JSONObject(s);
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
            } catch (Exception e) {
                System.out.println("error in Light JSON : " + e.getMessage());
            }
            Light_Data(list);
            progressDialog.dismiss();
            System.out.println("Loading is end");
        }
    }

}