package com.example.sonjunhyeok.myhome;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Network extends AsyncTask<String, Void, String> {
    private HttpURLConnection httpURLConnection = null;
    private OutputStream outputStream = null;
    private String data = null;
    private String link = "Server Link";
    //private String link = "192.168.0.254/home";
    private String line = null;
    private String mJsonString = null;
    private int responseStatusCode = 0;
    private int mode = 0;
    private boolean dialog_use = false;
    private boolean upload_mode = false;

    private Context context;
    private InputStream inputStream = null;
    private InputStreamReader inputStreamReader = null;
    private BufferedReader bufferedReader = null;
    private StringBuilder stringBuilder = null;
    private URL url = null;

    private ProgressDialog progressDialog;

    public Network(Context context){
        this.context = context;
        this.dialog_use = true;
    }
    public Network(){
        this.dialog_use = false;
    }
    @Override
    protected void onPreExecute() {
        if(dialog_use) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("로딩 중입니다.");
            progressDialog.show();
        }
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... _param){
        String n = _param[0];
        switch (n){
            case "login":
                link += "/Login_Check.php";
                Login_Input(_param[1],_param[2]);
                mode = 1;
                upload_mode = true;
                break;
            case "Signin":
                link += "/SignUp.php";
                Signin_Input(_param[1], _param[2], _param[3]);
                mode = 2;
                upload_mode = true;
                break;
            case "IDOverlap":
                link += "/SignUP_IDCheck.php";
                Overlap_Input(_param[1]);
                upload_mode = true;
                break;
            case "Get_Userinfo":
                link += "/Get_UserInfo.php";
                mode = 3;
                break;
            case "Cal_Upload":
                link += "/Cal_Upload.php";
                Cal_Upload(_param[1],_param[2],_param[3],_param[4],_param[5],_param[6]);
                upload_mode = true;
                mode = 4;
                //name, contents, startday, endday, starttime, endtime
                break;
            case "Get_Cal":
                link += "/Get_Cal.php";
                break;
            case "Get_Ver":
                link += "/Get_Version.php";
                break;
            case "Update_User":
                link += "/Update_UserData.php";
                Signin_Input(_param[1], _param[2], _param[3]);
                upload_mode = true;
                break;
            case "FileList":
                link += "/Get_Filelist.php";
                Filelist_Input(_param[1]);
                upload_mode = true;
                break;
            case "FileDelete":
                link += "/Delete_file.php";
                FileDelete_Input(_param[1], _param[2], _param[3], _param[4]);//dir, path, mode, user
                upload_mode = true;
                break;
            case "FileRestore":
                link += "/Restore_file.php";
                FileRestore_Input(_param[1], _param[2], _param[3], _param[4]);//file, path, mode, user
                upload_mode = true;
                break;
            case "FileDistroy":
                link += "/Distroy_file.php";
                FileDistroy_Input(_param[1], _param[2]);//file,  path
                upload_mode = true;
                break;
            case "Get_Notice":
                link += "/Notice_GetList.php";
                break;
            case "Set_Notice":
                link += "/Notice_Set.php";
                Notice_Input(_param[1], _param[2], _param[3], _param[4]); // time, content, name, title
                upload_mode = true;
                break;
            case "Delete_Notice":
                link += "/Notice_Delete.php";
                NoticeDelete_Input(_param[1]);//Num
                upload_mode = true;
                break;
            case "Light_State":
                link += "/Light_State.php";
                upload_mode = false;
                break;
            case "Light_ReserveList":
                link += "/Get_Reserve.php";
                upload_mode = false;
                break;
            case "Light_ReserveUpload"://String time, String day, String name, String room, String roomkor, String action, String repeat
                link += "/Set_Reserve.php";
                LightReserveSet_Input(_param[1], _param[2], _param[3], _param[4], _param[5], _param[6], _param[7]);
                upload_mode = true;
                break;
            case "Light_ReserveUpdate"://String time, String day, String name, String room, String roomkor, String action, String repeat, String num
                link += "/Update_Reserve.php";
                LightReserveUpdate_Input(_param[1], _param[2], _param[3], _param[4], _param[5], _param[6], _param[7], _param[8]);
                upload_mode = true;
                break;
            case "Light_ReserveDelete":
                link += "/Delete_Reserve.php";
                LightReserveDelete_Input(_param[1]);
                upload_mode = true;
                break;

        }
        System.out.println("Network data : " + data);
        try{
            url = new URL(link);

            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            if(upload_mode) {
                outputStream = httpURLConnection.getOutputStream();
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            }

            responseStatusCode = httpURLConnection.getResponseCode();

            if(responseStatusCode == HttpURLConnection.HTTP_OK){
                inputStream = httpURLConnection.getInputStream();
                System.out.println("response is ok");
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
                System.out.println("response is error : " + inputStream);
            }
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);

            stringBuilder = new StringBuilder();

            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }

            httpURLConnection.disconnect();
            return stringBuilder.toString().trim();

        } catch (ProtocolException e) {
            e.printStackTrace();
            System.out.println("protocol error");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO error");
        }

        return "Error";
    }

    @Override
    protected void onPostExecute(String s) {
        switch (mode){
            case 1://login
                break;
            case 2:
                break;
            case 3:
                break;
        }
        if(dialog_use) {
            progressDialog.dismiss();
        }
        super.onPostExecute(s);
    }

    private void Login_Input(String user_id, String user_pw){
        try {
            data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
            data += "&" + URLEncoder.encode("PW", "UTF-8") + "=" + URLEncoder.encode(user_pw, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void Signin_Input(String user_id, String user_pw, String user_name){
        try {
            data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
            data += "&" + URLEncoder.encode("Name","UTF-8")+"="+ URLEncoder.encode(user_name,"UTF-8");
            data += "&" + URLEncoder.encode("PW", "UTF-8") + "=" + URLEncoder.encode(user_pw, "UTF-8");
            System.out.println("Sign data : " + data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void Filelist_Input(String dir){
        try {
            data = URLEncoder.encode("server_dir","UTF-8")+"="+URLEncoder.encode(dir,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void FileDelete_Input(String dir, String path, String mode, String user){
        try {
            data = URLEncoder.encode("dir", "UTF-8") + "=" + URLEncoder.encode(dir, "UTF-8");
            data += "&" + URLEncoder.encode("path","UTF-8")+"="+ URLEncoder.encode(path,"UTF-8");
            data += "&" + URLEncoder.encode("user","UTF-8")+"="+ URLEncoder.encode(user,"UTF-8");
            data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(mode, "UTF-8");
            System.out.println("Sign data : " + data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void FileRestore_Input(String file, String path, String mode, String user){
        try {
            data = URLEncoder.encode("file", "UTF-8") + "=" + URLEncoder.encode(file, "UTF-8");
            data += "&" + URLEncoder.encode("path","UTF-8")+"="+ URLEncoder.encode(path,"UTF-8");
            data += "&" + URLEncoder.encode("user","UTF-8")+"="+ URLEncoder.encode(user,"UTF-8");
            data += "&" + URLEncoder.encode("mode", "UTF-8") + "=" + URLEncoder.encode(mode, "UTF-8");
            System.out.println("Sign data : " + data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void FileDistroy_Input(String file, String path){
        try {
            data = URLEncoder.encode("file","UTF-8")+"="+URLEncoder.encode(file,"UTF-8");
            data += "&" + URLEncoder.encode("path", "UTF-8") + "=" + URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void Overlap_Input(String ID){
        try {
            data = URLEncoder.encode("ID","UTF-8")+"="+URLEncoder.encode(ID,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void Notice_Input(String Time,String Content,String Name, String title){
        try {
            data = URLEncoder.encode("Time","UTF-8")+"="+URLEncoder.encode(Time,"UTF-8");
            data += "&" + URLEncoder.encode("Content", "UTF-8") + "=" + URLEncoder.encode(Content, "UTF-8");
            data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
            data += "&" + URLEncoder.encode("Title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void NoticeDelete_Input(String Num){
        try {
            data = URLEncoder.encode("Num","UTF-8")+"="+URLEncoder.encode(Num,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void LightReserveSet_Input(String time, String day, String name, String room, String roomkor , String action, String repeat){
        try {
            data = URLEncoder.encode("Time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8");
            data += "&" + URLEncoder.encode("Do", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
            data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("Day", "UTF-8") + "=" + URLEncoder.encode(day, "UTF-8");
            data += "&" + URLEncoder.encode("Room", "UTF-8") + "=" + URLEncoder.encode(room, "UTF-8");
            data += "&" + URLEncoder.encode("RoomKor", "UTF-8") + "=" + URLEncoder.encode(roomkor, "UTF-8");
            data += "&" + URLEncoder.encode("Repeat", "UTF-8") + "=" + URLEncoder.encode(repeat, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void LightReserveUpdate_Input(String time, String day, String name, String room, String roomkor , String action, String repeat, String num){
        try {
            data = URLEncoder.encode("Time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8");
            data += "&" + URLEncoder.encode("Do", "UTF-8") + "=" + URLEncoder.encode(action, "UTF-8");
            data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("Day", "UTF-8") + "=" + URLEncoder.encode(day, "UTF-8");
            data += "&" + URLEncoder.encode("Room", "UTF-8") + "=" + URLEncoder.encode(room, "UTF-8");
            data += "&" + URLEncoder.encode("RoomKor", "UTF-8") + "=" + URLEncoder.encode(roomkor, "UTF-8");
            data += "&" + URLEncoder.encode("Repeat", "UTF-8") + "=" + URLEncoder.encode(repeat, "UTF-8");
            data += "&" + URLEncoder.encode("Num", "UTF-8") + "=" + URLEncoder.encode(num, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void LightReserveDelete_Input(String num){
        try {
            data = URLEncoder.encode("Num","UTF-8")+"="+URLEncoder.encode(num,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
   /* private void User_Cal_load(String... _param){
        String TAG_User = _param[0];
        Calander_struct tmp_cal = new Calander_struct();
        int time_arr[] = new int[5];
        try{
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_User);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String tmp_Name = item.getString("Name");
                String tmp_Content = item.getString("Contents");
                String tmp_Start_day = item.getString("Start_day");
                String tmp_End_day = item.getString("End_day");
                String tmp_Start_time = item.getString("Start_time");
                String tmp_End_time = item.getString("End_time");

                tmp_cal.Cal_insert_String(tmp_Name, tmp_Content);

                time_arr = Time_Div_Toint(tmp_Start_day+tmp_Start_time);
                tmp_cal.Cal_insert_Starttime(time_arr[0],time_arr[1],time_arr[2],time_arr[3],time_arr[4]);

                time_arr = Time_Div_Toint(tmp_End_day+tmp_End_time);
                tmp_cal.Cal_insert_Endtime(time_arr[0],time_arr[1],time_arr[2],time_arr[3],time_arr[4]);

                myHome.Output_User().Cal_Insert(tmp_cal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }*/
    private void Cal_Upload(String... _param){
        String Cal_name = _param[0];
        String Cal_con = _param[1];
        String Start_day = _param[2];
        String End_day = _param[3];
        String Start_time = _param[4];
        String End_time = _param[5];

        try{
            data = URLEncoder.encode("Name","UTF-8")+"="+URLEncoder.encode(Cal_name,"UTF-8");
            data += URLEncoder.encode("Contents","UTF-8")+"="+URLEncoder.encode(Cal_con,"UTF-8");
            data += URLEncoder.encode("Start_day","UTF-8")+"="+URLEncoder.encode(Start_day,"UTF-8");
            data += URLEncoder.encode("End_day","UTF-8")+"="+URLEncoder.encode(End_day,"UTF-8");
            data += URLEncoder.encode("Start_time","UTF-8")+"="+URLEncoder.encode(Start_time,"UTF-8");
            data += URLEncoder.encode("End_time","UTF-8")+"="+URLEncoder.encode(End_time,"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    /*private void Cal_load(){
        Calander_struct tmp_cal = new Calander_struct();
        int time_arr[] = new int[5];
        try{
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("sonjuhy");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String tmp_Name = item.getString("Name");
                String tmp_Content = item.getString("Contents");
                String tmp_Start_day = item.getString("Start_day");
                String tmp_End_day = item.getString("End_day");
                String tmp_Start_time = item.getString("Start_time");
                String tmp_End_time = item.getString("End_time");

                tmp_cal.Cal_insert_String(tmp_Name, tmp_Content);

                time_arr = Time_Div_Toint(tmp_Start_day+tmp_Start_time);
                tmp_cal.Cal_insert_Starttime(time_arr[0],time_arr[1],time_arr[2],time_arr[3],time_arr[4]);

                time_arr = Time_Div_Toint(tmp_End_day+tmp_End_time);
                tmp_cal.Cal_insert_Endtime(time_arr[0],time_arr[1],time_arr[2],time_arr[3],time_arr[4]);

                myHome.Input_Cal(tmp_cal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }*/
    private int[] Time_Div_Toint(String time){
        int tmp_time = Integer.parseInt(time);
        int time_arr[] = new int[5];
        time_arr[0] = tmp_time/10000000;
        time_arr[1] = (tmp_time - time_arr[0])/100000;
        time_arr[2] = (tmp_time - time_arr[0] - time_arr[1])/1000;
        time_arr[3] = (tmp_time - time_arr[0] - time_arr[1] - time_arr[2])/10;
        time_arr[4] = (tmp_time - time_arr[0] - time_arr[1] - time_arr[2] - time_arr[1]);
        return time_arr;
    }

    /*private void User_load(String... _param){
        String TAG_User = _param[0];

        try{
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_User);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String tmp_Name = item.getString("Name");
                String tmp_Age = item.getString("Age");
                String tmp_Address = item.getString("Address");

                System.out.println("Name : "+tmp_Name +
                        "\nAge : "+tmp_Age +
                        "\nAddress : "+tmp_Address);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}