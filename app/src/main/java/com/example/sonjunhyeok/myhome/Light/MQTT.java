package com.example.sonjunhyeok.myhome.Light;


import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.sonjunhyeok.myhome.MainActivity.user;

public class MQTT {
    private static final String TAG = "Mqtt_Client";
    private MqttClient mqttClient;
    private String TOPIC_PUB = "MyHome/Light/Pub";
    private String TOPIC_SUB = "MyHome/Light/Result/";

    private String Server_IP = "tcp://sonjuhy.iptime.org:1883";
    private String message = null;
    private String selected_str = null;

    private int selected = 0;

    private Context context;
    public MQTT(Context context){
        this.context = context;
    }

    public void SelectLight(int mode, String mode_str){
        this.selected = mode;
        this.selected_str = mode_str;
    }
    public void Mqtt_Control(boolean mode){//true : on, false : off
        try {
            mqttClient = new MqttClient(Server_IP, MqttClient.generateClientId(), null);
            mqttClient.connect();
            if(mqttClient.isConnected()){
                System.out.println("Out class connected");
            }
            else{
                System.out.println("Out class connected failed");
            }
            switch(selected){
                case 1://living room 거실
                    message="living_room";
                    break;
                case 2://kitchen 부엌
                    message= "kitchen";
                    break;
                case 3://kitchen_table 식탁
                    message= "kitchen_table";
                    break;
                case 4://inner room 안방
                    message= "inner_room";
                    break;
                case 5://middle room 중간방
                    message = "middle_room";
                    break;
                case 6://small room 작은방
                    message = "small room";
                    //message = "test";
                    break;
            }
            TOPIC_PUB += "/"+selected_str;
            //TOPIC_PUB += "/"+"small room";//must change switch firmware(small room -> small Room)
            System.out.println("Topic pub : " + TOPIC_PUB);
            JSONObject object = new JSONObject();
            try{
                JSONObject tmp = new JSONObject();
                tmp.put("sender",user.Get_name());
                if(mode){
                    tmp.put("message","ON");
                }
                else{
                    tmp.put("message","OFF");
                }
                object.put("Light",tmp);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String tmp_str = object.toString();
            mqttClient.subscribe(TOPIC_SUB+user.Get_name());
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println("Message is arrived mqtt : " + mqttMessage.toString());
                    //Toast.makeText(context, "test"+mqttMessage.toString(), Toast.LENGTH_SHORT).show();
                    toasttest(mqttMessage.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    System.out.println("My_Room_Light");
                }
            });
            Mqtt_Pubsich(tmp_str);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void toasttest(String mqttMessage){
        Toast.makeText(context, "test"+mqttMessage, Toast.LENGTH_SHORT).show();
    }
    public void Mqtt_Pubsich(String message){
        try {
            mqttClient.publish(TOPIC_PUB, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
