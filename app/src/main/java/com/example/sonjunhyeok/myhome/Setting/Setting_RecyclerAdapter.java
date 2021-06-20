package com.example.sonjunhyeok.myhome.Setting;

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

public class Setting_RecyclerAdapter extends RecyclerView.Adapter<Setting_RecyclerAdapter.ItemViewHolder>{
    private Context context;
    private ArrayList<Setting_Listitem> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_setting, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        System.out.println("Setting Adapter onBind viewer : "+listData.get(0).getName());
        itemViewHolder.onBind(listData.get(i), i);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public Object getItem(int posision){
        return listData.get(posision);
    }
    public void addItem(Setting_Listitem listItem){
        listData.add(listItem);
    }
    public void setContext(Context context){
        this.context = context;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name;
        private ImageView imageView;
        private Setting_Listitem listItem;
        private int position;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.SettingListItem_name);
            imageView = itemView.findViewById(R.id.SettingListItem_image);
        }
        public void onBind(Setting_Listitem listItem_input, int position_input){
            this.listItem = listItem_input;
            this.position = position_input;

            textView_name.setText(listItem.getName());
            System.out.println("setting recycler view item name : "+listItem_input.getName());
            switch (listItem.getType()){
                case 1:
                    imageView.setImageResource(R.drawable.account);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.login);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.download);
                    break;
                case 11:
                    imageView.setImageResource(R.drawable.unlinkautologin);
                    break;
                case 12:
                    imageView.setImageResource(R.drawable.logout);
                    break;
            }
        }
    }
}
