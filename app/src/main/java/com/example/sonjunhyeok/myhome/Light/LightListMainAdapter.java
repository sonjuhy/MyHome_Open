package com.example.sonjunhyeok.myhome.Light;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;

public class LightListMainAdapter extends RecyclerView.Adapter<LightListMainAdapter.ItemViewHolder>{
    private ArrayList<LightListitem> listData = new ArrayList<>();
    public Context context;

    @NonNull
    @Override
    public LightListMainAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.light_room_cardview, viewGroup, false);
        return new LightListMainAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LightListMainAdapter.ItemViewHolder itemViewHolder, int i) {
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
    public void addContext(Context context){
        this.context = context;
    }
    public void clearList(){
        listData = new ArrayList<>();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_name, textView_state;
        private ImageView imageView;
        private LinearLayout layout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.light_cardview_name_text);
            textView_state = itemView.findViewById(R.id.light_cardview_state_text);
            imageView = itemView.findViewById(R.id.light_room_image);
            layout = itemView.findViewById(R.id.light_cardview_framelayout);
            //imageView.setAlpha(1f);
        }
        public void onBind(LightListitem listItem, int postition){
            textView_name.setText(listItem.getName_kor());
            if("On".equals(listItem.getState()) || "ON".equals(listItem.getState())){
                layout.setBackground(ContextCompat.getDrawable(context, R.drawable.light_cardview_line_on));
                imageView.setImageResource(R.drawable.light_on_cardview);
                textView_state.setText("On");
            }
            else {
                layout.setBackground(ContextCompat.getDrawable(context, R.drawable.light_cardview_line_off));
                imageView.setImageResource(R.drawable.light);
                textView_state.setText("Off");
            }
        }
    }
}
