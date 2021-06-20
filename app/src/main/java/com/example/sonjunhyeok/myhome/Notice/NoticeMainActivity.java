package com.example.sonjunhyeok.myhome.Notice;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.sonjunhyeok.myhome.MainActivity.user;

public class NoticeMainActivity extends AppCompatActivity {

    private GridLayoutManager gridLayoutManager;
    private Notice_RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;

    private int NOTICE_ADD_CODE = 100;
    private int NOTICE_POPUP_CODE = 110;

    private ArrayList<Notice_ListItem> notice_listItems = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_main);
        getSupportActionBar().setElevation(0);

        Intent intent_result = getIntent();

        notice_listItems = (ArrayList<Notice_ListItem>) intent_result.getSerializableExtra("notice");
        if(null == notice_listItems){
            System.out.println("notice_itemlist is null");
            Network network = new Network(this);
            String result;
            notice_listItems = new ArrayList<>();
            try{
                result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Get_Notice").get();
                System.out.println("notice : " + result);
                JSONObject jsonObject;
                jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("Notice");
                for(int i=0;i<jsonArray.length();i++){
                    Notice_ListItem tmp = new Notice_ListItem();
                    JSONObject jsontmp = jsonArray.getJSONObject(i);
                    tmp.setContent(jsontmp.getString("content"));
                    tmp.setName(jsontmp.getString("name"));
                    tmp.setTime(jsontmp.getString("time"));
                    tmp.setNum(jsontmp.getString("number"));
                    tmp.setTitle(jsontmp.getString("Title"));
                    notice_listItems.add(tmp);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        recyclerAdapter = new Notice_RecyclerAdapter();
        int size = notice_listItems.size();
        for(int i=0;i<size; i++){
            recyclerAdapter.addItem(notice_listItems.get(i));
        }
        gridLayoutManager = new GridLayoutManager(NoticeMainActivity.this, 1);
        recyclerView = (RecyclerView)findViewById(R.id.Notice_recyclerView);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Notice_ListItem list = (Notice_ListItem)recyclerAdapter.getItem(position);
                System.out.println("Touch here : " + list.getNum());
                Intent intent_popup = new Intent(NoticeMainActivity.this, NoticePopupActivity.class);
                intent_popup.putExtra("Title", list.getTitle());
                intent_popup.putExtra("Writer", list.getName());
                intent_popup.putExtra("Content", list.getContent());
                intent_popup.putExtra("Time", list.getTime());
                intent_popup.putExtra("Num", list.getNum());
                startActivityForResult(intent_popup, NOTICE_POPUP_CODE);
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.notice_add_button:
                Intent intent_add = new Intent(NoticeMainActivity.this, NoticeAddActivity.class);
                startActivityForResult(intent_add,NOTICE_ADD_CODE);
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NOTICE_ADD_CODE){
            if(resultCode == 101){
                String title, content, time;
                title = data.getStringExtra("Title");
                content = data.getStringExtra("Content");
                time = data.getStringExtra("Time");
                Notice_ListItem item = new Notice_ListItem();
                item.setName(user.Get_name());
                item.setTitle(title);
                item.setTime(time);
                item.setContent(content);
                notice_listItems.add(item);
                recyclerAdapter.addItem(item);
                recyclerView.setAdapter(recyclerAdapter);
            }
        }
        else if(requestCode == NOTICE_POPUP_CODE){
            if(resultCode == 111){//refresh
                String num = data.getStringExtra("Num");
                Network network = new Network();
                network.execute("Delete_Notice",num);
                int position = recyclerAdapter.getItemPosition(num);
                int i;
                for(i=0;i<notice_listItems.size();i++){
                    if(notice_listItems.get(i).getNum().equals(num)){
                        break;
                    }
                }
                notice_listItems.remove(i);
                recyclerAdapter.removeItem(position);
                recyclerView.setAdapter(recyclerAdapter);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent_callback = new Intent();
        intent_callback.putExtra("notice_list",notice_listItems);
        setResult(21, intent_callback);
        finish();
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }

    private interface  ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    private static class RecyclerTouchListener implements  RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private NoticeMainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final NoticeMainActivity.ClickListener clickListener) {
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
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)) {
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