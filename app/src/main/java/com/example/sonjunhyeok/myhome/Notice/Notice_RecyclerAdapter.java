package com.example.sonjunhyeok.myhome.Notice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;

public class Notice_RecyclerAdapter extends RecyclerView.Adapter<Notice_RecyclerAdapter.ItemViewHolder> {
    private ArrayList<Notice_ListItem> listData = new ArrayList<>();
    private Context context;


    @NonNull
    @Override
    public Notice_RecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_itemlist, viewGroup, false);
        return new Notice_RecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        //System.out.println("Recyckler Adapter onBind viewer : "+listData.get(0).getName());
        itemViewHolder.onBind(listData.get(i), i);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public void removeItem(int position){listData.remove(position);}
    public int getItemPosition(String num){
        int result = -1;
        for(int i=0;i<listData.size();i++){
            if(listData.get(i).getNum().equals(num)){
                result = i;
            }
        }
        return result;
    }
    public Object getItem(int position){
        return listData.get(position);
    }
    public void addItem(Notice_ListItem listItem){
        listData.add(listItem);
    }
    public void setContext(Context context){
        this.context = context;
    }
    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name;
        private TextView textView_data;
        private TextView textView_time;
        private Notice_ListItem listItem;

        private int position;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.notice_writercontent);
            textView_data = itemView.findViewById(R.id.notice_contentview);
            textView_time = itemView.findViewById(R.id.notice_timecontent);
        }
        public void onBind(Notice_ListItem listItem, int position){
            this.listItem = listItem;
            this.position = position;

            /*textView_name.setTextSize(5);
            textView_data.setTextSize(10);*/

            textView_name.setText(listItem.getName());
            textView_data.setText(listItem.getTitle());
            textView_time.setText(listItem.getTime());
        }
    }
}
