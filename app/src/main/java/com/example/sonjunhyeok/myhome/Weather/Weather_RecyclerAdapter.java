package com.example.sonjunhyeok.myhome.Weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;

public class Weather_RecyclerAdapter extends RecyclerView.Adapter<Weather_RecyclerAdapter.ItemViewHolder> {
    private ArrayList<Weather_ListItem> listData = new ArrayList<>();
    private Context context;


    @NonNull
    @Override
    public Weather_RecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_search_listitem, viewGroup, false);//weather_search_listitem -> change to Data listitem
        return new Weather_RecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        System.out.println("Recyckler Adapter onBind viewer : "+listData.get(0).getName());
        itemViewHolder.onBind(listData.get(i), i);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public Object getItem(int position){
        return listData.get(position);
    }
    public void addItem(Weather_ListItem listItem){
        listData.add(listItem);
    }
    public void setContext(Context context){
        this.context = context;
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name;
        private TextView textView_data;
        private TextView textView_unit;
        private ImageView imageView;
        private Weather_ListItem listItem;

        private int position;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.nowNameText);
            textView_data = itemView.findViewById(R.id.nowDataText);
            textView_unit = itemView.findViewById(R.id.nowUnitText);
            imageView = itemView.findViewById(R.id.nowImageView);
        }
        public void onBind(Weather_ListItem listItem, int position){
            this.listItem = listItem;
            this.position = position;

            textView_name.setTextSize(5);
            textView_data.setTextSize(10);

            textView_name.setText(listItem.getName());
            textView_data.setText(listItem.getData());

            if (listItem.getStatus() == "습도") {
                textView_unit.setText("%");
                imageView.setImageResource(R.drawable.weather_reh);
            }
            else if (listItem.getStatus() == "풍향") {
                //need to calculate way
                textView_unit.setText("방향");
                imageView.setImageResource(R.drawable.weather_wind_way);
            }
            else if (listItem.getStatus() == "풍속") {
                textView_unit.setText("m/s");
                imageView.setImageResource(R.drawable.weather_windy);
            }
            else if (listItem.getStatus() == "기온") {
                textView_unit.setText("℃");
                imageView.setImageResource(R.drawable.weather_temperature_c);
            }
            else if (listItem.getStatus() == "강수형태") {
                textView_unit.setText("");
                imageView.setImageResource(R.drawable.weather_reh);
            }
            else if (listItem.getStatus() == "강수량") {
                textView_unit.setText("mm");
                imageView.setImageResource(R.drawable.weather_rain);
            }
        }
    }
}
