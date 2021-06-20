package com.example.sonjunhyeok.myhome.Calander;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.sonjunhyeok.myhome.R;

import java.util.Calendar;

public class Calendar_add_Activity extends AppCompatActivity {
    private int year, month, day, time_start_year, time_start_month, time_start_day, time_start_hour, time_start_minute;
    private TextView TV_day, TV_date_start, TV_date_end, TV_time_start, TV_time_end;
    private Button BT_save, BT_cancel;
    private EditText ET_name, ET_content;
    final Calendar cal = Calendar.getInstance();
    private User_class tmp_User = new User_class();
    private Calander_struct tmp_Cal = new Calander_struct();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add_);
        Intent intent_Cal_add = getIntent();
        final MyHome MH_Cal_add = (MyHome)intent_Cal_add.getSerializableExtra("GV");
        //final int[] start_time = new int[5] ,end_time = new int[5];
        final int[][] tmp = {new int[4]};

        year = getIntent().getIntExtra("year",-1);
        month = getIntent().getIntExtra("month",-2);
        day = getIntent().getIntExtra("day",-1);

        TV_day = (TextView)findViewById(R.id.Cal_day);
        TV_date_start =(TextView)findViewById(R.id.Cal_start_date);
        TV_date_end = (TextView)findViewById(R.id.Cal_end_date);
        TV_time_start = (TextView)findViewById(R.id.Cal_start_time);
        TV_time_end = (TextView)findViewById(R.id.Cal_end_time);

        BT_save = (Button)findViewById(R.id.Cal_save);
        BT_cancel = (Button)findViewById(R.id.Cal_cancel);

        ET_name = (EditText)findViewById(R.id.Cal_name);
        ET_content = (EditText)findViewById(R.id.Cal_content);

        TV_day.setText(String.valueOf(year)+" 년"+String.valueOf(month+1)+" 월"+String.valueOf(day)+" 일");

        TV_date_start.setText(cal.get(Calendar.YEAR)+" 년 "+cal.get(Calendar.MONTH+1)+" 월 "+cal.get(Calendar.DATE)+" 일 ");
        TV_date_end.setText(cal.get(Calendar.YEAR)+" 년 "+cal.get(Calendar.MONTH+1)+" 월 "+cal.get(Calendar.DATE)+" 일 ");
        TV_time_start.setText(cal.get(Calendar.HOUR_OF_DAY)+" 시 "+cal.get(Calendar.MINUTE)+" 분 ");
        TV_time_end.setText(cal.get(Calendar.HOUR_OF_DAY)+" 시 "+cal.get(Calendar.MINUTE)+" 분 ");

        TV_date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp[0] = Date_start_set();
            }
        });
        TV_date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp[1] = Date_end_set();
            }
        });
        TV_time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp[2] = Time_start_set();
            }
        });
        TV_time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp[3] = Time_end_set();
            }
        });
        BT_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp_Cal.Cal_insert_Starttime(tmp[0][0],tmp[0][1],tmp[0][2],tmp[2][0],tmp[2][1]);
                tmp_Cal.Cal_insert_Endtime(tmp[1][0],tmp[1][1],tmp[1][2],tmp[3][0],tmp[3][0]);
                tmp_Cal.Cal_insert_String(ET_name.getText().toString(),ET_content.getText().toString());

                MH_Cal_add.Input_Cal(tmp_Cal);
                MH_Cal_add.Output_User().Cal_Insert(tmp_Cal);


                setResult(0);
                finish();
            }
        });
        BT_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
    }
    protected int[] Date_start_set(){
        final int[] tmp = new int[3];
        DatePickerDialog datePickerDialog = new DatePickerDialog(Calendar_add_Activity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tmp[0] = year;
                tmp[1] = month;
                tmp[2] = dayOfMonth;
                TV_date_start.setText(String.valueOf(year) + "년" + String.valueOf(month + 1) + "월" + String.valueOf(dayOfMonth) + "일");
            }}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        datePickerDialog.show();

        return tmp;
    }
    protected  int[] Date_end_set(){
        final int[] tmp = new int[3];
        DatePickerDialog datePickerDialog = new DatePickerDialog(Calendar_add_Activity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tmp[0] = year;
                tmp[1] = month;
                tmp[2] = dayOfMonth;
                TV_date_end.setText(String.valueOf(year)+"년"+String.valueOf(month+1)+"월"+String.valueOf(dayOfMonth)+"일");
            }}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        datePickerDialog.show();
        return tmp;
    }
    protected int[] Time_start_set() {
        final int[] tmp = new int[2];
        TimePickerDialog timePickerDialog = new TimePickerDialog(Calendar_add_Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tmp[0] = hourOfDay;
                tmp[1] = minute;
                TV_time_start.setText(String.valueOf(hourOfDay) + "시" + String.valueOf(minute) + "분");
            }}, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        timePickerDialog.show();

        return tmp;
    }
    protected  int[] Time_end_set(){
        final int[] tmp = new int[2];
        TimePickerDialog timePickerDialog = new TimePickerDialog(Calendar_add_Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                tmp[0] = hourOfDay;
                tmp[1] = minute;
                TV_time_end.setText(String.valueOf(hourOfDay) + "시" + String.valueOf(minute) + "분");
            }}, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        timePickerDialog.show();

        return tmp;
    }
}
