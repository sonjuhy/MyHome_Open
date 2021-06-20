package com.example.sonjunhyeok.myhome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.Calander.MyHome;
import com.example.sonjunhyeok.myhome.Calander.User_class;
import com.example.sonjunhyeok.myhome.Light.LightListitem;
import com.example.sonjunhyeok.myhome.Notice.Notice_ListItem;
import com.example.sonjunhyeok.myhome.Weather.Weather_Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.sonjunhyeok.myhome.LoadingActivity.weather_class;
import static com.example.sonjunhyeok.myhome.MainActivity.Version;
import static com.example.sonjunhyeok.myhome.LoadingActivity.auto;
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    public static boolean Login_check = false;
    public static boolean Async_check = false;
    public static String name = null;
    public static MyHome myHome = new MyHome();

    private int MainActivity_code = 100;
    private int exit_code = -1;

    private Weather_Data UltraNcst;
    private ArrayList<Weather_Data> weather_data_VilageFcst = null;
    private ArrayList<Notice_ListItem> notice_listItems = null;

    private TextView VersionView;
    private Button Login,Signin;
    private CheckBox AutoLogin_CheckBox;
    private EditText ID, PW;
    private String fnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Intent intent =new Intent(LoginActivity.this, MainActivity.class);
        final Network[] network = {null};

        Login = (Button)findViewById(R.id.Login);
        Signin =(Button)findViewById(R.id.Signin);

        ID = (EditText)findViewById(R.id.ID_edit);
        PW = (EditText)findViewById(R.id.PW_edit);

        VersionView = (TextView)findViewById(R.id.versionTextView);
        VersionView.setText("Ver "+Version);

        AutoLogin_CheckBox = findViewById(R.id.AutoLogin_checkBox);

        Login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp_ID = ID.getText().toString(), tmp_PW = PW.getText().toString();
                LoginThreadClass loginThreadClass = new LoginThreadClass();
                loginThreadClass.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tmp_ID,tmp_PW);
            }
        });
        Signin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_sign = new Intent(LoginActivity.this, SigninActivity.class);
                startActivity(intent_sign);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MainActivity_code){
            if(resultCode == exit_code){
                setResult(exit_code);
                finish();
            }
        }
    }
    private class LoginThreadClass extends  AsyncTask<String, Void, Void>{
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        @Override
        protected Void doInBackground(String... strings) {
            Network network = new Network();
            Login_fun(network, strings[0], strings[1]);//id, pw
            if(Login_check){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Login.setText("로그인 중입니다..");
                        Login.setClickable(false);
                    }
                });
                network = new Network();
                ArrayList<LightListitem> list = null;
                try {
                    String result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Light_State").get();
                    list = LightlistParser(result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                notice_listItems = Get_Notice();
                weather_class.Weather_Search_Direct(91,76);
                UltraNcst = weather_class.Get_Weather_UtlraNcst();

                //weather_data_UltraFcst = weather_class.Get_Weather_UtlraFcst();
                weather_data_VilageFcst = weather_class.Get_Weather_VilageFcst();


                intent.putExtra("Name", name);
                intent.putExtra("ID",strings[0]);
                intent.putExtra("PW",strings[1]);
                intent.putExtra("fnumber",fnumber);
                intent.putExtra("Ultra", UltraNcst);
                intent.putExtra("Vilage", weather_data_VilageFcst);
                intent.putExtra("notice", notice_listItems);
                intent.putExtra("light", list);

                if(AutoLogin_CheckBox.isChecked()) {
                    auto = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();
                    autoLogin.putString("inputID", strings[0]);
                    autoLogin.putString("inputPW", strings[1]);
                    autoLogin.putBoolean("AutoLoginCheck", true);
                    autoLogin.commit();
                }
                //Get_Cal_fun(network, User.Name_Output());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(Login_check){
                Toast.makeText(LoginActivity.this , "로그인성공.",Toast.LENGTH_LONG).show();
                startLoading(intent);
            }
            else{
                Toast.makeText(LoginActivity.this , "로그인실패.",Toast.LENGTH_LONG).show();
                AutoLogin_CheckBox.setChecked(false);
            }
        }
    }
    private void startLoading(final Intent intent){
        Handler handelr = new Handler();
        handelr.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(intent, MainActivity_code);
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
            }
        }, 500);
    }
    private void Login_fun(Network network, String Input_ID, String Input_PW){
        String result = null;
        String[] tmp_result = null;
        network = new Network();
        try {
            result = network.execute("login",Input_ID, Input_PW).get();
            tmp_result = result.split("/");
            if(tmp_result.length > 1) {
                result = tmp_result[1];
            }
            System.out.println("tmp_result : " + result);
            JSONObject jsonObject = null;
            String check;
            try {
                jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("Login");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsontmp = jsonArray.getJSONObject(i);
                    check = jsontmp.getString("check");
                    if(check.equals("Failed")){
                        Login_check = false;
                        break;
                    }
                    name = jsontmp.getString("name");
                    fnumber = jsontmp.getString("fnumber");
                    Login_check = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Login result : " + result);
    }
    private void Get_Cal_fun(Network network,String name){
        network = new Network(this);
        network.execute("Get_Cal",name);
        while(!Async_check){}
        Async_check = false;
    }
    private ArrayList<Notice_ListItem> Get_Notice(){
        Network network = new Network();
        String result;
        ArrayList<Notice_ListItem> list = new ArrayList<>();
        try{
            result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Get_Notice").get();
            System.out.println("notice : " + result);
            JSONObject jsonObject;
            jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("Notice");
            for(int i=0;i<jsonArray.length();i++){
                Notice_ListItem tmp = new Notice_ListItem();
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                tmp.setContent(jsontmp.getString("content"));
                tmp.setName(jsontmp.getString("name"));
                tmp.setTime(jsontmp.getString("time"));
                tmp.setNum(jsontmp.getString("number"));
                list.add(tmp);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    private ArrayList<LightListitem> LightlistParser(String data){
        ArrayList<LightListitem> list = new ArrayList<>();
        try {
            JSONObject jsonObject;
            jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("Light_State");
            for(int i=0;i<jsonArray.length();i++){
                LightListitem tmp = new LightListitem();
                JSONObject jsontmp = jsonArray.getJSONObject(i);
                tmp.setName(jsontmp.getString("Room"));
                tmp.setName_kor(jsontmp.getString("Kor"));
                tmp.setState(jsontmp.getString("State"));
                tmp.setCategory(jsontmp.getString("Category"));
                tmp.setPlace(i+1);
                tmp.setAlarm_count(0);
                list.add(tmp);
            }
            return list;
        } catch (Exception e) {
            System.out.println("error in loading Light JSON : " + e.getMessage());
            return null;
        }
    }
}