package com.example.sonjunhyeok.myhome.FileServer;

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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private ArrayList<ListItem> listData = new ArrayList<>();
    private Context context;

    private int prePosition = -1;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ItemViewHolder(view);
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
    public void addItem(ListItem listItem){
        listData.add(listItem);
    }
    public void setContext(Context context){
        this.context = context;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name;
        private TextView textView_size;
        private ImageView imageView;
        private ListItem listItem;

        private int position;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.nameText);
            textView_size = itemView.findViewById(R.id.sizeText);
            imageView = itemView.findViewById(R.id.imageView);
        }
        public void onBind(ListItem listItem, int position){
            this.listItem = listItem;
            this.position = position;

            textView_size.setText(listItem.getSize());
            textView_name.setText(listItem.getName());

            if(listItem.getType()){//folder
                if(listItem.getName().equals("위로가기")){
                    imageView.setImageResource(R.drawable.upside);
                }
                else if(listItem.getName().equals("휴지통")){
                    imageView.setImageResource(R.drawable.recyclebin);
                }
                else if(listItem.getName().equals("복원")){
                    imageView.setImageResource(R.drawable.recycle);
                }
                else {
                    imageView.setImageResource(R.drawable.folder240);
                }
            }
            else{//file
                imageView.setImageResource(R.drawable.file240);
            }
        }
    }
}
