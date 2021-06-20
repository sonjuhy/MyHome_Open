package com.example.sonjunhyeok.myhome.Weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;

public class WeatherPlace_RecyclerAdapter extends RecyclerView.Adapter<WeatherPlace_RecyclerAdapter.ItemViewHolder>  {
    private ArrayList<Weather_Place_ListItem> listData = new ArrayList<>();
    private Context context;
    @NonNull
    @Override
    public WeatherPlace_RecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_search_listitem, viewGroup, false);
        return new WeatherPlace_RecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherPlace_RecyclerAdapter.ItemViewHolder itemViewHolder, int i) {
        System.out.println("Weather place Recycler Adapter onBind viewer : "+listData.get(0).getName());
        itemViewHolder.onBind(listData.get(i), i);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public Object getItem(int position){
        return listData.get(position);
    }
    public void addItem(Weather_Place_ListItem listItem){
        listData.add(listItem);
    }
    public void setContext(Context context){
        this.context = context;
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name;
        private Weather_Place_ListItem listItem;

        private int position;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.nameText);
        }
        public void onBind(Weather_Place_ListItem listItem, int position){
            this.listItem = listItem;
            this.position = position;

            textView_name.setText(listItem.getName());
            textView_name.setTextSize(20);
            textView_name.setText(this.listItem.getName());
        }
    }
}
