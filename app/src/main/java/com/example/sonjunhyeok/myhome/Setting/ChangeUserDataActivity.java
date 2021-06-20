package com.example.sonjunhyeok.myhome.Setting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import java.util.concurrent.ExecutionException;

public class ChangeUserDataActivity extends AppCompatActivity {
    private Network network;

    private EditText Edit_id, Edit_pw, Edit_name;
    private Button Button_check, Button_cancle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_data);

        Edit_id = findViewById(R.id.edit_ID_update);
        Edit_pw = findViewById(R.id.edit_PW_update);
        Edit_name = findViewById(R.id.edit_name_update);

        Button_check = findViewById(R.id.button_OK_update);
        Button_cancle = findViewById(R.id.button_Cancel_update);

        Button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = null, pw = null, name = null;
                id = String.valueOf(Edit_id.getText());
                pw = String.valueOf(Edit_pw.getText());
                name = String.valueOf(Edit_name.getText());

                if(id.isEmpty()){
                    Toast.makeText(getApplicationContext(),"ID를 입력하지 않으셨습니다.", Toast.LENGTH_LONG).show();
                }
                else if(pw.isEmpty()){
                    Toast.makeText(getApplicationContext(),"PW를 입력하지 않으셨습니다.", Toast.LENGTH_LONG).show();
                }
                else if(name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"이름를 입력하지 않으셨습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    Network(id,pw,name);
                }
            }
        });
        Button_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void Network(String id, String pw, String name){
        network = new Network(this);
        String result= null;
        try {
            result = network.execute("Update_User", id,pw,name).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("change result : " + result);
        if(result.equals("Success")){
            Toast.makeText(getApplicationContext(),"변경성공 다시 로그인하세요.", Toast.LENGTH_LONG).show();
        }
        else if(result.equals("ID,PW not cur.")){
            Toast.makeText(getApplicationContext(),"ID,PW가 이 계정과 일치하지 않습니다.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"DataBase Error.", Toast.LENGTH_LONG).show();
        }
    }
}