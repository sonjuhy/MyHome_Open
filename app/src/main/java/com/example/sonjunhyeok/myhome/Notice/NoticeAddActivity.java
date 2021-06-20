package com.example.sonjunhyeok.myhome.Notice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.MQTT_Main;
import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.sonjunhyeok.myhome.MainActivity.user;

public class NoticeAddActivity extends AppCompatActivity {
    private TextView textView_name, textView_content;
    private Button button_ok, button_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_add);

        textView_name = findViewById(R.id.notice_add_edit_name);
        textView_content = findViewById(R.id.notice_add_edit_content);
        button_ok = findViewById(R.id.notice_add_button_OK);
        button_cancel = findViewById(R.id.notice_add_button_Cancel);

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name="", content="";
                name = textView_name.getText().toString();
                content = textView_content.getText().toString();
                System.out.println("name in add : " + name);
                System.out.println("content in add : " + content);
                if(name.length() > 32){
                    Toast.makeText(getApplicationContext(), "제목은 16자를 넘을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else if("".equals(name)){
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if("".equals(content)){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(content.length() > 100){
                    Toast.makeText(getApplicationContext(), "내용은 50자를 넘을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    MQTT_Main mqtt = new MQTT_Main();
                    mqtt.Mqtt_Publish(2, name);
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date time = new Date();
                    String time_str = format.format(time);
                    System.out.println("time in add : " + time_str);
                    Network network = new Network();
                    network.execute("Set_Notice",time_str,content, user.Get_name(), name);
                    Toast.makeText(getApplicationContext(), "공지를 등록했습니다.", Toast.LENGTH_SHORT).show();
                    try{
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent_result = new Intent();
                    intent_result.putExtra("Title", name);
                    intent_result.putExtra("Content", content);
                    intent_result.putExtra("Time", time_str);
                    //input
                    setResult(101, intent_result);//refresh
                    finish();
                    overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }
}