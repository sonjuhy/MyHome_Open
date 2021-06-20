package com.example.sonjunhyeok.myhome.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.R;
import static com.example.sonjunhyeok.myhome.LoadingActivity.auto;

public class AccountActivity extends AppCompatActivity {

    private Setting_RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;

    private int logout_code = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        recyclerView = (RecyclerView)findViewById(R.id.Account_linearLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        Setting_Data();
    }
    private void Setting_Data(){
        recyclerAdapter = new Setting_RecyclerAdapter();
        recyclerAdapter.setContext(this);

        recyclerAdapter.addItem(new Setting_Listitem("자동 로그인 해제", 11));
        recyclerAdapter.addItem(new Setting_Listitem("로그아웃", 12));

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new AccountActivity.RecyclerTouchListener(getApplicationContext(), recyclerView, new AccountActivity.ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Setting_Listitem setting_listitem = (Setting_Listitem)recyclerAdapter.getItem(position);
                System.out.println("Clicker on");
                switch (setting_listitem.getType()){
                    case 11:
                        System.out.println("Account case 1");
                        SharedPreferences.Editor autoLogindelete = auto.edit();
                        autoLogindelete.clear();
                        autoLogindelete.commit();
                        break;
                    case 12:
                        System.out.println("Account case 2");
                        SharedPreferences.Editor autoLogindelete2 = auto.edit();
                        autoLogindelete2.clear();
                        autoLogindelete2.commit();
                        setResult(logout_code);
                        finish();
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        }));
    }
    public interface ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private AccountActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AccountActivity.ClickListener clickListener) {
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
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)){
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