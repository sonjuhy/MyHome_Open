package com.example.sonjunhyeok.myhome.Setting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.R;

public class SettingActivity extends AppCompatActivity {

    private Setting_RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;

    private int Account_code = 1;
    private int logout_code = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_setting);

        recyclerView = (RecyclerView)findViewById(R.id.Setting_linearLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        Setting_Data();
    }
    private void Setting_Data(){
        recyclerAdapter = new Setting_RecyclerAdapter();
        recyclerAdapter.setContext(this);
        System.out.println("setting adapter");

        recyclerAdapter.addItem(new Setting_Listitem("계정설정", 1));
        recyclerAdapter.addItem(new Setting_Listitem("로그인설정", 2));
        recyclerAdapter.addItem(new Setting_Listitem("업데이트 확인", 3));

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new SettingActivity.ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Setting_Listitem setting_listitem = (Setting_Listitem)recyclerAdapter.getItem(position);
                switch (setting_listitem.getType()){
                    case 1:
                        Intent ChangeUserData = new Intent(SettingActivity.this, ChangeUserDataActivity.class);
                        startActivity(ChangeUserData);
                        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                        break;
                    case 2:
                        Intent LoginSetting = new Intent(SettingActivity.this, AccountActivity.class);
                        startActivityForResult(LoginSetting, Account_code);
                        Toast.makeText(getApplicationContext(), "아직 기능 구현 중 입니다.", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                        break;
                    case 3:
                        Intent Update_intent = new Intent(SettingActivity.this, UpdateActivity.class);
                        startActivity(Update_intent);
                        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                        break;
                }
            }

            @Override
            public void OnLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Account_code){
            if(resultCode == logout_code){
                setResult(logout_code);
                finish();
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
    }

    public interface ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private SettingActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SettingActivity.ClickListener clickListener) {
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