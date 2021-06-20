package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;
import com.example.sonjunhyeok.myhome.SFTP;

public class Popup_fileUploadActivity extends AppCompatActivity {

    private int RESULT_UPLOAD = 1;
    private int RESULT_CLOSE = 0;

    private String folder_device;
    private String folder_server;
    private String filename;

    TextView textView;
    SFTP sftp;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popupupload_file);

        textView = (TextView) findViewById(R.id.textUpload);

        Intent intent = getIntent();
        filename = intent.getStringExtra("data");
        folder_server = intent.getStringExtra("Server_Path");
        System.out.println("popup filename : " + filename);
        folder_device = intent.getExtras().getString("folder_device");
        System.out.println("file route : " + folder_device);

        textView.setText(filename);
    }
    public void mOnUploadClose(View view){
        Intent intent = new Intent();
        intent.putExtra("result","Close Popup");
        setResult(RESULT_CLOSE,intent);

        finish();
    }
    public void mOnUpload(View view){
        Intent intent = new Intent();
        intent.putExtra("result","Upload file");
        intent.putExtra("filename",filename);
        intent.putExtra("folder_server",folder_server);
        intent.putExtra("folder_device",folder_device);
        setResult(RESULT_UPLOAD,intent);

        sftp = new SFTP("server link","username","password", this);
        sftp.SetMode(3);
        sftp.execute(folder_device+"/"+filename, folder_server+"/"+filename);
    }
}
