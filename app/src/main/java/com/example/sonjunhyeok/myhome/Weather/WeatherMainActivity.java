package com.example.sonjunhyeok.myhome.Weather;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.MainActivity;
import com.example.sonjunhyeok.myhome.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import static com.example.sonjunhyeok.myhome.LoadingActivity.weather_class;

public class WeatherMainActivity extends AppCompatActivity {
    private LineChart lineChart;

    private ArrayList<Weather_ListItem> finalWeather_listItems = null;
    private RecyclerView now_recyclerView;
    private ArrayList<Weather_ListItem> weather_listItems = null;
    private ArrayList<Weather_Data> weather_data_UltraFcst = null;
    private ArrayList<Weather_Data> weather_data_VilageFcst = null;
    private ArrayList<String> location_history = null;

    private TextView textView_temperature,textView_sky,textView_place;
    private ImageView imageView_sky;

    private final int SEARCH_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);

        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_weather);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        now_recyclerView = findViewById(R.id.weather_now_Recycler);
        textView_temperature = findViewById(R.id.weather_title_NowTemperature_textview);
        textView_sky = findViewById(R.id.weather_title_sky_textview);
        textView_place = findViewById(R.id.weather_title_place_textview);
        imageView_sky = findViewById(R.id.weather_title_sky_imageview);

        now_recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        WeatherLoadingClass loading = new WeatherLoadingClass(this);
        loading.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.weather_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.weather_search_button:
                Intent intent_search = new Intent(WeatherMainActivity.this, WeatherPlaceSearchActivity.class);
                startActivityForResult(intent_search, SEARCH_CODE);
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_CODE){
            if(resultCode == 0){
                String tmp_place = "";
                location_history = data.getStringArrayListExtra("location_history");
                Weather_Data tmp = weather_class.Get_Weather_UtlraNcst();
                weather_data_VilageFcst = weather_class.Get_Weather_VilageFcst();

                weather_listItems = this.UltraNcstToList(tmp);

                finalWeather_listItems = weather_listItems;
                textView_temperature.setText(tmp.getT1H());
                for(int i=0;i<location_history.size();i++){
                    tmp_place += location_history.get(i) + " ";
                }
                System.out.println("tmp place : " + tmp_place);
                textView_place.setText(tmp_place);
                this.Now_RecyclerApdaterSet();
                this.SetWeather_Day_Chart();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }

    private void Now_RecyclerApdaterSet(){
        now_recyclerView.setAdapter(new RecyclerView.Adapter<WeatherNowViewHolder>() {
            @Override
            public WeatherNowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = View.inflate(getApplicationContext(), R.layout.weather_now_itemlist, null);
                return new WeatherNowViewHolder(view);
            }

            @Override
            public void onBindViewHolder(WeatherNowViewHolder holder, int position) {
                //holder.textView.setText(testStrings.get(position));
                holder.textNameView.setText(finalWeather_listItems.get(position).getName());
                if("습도".equals(finalWeather_listItems.get(position).getStatus())){
                    holder.imageView.setImageResource(R.drawable.weather_reh);
                }
                else if("풍향".equals(finalWeather_listItems.get(position).getStatus())){
                    holder.imageView.setImageResource(R.drawable.weather_wind_way);
                }
                else if("풍속".equals(finalWeather_listItems.get(position).getStatus())){
                    holder.imageView.setImageResource(R.drawable.weather_windy);
                }
                else if("기온".equals(finalWeather_listItems.get(position).getStatus())){
                    holder.imageView.setImageResource(R.drawable.weather_temperature_c);
                }
                else if("강수형태".equals(finalWeather_listItems.get(position).getStatus())){
                    holder.imageView.setImageResource(R.drawable.weather_cloudy);
                }
                else if("1시간강수량".equals(finalWeather_listItems.get(position).getStatus())){
                    holder.imageView.setImageResource(R.drawable.weather_rain);
                }
                holder.textUnitView.setText(finalWeather_listItems.get(position).getUnit());
                holder.textDataView.setText(finalWeather_listItems.get(position).getData());
            }
            public int getItemCount() {
                return finalWeather_listItems.size();
            }
        });
    }
    private ArrayList<Weather_ListItem> UltraNcstToList(Weather_Data weather_data){
        ArrayList<Weather_ListItem> list = new ArrayList<>();
        Weather_ListItem item = new Weather_ListItem();
        //습도
        item.setStatus("습도");
        item.setUnit("%");
        item.setName("습도");
        item.setData(weather_data.getREH());
        list.add(item);
        //기온
        item = new Weather_ListItem();
        item.setStatus("기온");
        item.setUnit("℃");
        item.setName("기온");
        item.setData(weather_data.getT1H());
        list.add(item);
        //강수형태
        item = new Weather_ListItem();
        item.setStatus("강수형태");
        item.setUnit("");
        item.setName("비/눈");
        item.setData(weather_data.getPTY());
        list.add(item);
        //풍향
        item = new Weather_ListItem();
        item.setStatus("풍향");
        item.setUnit("방향");
        item.setName("풍향");
        item.setData(weather_data.getVEC());
        list.add(item);
        //풍속
        item = new Weather_ListItem();
        item.setStatus("풍속");
        item.setUnit("m/s");
        item.setName("풍속");
        item.setData(weather_data.getWSD());
        list.add(item);
        //강수량
        item = new Weather_ListItem();
        item.setStatus("1시간강수량");
        item.setUnit("mm");
        item.setName("강수량");
        item.setData(weather_data.getRN1());
        list.add(item);

        return list;
    }

    private void SetWeather_Day_Chart(){
        final MyHandler myHandler = new MyHandler();
        final List<Entry> entries = new ArrayList<>();
        System.out.println("set weather : " + weather_data_VilageFcst.size());
        final String[] values = new String[weather_data_VilageFcst.size()];
        for(int i=0;i<weather_data_VilageFcst.size();i++){
            values[i] = weather_data_VilageFcst.get(i).getFcstTime();
            float y = Float.parseFloat(weather_data_VilageFcst.get(i).getT3H());
            entries.add(new Entry(i,y));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        lineChart = (LineChart)findViewById(R.id.weather_day_chart);

                        LineDataSet lineDataSet = new LineDataSet(entries, "시간별 온도");
                        lineDataSet.setLineWidth(2);
                        lineDataSet.setCircleRadius(6);
                        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
                        lineDataSet.setCircleColorHole(Color.BLUE);
                        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
                        lineDataSet.setDrawCircleHole(false);
                        lineDataSet.setDrawCircles(true);
                        lineDataSet.setDrawHorizontalHighlightIndicator(false);
                        lineDataSet.setDrawHighlightIndicators(false);
                        lineDataSet.setDrawValues(true);
                        lineDataSet.setValueTextSize(15.0f);
                        lineDataSet.setDrawFilled(true);

                        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

                        LineData lineData = new LineData(lineDataSet);
                        lineChart.setData(lineData);

                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(Color.BLACK);
                        xAxis.setGridColor(Color.WHITE);
                        xAxis.setValueFormatter(new AxisValueFormatter(values));
                        //xAxis.enableGridDashedLine(8, 24, 0);

                        YAxis yLAxis = lineChart.getAxisLeft();
                        yLAxis.setTextColor(Color.WHITE);
                        yLAxis.setGridColor(Color.WHITE);

                        YAxis yRAxis = lineChart.getAxisRight();
                        yRAxis.setDrawLabels(false);
                        yRAxis.setDrawAxisLine(false);
                        yRAxis.setDrawGridLines(false);

                        Description description = new Description();
                        description.setText("");

                        lineChart.setDoubleTapToZoomEnabled(false);
                        lineChart.setVisibleXRangeMaximum(6);
                        lineChart.setDrawGridBackground(false);
                        lineChart.setTouchEnabled(true);
                        lineChart.setDragEnabled(true);
                        lineChart.setScaleEnabled(true);
                        lineChart.setDescription(description);
                        lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
                        lineChart.invalidate();
                    }
                });
            }
        }).start();

    }
    private class AxisValueFormatter implements IAxisValueFormatter{
        private String[] mValues;

        public AxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }
    public static class WeatherNowViewHolder extends RecyclerView.ViewHolder {
        public TextView textNameView, textDataView, textUnitView;
        public ImageView imageView;
        public WeatherNowViewHolder(View itemView) {
            super(itemView);
            textNameView = itemView.findViewById(R.id.nowNameText);
            textDataView = itemView.findViewById(R.id.nowDataText);
            textUnitView = itemView.findViewById(R.id.nowUnitText);
            imageView = itemView.findViewById(R.id.nowImageView);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
    public static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1000) {
                System.out.println("Handler in what == 1000");
                //sResultTextView.setText("Handler sendEmptyMessage()瑜� �넻�븳 handleMessage() �떎�뻾");
            }
        }
    }
    private class WeatherLoadingClass extends AsyncTask<String,Void,String>{
        private ProgressDialog progressDialog;
        private Context context;

        public WeatherLoadingClass(Context context){
            this.context = context;
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
            weather_class.Weather_Search_Direct(91,76);
            Weather_Data UltraNcst = weather_class.Get_Weather_UtlraNcst();
            weather_data_UltraFcst = weather_class.Get_Weather_UtlraFcst();
            weather_data_VilageFcst = weather_class.Get_Weather_VilageFcst();

            if(UltraNcst == null){
                return null;
            }
            weather_listItems = UltraNcstToList(UltraNcst);

            finalWeather_listItems = weather_listItems;
            //System.out.println("vilage size in doinbackground : " + weather_data_VilageFcst.size());

            return UltraNcst.getT1H();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Now_RecyclerApdaterSet();
            //System.out.println("vilage size in postexecute : " + weather_data_VilageFcst.size());

            if(s == null){
                textView_temperature.setText("--");
                textView_sky.setText("--");
                textView_place.setText("--");
            }
            else {
                textView_temperature.setText(s);
                textView_sky.setText(weather_data_VilageFcst.get(0).getSKY());
                textView_place.setText("경상남도 창원시 사파동");
                SetWeather_Day_Chart();
            }
            imageView_sky.setImageResource(R.drawable.moon);
            progressDialog.dismiss();
            System.out.println("Loading is end");
        }
    }
}
