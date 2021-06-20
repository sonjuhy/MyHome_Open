package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;
import com.example.sonjunhyeok.myhome.SFTP;

public class Popup_fileActivity extends AppCompatActivity {

    private int RESULT_DOWNLOAD = 1;
    private int RESULT_CLOSE = 0;
    private String folder_device;
    private String folder_server;
    private String filename;
    private String filesize;
    TextView textView;
    SFTP sftp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_file);

        textView = (TextView)findViewById(R.id.txtText);

        Intent intent = getIntent();
        filename = intent.getStringExtra("data");
        folder_server = intent.getExtras().getString("folder_server");
        folder_device = intent.getExtras().getString("folder_device");
        filesize = intent.getExtras().getString("size");
        System.out.println("file route : " + folder_device);
        System.out.println("server route : " + folder_server);
        System.out.println("file name" + filename);
        System.out.println("file size" + filesize);
        textView.setText(filename);
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

    public void mOnClose(View view){
        Intent intent = new Intent();
        intent.putExtra("result","Close Popup");
        setResult(RESULT_CLOSE,intent);

        finish();
    }
    public void mOnDownload(View view){
        /*sftp = new SFTP("sonjuhy.iptime.org","sonjuhy","son278298", this);
        sftp.SetMode(2);
        sftp.execute(folder_device, filename,folder_server);*/
        Intent intent = new Intent();
        intent.putExtra("filename",filename);
        intent.putExtra("folder_server",folder_server);
        intent.putExtra("filesize",filesize);
        setResult(RESULT_DOWNLOAD,intent);

        finish();
    }
}