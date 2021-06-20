package com.example.sonjunhyeok.myhome.Light;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

public class LightReserveAddActivity extends AppCompatActivity {

    private RadioGroup radio_power;
    private ToggleButton toggle_sun, toggle_mon, toggle_tus, toggle_wen,
            toggle_thu, toggle_fri, toggle_set;
    private Switch switch_repeat;
    private LinearLayout daylayout;
    private TimePicker timePicker;
    private Button button_cancel, button_save;
    private EditText edittext_name;
    private TextView textview_roomName;

    private String resultStr_power="On", resultStr_repeat="False", resultStr_day="", resultStr_name;
    private String[] resultStrDays = {"","","","","","",""};
    private int resultInt_hour=0, resultInt_min=0;

    private String roomName_eng, roomName_kor;

    public final static int RESERVEADD_CODE = 1;
    public final static int RESERVEADD_UPLOAD = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_reserve_add);

        Toolbar toolbar = findViewById(R.id.light_reserveAdd_toolbar);
        setSupportActionBar(toolbar);

        Intent getIntent = getIntent();
        roomName_eng = getIntent.getStringExtra("Room_eng");
        roomName_kor = getIntent.getStringExtra("Room_kor");

        daylayout = findViewById(R.id.light_reserveAdd_dayLayout);
        switch_repeat = findViewById(R.id.light_reserveAdd_togglerepeat);
        radio_power = findViewById(R.id.light_reserveAdd_toggle);
        timePicker = findViewById(R.id.light_reserveAdd_timepicker);
        button_cancel = findViewById(R.id.lightAdd_room_cancel_button);
        button_save = findViewById(R.id.lightAdd_room_save_button);
        edittext_name = findViewById(R.id.light_reserveAdd_edittext);
        textview_roomName = findViewById(R.id.light_reserveAdd_room_textview);
        toggle_mon = findViewById(R.id.toggle_mon);
        toggle_tus = findViewById(R.id.toggle_tus);
        toggle_wen = findViewById(R.id.toggle_wen);
        toggle_thu = findViewById(R.id.toggle_thu);
        toggle_fri = findViewById(R.id.toggle_fri);
        toggle_set = findViewById(R.id.toggle_set);
        toggle_sun = findViewById(R.id.toggle_sun);

        textview_roomName.setText(roomName_kor);

        radio_power.setOnCheckedChangeListener(radiogroupChangeListener);
        switch_repeat.setOnCheckedChangeListener(switchChangeListener);
        {
            toggle_mon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        resultStrDays[0] = "월,";
                    } else {
                        resultStrDays[0] = "";
                    }
                }
            });
            toggle_tus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        resultStrDays[1] = "화,";
                    } else {
                        resultStrDays[1] = "";
                    }
                }
            });
            toggle_wen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        resultStrDays[2] = "수,";
                    } else {
                        resultStrDays[2] = "";
                    }
                }
            });
            toggle_thu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        resultStrDays[3] = "목,";
                    } else {
                        resultStrDays[3] = "";
                    }
                }
            });
            toggle_fri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        resultStrDays[4] = "금,";
                    } else {
                        resultStrDays[4] = "";
                    }
                }
            });
            toggle_set.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        resultStrDays[5] = "토,";
                    } else {
                        resultStrDays[5] = "";
                    }
                }
            });
            toggle_sun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        resultStrDays[6] = "일";
                    } else {
                        resultStrDays[6] = "";
                    }
                }
            });
        }

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultStr_name = edittext_name.getText().toString();
                if(resultStr_name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"이름을 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    resultInt_hour = timePicker.getHour();
                    resultInt_min = timePicker.getMinute();
                    String mintmp ="";
                    if(resultInt_min < 10){
                        mintmp = "0"+resultInt_min;
                    }
                    else{
                        mintmp = String.valueOf(resultInt_min);
                    }
                    String resultStr_time = resultInt_hour+":"+mintmp;
                    if(resultStr_repeat.equals("True")) {
                        for (int i = 0; i < 7; i++) {
                            resultStr_day += resultStrDays[i];
                        }
                    }
                    else{
                        resultStr_day = "No data";
                    }
                    Intent return_intent = new Intent();
                    return_intent.putExtra("action", resultStr_power);
                    return_intent.putExtra("room", roomName_eng);
                    return_intent.putExtra("roomkor", roomName_kor);
                    return_intent.putExtra("name", resultStr_name);
                    return_intent.putExtra("repeat",resultStr_repeat);
                    return_intent.putExtra("day",resultStr_day);
                    return_intent.putExtra("time",resultStr_time);
                    setResult(RESERVEADD_UPLOAD, return_intent);
                    finish();
                }
            }
        });
    }
    Switch.OnCheckedChangeListener switchChangeListener = new Switch.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){//checked
                daylayout.setVisibility(LinearLayout.VISIBLE);
                resultStr_repeat = "True";
            }
            else{
                daylayout.setVisibility(LinearLayout.INVISIBLE);
                resultStr_repeat = "False";
            }
        }
    };
    RadioGroup.OnCheckedChangeListener radiogroupChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.toggle_off:
                    resultStr_power = "OFF";
                    break;
                case R.id.toggle_on:
                    resultStr_power = "ON";
                    break;
            }
        }
    };
}