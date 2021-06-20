package com.example.sonjunhyeok.myhome.Calander;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;

import static com.example.sonjunhyeok.myhome.LoginActivity.myHome;
public class CalenderActivity extends AppCompatActivity {
    ArrayList<Calander_struct> tmp_Cs = new ArrayList<>();

    //MyHome MH_Cal = (MyHome)intent_Main.getSerializableExtra("GV");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        final Intent intent_Main = getIntent();
        final ArrayList<String> alarm_list = new ArrayList<>();
        final ArrayAdapter<String> alarm_adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.cal_alarm_list, alarm_list);
        //CalendarView calv = (CalendarView)findViewById(R.id.calendarView1);
        Button alarm_add, alarm_view;
        ListView alarm_listview = (ListView)findViewById(R.id.listview_alarm);
        final Intent intent_add = new Intent(CalenderActivity.this, Calendar_add_Activity.class);
        alarm_add = (Button)findViewById(R.id.button_alarm_add);
        alarm_view = (Button)findViewById(R.id.button_alarm_view);
        final int[] tmp_year = new int[1];
        final int[] tmp_month = new int[1];
        final int[] tmp_day = new int[1];
        final int start_time;
        final Bundle bundle = new Bundle();

        final MyHome MH_Cal = (MyHome)intent_Main.getSerializableExtra("GV");
        //bundle.putSerializable("GV", MH_Cal);
        /*calv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                tmp_year[0] = year;
                tmp_month[0] = month;
                tmp_day[0] = dayOfMonth;
            }
        });*/
        start_time = tmp_year[0]*1000+tmp_month[0]*100+tmp_day[0];

        alarm_listview.setAdapter(alarm_adapter);

        alarm_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stat_cal = 0;
                intent_add.putExtra("year",tmp_year[0]);
                intent_add.putExtra("month", tmp_month[0]);
                intent_add.putExtra("day", tmp_day[0]);
                intent_add.putExtras(bundle);

                startActivityForResult(intent_add, stat_cal);
                if(stat_cal == 0){
                    onAlarm(start_time, alarm_list,MH_Cal);
                }
                alarm_adapter.notifyDataSetChanged();//adapter refresh
            }
        });
        alarm_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CalenderActivity.this, "Not yet",Toast.LENGTH_LONG).show();
            }
        });
    }
    protected  void onAlarm(int start_time,ArrayList<String> alarm_list,MyHome MH){
        ArrayList<Calander_Time_milestione> tmp_Ctm = new ArrayList<>();
        tmp_Ctm = myHome.Output_Cal(start_time);
       // tmp_Cs = tmp_Ctm;
        //tmp_Cs = myHome.Output_Cal(start_time);
        if(!tmp_Ctm.isEmpty()){
            int size_Cs = tmp_Ctm.size();
            for(int i=0; i<size_Cs;i++){
                if(tmp_Ctm.get(i).Output_Cs().Cal_Output_alarm() == false) {
                    alarm_list.add(tmp_Ctm.get(i).Output_Cs().Cal_Output_Calname());
                    tmp_Ctm.get(i).Output_Cs().Cal_Alarm(true);
                }
            }
        }
        else{
            alarm_list.add("No Calander");
        }

    }
}

