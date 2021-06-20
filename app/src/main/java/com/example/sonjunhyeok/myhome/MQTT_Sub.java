package com.example.sonjunhyeok.myhome;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sonjunhyeok.myhome.MainActivity.user;

public class MQTT_Sub{
    private MqttClient mqttClient_Reserve;
    private String TOPIC_PUB = "MyHome/Light/Pub/Server";
    private String Server_IP = "tcp://server link:1883";

    private String selected_room = null;
    private String category = null;

    public MQTT_Sub(){
        try {
            mqttClient_Reserve = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
            mqttClient_Reserve.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void on_publishInMain(String room, String category, String message){
        this.selected_room = room;
        this.category = category;

        if(selected_room.equals("balcony main")){
            category = "living Room";
        }
        Log.w("mqtt publish",selected_room);
        JSONObject object = new JSONObject();
        try{
            JSONObject tmp = new JSONObject();
            tmp.put("sender",user.Get_name());
            tmp.put("message",message);
            tmp.put("destination",selected_room);
            tmp.put("room",category);
            object.put("Light",tmp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String tmp_str = object.toString();
        try {
            on_publish(tmp_str);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void on_publish(String message) throws MqttException {
        if(!mqttClient_Reserve.isConnected()){
            mqttClient_Reserve = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
            mqttClient_Reserve.connect();
        }
        mqttClient_Reserve.publish(TOPIC_PUB, new MqttMessage(message.getBytes()));
    }
}