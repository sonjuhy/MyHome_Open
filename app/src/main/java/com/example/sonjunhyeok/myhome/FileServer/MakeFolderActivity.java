package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.R;
import com.example.sonjunhyeok.myhome.SFTP;

import java.io.File;

public class MakeFolderActivity extends AppCompatActivity {

    private int mode;
    private int Refresh_code = 3;

    private String server_folder;
    private String device_folder;
    private String getText;
    private EditText editText;
    private TextView pathtext;
    private SFTP sftp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_make_folder);

        Intent intent = getIntent();
        server_folder = intent.getStringExtra("Path");
        device_folder = intent.getStringExtra("Device_Path");
        String str_mode = intent.getStringExtra("Mode");
        mode = Integer.parseInt(str_mode);

        editText = findViewById(R.id.editText);
        pathtext = findViewById(R.id.pathview);
        pathtext.setText("지금 경로 : "+server_folder);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }
    public void mOnMake(View view){
        switch(mode){
            case 1://server folder
                getText = editText.getText().toString();
                if(getText.length() == 0){
                    Toast.makeText(this, "이름입력을 하지 않았습니다",Toast.LENGTH_LONG).show();
                }
                else {
                    sftp = new SFTP("server link","username","password", this);
                    sftp.SetMode(0);
                    sftp.execute(server_folder + "/" + getText);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2://device folder
                getText = editText.getText().toString();
                if(getText.length() == 0){
                    Toast.makeText(this, "이름입력을 하지 않았습니다",Toast.LENGTH_LONG).show();
                }
                else {
                    File path = new File(device_folder+"/"+getText+"/");
                    if (!path.exists()) {
                        path.mkdirs();
                        System.out.println("make folder");
                    }
                }
                break;
        }
        setResult(Refresh_code);
        finish();
    }
    public void mOnClose(View view){
        Intent intent = new Intent();
        intent.putExtra("result","Close Popup");
        setResult(RESULT_OK,intent);

        finish();
    }
}