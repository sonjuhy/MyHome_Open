package com.example.sonjunhyeok.myhome.Weather;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

import static com.example.sonjunhyeok.myhome.LoadingActivity.weather_class;

import java.util.ArrayList;

public class WeatherPlaceSearchActivity extends AppCompatActivity {
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private WeatherPlace_RecyclerAdapter recyclerAdapter;

    private TextView textView_location;

    private static String Location_before = "0";
    private final int RESULT_CODE = 0;
    private ArrayList<String> location_history = new ArrayList<>();
    private ArrayList<String> location_history_name = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_plcae_search);

        gridLayoutManager = new GridLayoutManager(WeatherPlaceSearchActivity.this, 5);
        recyclerView = findViewById(R.id.weather_recyclerView_bottom);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerAdapter = new WeatherPlace_RecyclerAdapter();
        recyclerAdapter.setContext(this);
        System.out.println("WeatherSearch activity");

        textView_location = findViewById(R.id.weather_subView);

        weather_class.setRecycler(recyclerView, recyclerAdapter);

        weather_class.Weather_Search("frist",true, recyclerAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Weather_Place_ListItem ListItem = (Weather_Place_ListItem)recyclerAdapter.getItem(position);
                recyclerAdapter = new WeatherPlace_RecyclerAdapter();
                recyclerAdapter.setContext(WeatherPlaceSearchActivity.this);
                int result_code = 0;
                Intent intent_result = new Intent();
                if("위로".equals(ListItem.getName())){
                    System.out.println("Click 위로 : "+Location_before);
                    location_history.remove(location_history.size()-1);
                    location_history_name.remove(location_history_name.size() - 1);
                    if(location_history.size() == 0){
                        Location_before = "0";
                    }else {
                        Location_before = location_history.get(location_history.size() - 1);
                    }
                    weather_class.Weather_Search(Location_before, false, recyclerAdapter);
                }
                else{
                    Location_before = ListItem.getCode();
                    textView_location.setText(ListItem.getName());
                    location_history.add(Location_before);
                    location_history_name.add(ListItem.getName());
                    System.out.println("location before : " +Location_before);
                    result_code = weather_class.Weather_Search(Location_before, true, recyclerAdapter);
                    //if select leaf
                    if(result_code == 1) {
                        intent_result.putExtra("location_history", location_history_name);
                        setResult(RESULT_CODE, intent_result);
                        finish();
                    }
                }
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        }));

    }
    private interface  ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    private static class RecyclerTouchListener implements  RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private WeatherPlaceSearchActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final WeatherPlaceSearchActivity.ClickListener clickListener) {
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
}