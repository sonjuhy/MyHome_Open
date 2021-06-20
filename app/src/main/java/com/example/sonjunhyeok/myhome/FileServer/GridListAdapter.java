package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

import java.util.ArrayList;

public class GridListAdapter extends BaseAdapter {
    ArrayList<ListItem> items = new ArrayList<ListItem>();
    Context context;
    public void addItem(ListItem listItem){
        items.add(listItem);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        ListItem listItem = items.get(position);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item,parent,false);
        }
        TextView nameText = convertView.findViewById(R.id.nameText);
        TextView sizeText = convertView.findViewById(R.id.sizeText);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        nameText.setText(listItem.getName());
        sizeText.setText(listItem.getSize());
        if(listItem.getType()){//folder
            imageView.setImageResource(R.drawable.folder240);
        }
        else{//file
            imageView.setImageResource(R.drawable.file240);
        }

        return convertView;
    }
}
