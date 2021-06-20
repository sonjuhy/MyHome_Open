package com.example.sonjunhyeok.myhome;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.sonjunhyeok.myhome.Notice.NoticeMainActivity;
import static com.example.sonjunhyeok.myhome.MainActivity.user;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MQTT_Main extends Service {
    private static final String TAG = "Mqtt_Client";
    private MqttClient mqttClient_Notice;
    private MqttClient mqttClient_Light;
    private MqttClient mqttClient_Reserve;

    private String TOPIC_PUB = "";
    private String TOPIC_SUB = "";

    private String TOPIC_LIGHT_PUB = "MyHome/Light/Pub/Server";
    private String TOPIC_LIGHT_SUB = "MyHome/Light/Result";
    private String TOPIC_RESERVE_PUB = "MyHome/Light/Reserve/Pub";

    private String TOPIC_NOTICE_SUB = "MyHome/Notice/Sub";
    private String Server_IP = "tcp://server link:1883";
    private String selected_str = null;
    private String category = null;

    private int mode = 0;

    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "notification_channel";
    private NotificationManager manager;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_NOTICE = 4;
    public static final int MSG_SEND_TO_LIGHT = 5;
    public static final int MSG_SEND_TO_LIGHT_ACTIVITY = 6;
    public static final int MSG_SEND_TO_DISCONNECT = 7;

    private Messenger mClient = null;   // Activity 에서 가져온 Messenger
    private Messenger mClient_light = null;

    private Messenger mMessenger = null;

    public MQTT_Main(){
        try {
            mqttClient_Notice = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
            mqttClient_Notice.connect();
            mqttClient_Light = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
            mqttClient_Light.connect();
            mqttClient_Reserve = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
            mqttClient_Reserve.connect();
            if(!mqttClient_Notice.isConnected()){
                sendMsgToActivity(2,null);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void setMQTT(String room, int mode, String category){
        this.selected_str = room;
        this.mode = mode;
        this.category = category;

        TOPIC_PUB = TOPIC_LIGHT_PUB;
        TOPIC_SUB = TOPIC_LIGHT_SUB;
    }
    public void Mqtt_Sub_Notice(){
        try{
            if(!mqttClient_Notice.isConnected()){
                mqttClient_Notice = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
                mqttClient_Notice.connect();
                System.out.println("Mqtt Notice is connected");
            }
            System.out.println("Mqtt Notice is connected else");
            mqttClient_Notice.subscribe(TOPIC_NOTICE_SUB);
            mqttClient_Notice.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Mqtt Notice connect lost");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println("Notice is arrived : " + mqttMessage);
                    sendNotification(mqttMessage.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (MqttException e) {
            Log.w("Mqtt sub notice",e.toString());
            e.printStackTrace();
        }
    }
    public void Mqtt_Sub_Light(){
        try {
            if(!mqttClient_Light.isConnected()){
                mqttClient_Light = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
                mqttClient_Light.connect();
                System.out.println("Mqtt Light is connected");
            }
            mqttClient_Light.subscribe(TOPIC_LIGHT_SUB);
            mqttClient_Light.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("MQTT Light connect lost");
                    sendMsgToActivity(MSG_SEND_TO_DISCONNECT,null);
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println("Message : "+mqttMessage.toString()+" | mode : " + mode);
                    ArrayList<String> parsing = JSON_Parsing_Light(mqttMessage.toString());
                    if(mode == MSG_SEND_TO_LIGHT){
                        sendMsgToActivity(MSG_SEND_TO_LIGHT, parsing);
                    }
                    else if(mode == MSG_SEND_TO_LIGHT_ACTIVITY){
                        sendMsgToActivity(MSG_SEND_TO_LIGHT_ACTIVITY, parsing);
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        } catch (MqttException e) {
            Log.w("Mqtt sub light",e.toString());
            e.printStackTrace();
        }
    }
    public void Mqtt_Publish(int mode, String message){
        try {
            switch (mode){
                case 1://room light
                    if(selected_str.equals("balcony main")){
                        category = "living Room";
                    }
                    Log.w("mqtt publish",selected_str);
                    JSONObject object = new JSONObject();
                    try{
                        JSONObject tmp = new JSONObject();
                        tmp.put("sender",user.Get_name());
                        tmp.put("message",message);
                        tmp.put("destination",selected_str);
                        tmp.put("room",category);
                        object.put("Light",tmp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String tmp_str = object.toString();
                    Log.w("mqtt pub",tmp_str);
                    Log.w("mqtt pub", TOPIC_PUB);
                    mqttClient_Light.publish(TOPIC_PUB, new MqttMessage(tmp_str.getBytes()));
                    break;
                case 2://notice pub
                    mqttClient_Notice.publish(TOPIC_NOTICE_SUB, new MqttMessage(message.getBytes()));
                    break;
                case 3://light reserve
                    System.out.println("light resrve mqtt pub : " + TOPIC_LIGHT_PUB);
                    mqttClient_Reserve.publish(TOPIC_LIGHT_PUB, new MqttMessage(message.getBytes()));
                    break;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> JSON_Parsing_Light(String message){
        String sender, state, destination;
        ArrayList<String> parsing = new ArrayList<>();
        parsing.add("no data");
        try {
            JSONObject object = new JSONObject(message);
            sender = object.getString("sender");
            state = object.getString("message");
            destination = object.getString("room");
            parsing = new ArrayList<>();
            parsing.add(sender);
            parsing.add(state);
            parsing.add(destination);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parsing;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //return null;
        mMessenger = new Messenger(new ServiceHandler());
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("onCreate","start service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            Log.w("onStartCommand","intent is null");
            return Service.START_REDELIVER_INTENT;
        }
        else{
            System.out.println("onCreate in Service");
            createNotificationChannel();
            Mqtt_Sub_Notice();
            Mqtt_Sub_Light();
        }
        System.out.println("onStartCommand in Service");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void sendNotification(String content){
        NotificationCompat.Builder builder = getNotificationBuidler(content);
        manager.notify(NOTIFICATION_ID, builder.build());
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add("no data");
        sendMsgToActivity(MSG_SEND_TO_NOTICE,null);

    }
    public void createNotificationChannel(){
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "test", manager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Noti from Mascot");

            manager.createNotificationChannel(notificationChannel);
        }
    }
    private NotificationCompat.Builder getNotificationBuidler(String content){
        Intent nofi_intent = new Intent(this, NoticeMainActivity.class);
        PendingIntent noti_pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, nofi_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("새로 등록된 공지")
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(noti_pendingIntent)
                .setAutoCancel(true);
        return builder;
    }

    private void sendMsgToActivity(int sendValue, ArrayList<String> parsing) {
        try{
            Log.i("sendMSGtoActivity","here");
            Bundle bundle = new Bundle();
            Message msg;
            if(parsing.get(0).equals("no data")){
                Log.w("Mqtt Parsing","error");
                bundle.putString("error","yes");
            }
            else{
                bundle.putString("error","no");
                bundle.putString("sender",parsing.get(0));
                bundle.putString("message",parsing.get(1));
                bundle.putString("destination",parsing.get(2));
            }
            switch (sendValue){
                case MSG_SEND_TO_LIGHT:
                    msg = Message.obtain(null, MSG_SEND_TO_LIGHT);
                    break;
                case MSG_SEND_TO_LIGHT_ACTIVITY:
                    msg = Message.obtain(null, MSG_SEND_TO_LIGHT_ACTIVITY);
                    break;
                case MSG_SEND_TO_DISCONNECT:
                    msg = Message.obtain(null, MSG_SEND_TO_DISCONNECT);
                    break;
                case MSG_SEND_TO_NOTICE:
                    msg = Message.obtain(null, MSG_SEND_TO_NOTICE);
                    bundle = new Bundle();
                    bundle.putString("key","data");
                    break;
                default:
                    return;
            }
            msg.setData(bundle);
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public class ServiceHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_SEND_TO_LIGHT:
                    mMessenger = msg.replyTo;
                    mode = MSG_SEND_TO_LIGHT;
                    break;
                case MSG_SEND_TO_LIGHT_ACTIVITY:
                    mMessenger = msg.replyTo;
                    mode = MSG_SEND_TO_LIGHT_ACTIVITY;
                    break;
                case MSG_SEND_TO_NOTICE:
                    mMessenger = msg.replyTo;
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }
}
