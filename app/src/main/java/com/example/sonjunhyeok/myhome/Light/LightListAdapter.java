package com.example.sonjunhyeok.myhome.Light;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;

public class LightListAdapter extends RecyclerView.Adapter<LightListAdapter.ItemViewHolder> {
    private ArrayList<LightListitem> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.light_listitem, viewGroup, false);
        view.setAlpha(0.5f);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.onBind(listData.get(i), i);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public Object getItem(int position){
        return listData.get(position);
    }
    public void addItem(LightListitem listItem){
        listData.add(listItem);
    }
    public void clearList(){
        listData = new ArrayList<>();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name;
        private ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.light_list_nameText);
            imageView = itemView.findViewById(R.id.light_list_imageView);
            //imageView.setAlpha(1f);
        }
        public void onBind(LightListitem listItem, int postition){
            System.out.println("test light : " + listItem.getName());
            textView_name.setText(listItem.getName_kor());
            if("On".equals(listItem.getState())){
                imageView.setImageResource(R.drawable.light_on);
            }
            else {
                imageView.setImageResource(R.drawable.light);
            }
            imageView.setAlpha(1f);
        }
    }
}
