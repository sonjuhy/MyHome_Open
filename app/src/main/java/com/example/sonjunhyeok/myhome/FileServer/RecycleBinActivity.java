package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;
import com.example.sonjunhyeok.myhome.SFTP;

public class RecycleBinActivity extends AppCompatActivity {

    private int Refresh_code = 3;

    private String filename;
    private String PrePath;
    private String Server_path;
    private TextView RecycleBinTextview;

    private SFTP sftp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recycle_bin);

        RecycleBinTextview = findViewById(R.id.textRecycleBin);
        Intent intent = getIntent();
        filename = intent.getStringExtra("Name");
        PrePath = intent.getStringExtra("PrePath");
        Server_path = intent.getStringExtra("Server_path");

        System.out.println("prepath : " + PrePath);
        System.out.println("prepath : " + Server_path);

        RecycleBinTextview.setText(filename);

    }
    public void mOnRecycleBinDelete(View view){
        sftp = new SFTP("server link","username","password", this);
        sftp.SetMode(4);
        sftp.execute(Server_path+"/"+filename);
        Network network = new Network(RecycleBinActivity.this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setResult(Refresh_code);
        finish();
    }
    public void mOnRecycleBinReplace(View view){
        sftp = new SFTP("server link","username","password", this);
        sftp.SetMode(6);
        sftp.execute(Server_path+"/"+filename, "/home/disk1/home/복원/"+filename);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setResult(Refresh_code);
        finish();
    }
    public void mOnRecycleBinClose(View view){
        finish();
    }
}