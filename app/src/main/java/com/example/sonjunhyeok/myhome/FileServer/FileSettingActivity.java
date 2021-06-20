package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;
import com.example.sonjunhyeok.myhome.SFTP;

import java.io.File;

public class FileSettingActivity extends AppCompatActivity {

    private int RESULT_CLOSE = -1;
    private int RESULT_DELETE = 0;
    private int RESULT_RENAME = 1;
    private int Rename_activity = 2;
    private int RESULT_REFRESH = 3;

    private String mode = null;//device, server
    private String folder_device;
    private String folder_server;
    private String filename;
    private String private_stat;
    private String fnumber;
    private String trash = "false";

    private TextView textView;
    private Button rename;
    SFTP sftp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file_setting);

        Intent intent = getIntent();
        filename = intent.getStringExtra("Name");
        folder_device = intent.getStringExtra("Device_path");
        folder_server = intent.getStringExtra("Server_path");
        mode = intent.getStringExtra("Mode");
        private_stat = intent.getStringExtra("private");
        fnumber = intent.getStringExtra("fnumber");
        trash = intent.getStringExtra("trash");

        System.out.println("FileSetting activity filename : "+ filename);
        System.out.println("FileSetting activity folder_device : "+ folder_device);
        System.out.println("FileSetting activity folder_server : "+ folder_server);
        System.out.println("FileSetting activity trash : "+ trash);

        textView = findViewById(R.id.textFileSetting);
        textView.setText(filename);
        rename = findViewById(R.id.rename_button);
        if(trash.equals("true")){
            rename.setText("복원");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Rename_activity){
            if(resultCode == RESULT_RENAME){
                String Get_filename = data.getStringExtra("filename");
                System.out.println("file setting filename : " +Get_filename);
                if(mode.equals("device")){
                    File Prefile = new File(folder_device+"/"+filename);
                    File Ordfile = new File(folder_device+"/"+Get_filename);
                    Prefile.renameTo(Ordfile);
                }
                else{
                    sftp = new SFTP("server link","username","password", this);
                    sftp.SetMode(6);
                    sftp.execute(folder_server+"/"+filename,folder_server+"/"+Get_filename);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setResult(RESULT_REFRESH);
                finish();
            }
        }
    }

    public void mOnDelete(View view){
        if(mode.equals("device")){
            File file = new File(folder_device+"/"+filename);
            if(file.delete()){
                Toast.makeText(getApplicationContext(),"파일 삭제 성공",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"파일 삭제 실패",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Network network = new Network(FileSettingActivity.this);

            if(trash.equals("false")) {
                System.out.println("folder file name : " + folder_server + "/" + filename);
                if (private_stat.equals("private")) {
                    network.execute("FileDelete", filename, folder_server, "private", "User_" + fnumber);
                } else {
                    network.execute("FileDelete", filename, folder_server, "public", "User_" + fnumber);
                }
            }
            else{
                if (private_stat.equals("private")) {
                    network.execute("FileDistroy", filename, folder_server);
                } else {
                    network.execute("FileDistroy", filename, folder_server);
                }
            }
        }
        setResult(RESULT_REFRESH);
        finish();
    }
    public void mOnRename(View view){
        if(trash.equals("true")){
            Network network = new Network(FileSettingActivity.this);
            if(private_stat.equals("private")){
                network.execute("FileRestore",filename, folder_server,"private","User_"+fnumber);
            }
            else{
                network.execute("FileRestore",filename, folder_server,"public","User_"+fnumber);
            }
        }
        else {
            Intent intent = new Intent(FileSettingActivity.this, RenameInputActivity.class);
            startActivityForResult(intent, Rename_activity);
        }
        setResult(RESULT_REFRESH);
        finish();
    }
    public void mOnSettingClose(View view){
        Intent intent = new Intent();
        intent.putExtra("result","Close Popup");
        setResult(RESULT_CLOSE,intent);

        finish();
    }
}