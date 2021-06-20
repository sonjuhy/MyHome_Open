package com.example.sonjunhyeok.myhome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import static com.example.sonjunhyeok.myhome.LoginActivity.Async_check;

public class SigninActivity extends AppCompatActivity {

    private boolean overlap_check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        final EditText edit_name = findViewById(R.id.edit_name);
        final EditText edit_ID = findViewById(R.id.edit_ID);
        final EditText edit_PW = findViewById(R.id.edit_PW);

        Button button_ok = findViewById(R.id.button_OK);
        Button button_cancel = findViewById(R.id.button_Cancel);
        final Button button_overpal = findViewById(R.id.button_OverlapCheck);

        final Network network = null;

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(overlap_check) {
                    String tmp_name = null, tmp_ID = null, tmp_PW = null;
                    tmp_name = edit_name.getText().toString();
                    tmp_ID = edit_ID.getText().toString();
                    tmp_PW = edit_PW.getText().toString();

                    Signin_fun(network, tmp_ID, tmp_PW, tmp_name);
                    try {
                        Thread.sleep(500);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(SigninActivity.this, "아이디 중복 체크를 먼저 하세요", Toast.LENGTH_SHORT).show();
                }

            }
        });
        button_overpal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp_ID = null;
                tmp_ID = edit_ID.getText().toString();
                if(tmp_ID.isEmpty()){
                    Toast.makeText(SigninActivity.this, "아이디를 입력하세요",Toast.LENGTH_LONG).show();
                }
                else{
                    OverlapCheck_fun(tmp_ID);
                    if(overlap_check){
                        button_overpal.setText("확인완료");
                    }
                    else{
                        button_overpal.setText("ID 중복체크");
                        Toast.makeText(getApplicationContext(),"이미 존재하는 아이디입니다.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void Signin_fun(Network network,String... _param){
        network = new Network(this);
        network.execute("Signin", _param[0],_param[1],_param[2]);
        Toast.makeText(SigninActivity.this,"Sign in Success",Toast.LENGTH_LONG).show();
    }
    private void OverlapCheck_fun(String ID){
        String result = null;
        Network network = new Network(this);
        try {
            result = network.execute("IDOverlap",ID).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!result.isEmpty()){
            if(result.equals("Success")){
                overlap_check = true;
            }
            else{
                overlap_check = false;
            }
        }
    }
}
