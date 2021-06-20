package com.example.sonjunhyeok.myhome.Notice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import static com.example.sonjunhyeok.myhome.MainActivity.user;

public class NoticePopupActivity extends AppCompatActivity {
    private TextView textView_title, textView_writer, textView_time, textView_content;
    private Button button_close, button_delete;
    private String title, writer, content, time, num;

    private final int REFRESH_CODE = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice_popup);

        textView_title = findViewById(R.id.notice_popup_title);
        textView_writer = findViewById(R.id.notice_popup_writer);
        textView_content = findViewById(R.id.notice_popup_content);
        textView_time = findViewById(R.id.notice_popup_time);

        button_delete = findViewById(R.id.notice_popup_delete);
        button_delete.setVisibility(Button.INVISIBLE);
        button_delete.setClickable(false);

        Intent intent_result = getIntent();
        title = intent_result.getStringExtra("Title");
        writer = intent_result.getStringExtra("Writer");
        content = intent_result.getStringExtra("Content");
        time = intent_result.getStringExtra("Time");
        num = intent_result.getStringExtra("Num");

        if(user.Get_name().equals(writer)){
            button_delete.setClickable(true);
            button_delete.setVisibility(Button.VISIBLE);
        }
        System.out.println("title : " + title);
        textView_title.setText(title);
        textView_time.setText(time);
        textView_content.setText(content);
        textView_writer.setText(writer);
    }
    public void mOnClose_notice_popup(View view){
        finish();
    }
    public void mDelete_notice_popup(View view){
        Intent intent_callback = new Intent();
        intent_callback.putExtra("Num", num);
        setResult(REFRESH_CODE, intent_callback);
        finish();
        /*
        * delete from db
        * delete from list
        * send code for refresh
        * */
    }
}