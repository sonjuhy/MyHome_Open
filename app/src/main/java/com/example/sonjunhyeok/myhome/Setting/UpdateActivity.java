package com.example.sonjunhyeok.myhome.Setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.BuildConfig;
import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.example.sonjunhyeok.myhome.MainActivity.Version;

public class UpdateActivity extends AppCompatActivity {
    private TextView NowVer_textview, LastVer_textview;
    private Button CheckVer_botton, Update_botton;

    private double lastver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        CheckVer_botton = findViewById(R.id.UpdateCheck_button);
        Update_botton = findViewById(R.id.Update_button);

        NowVer_textview = findViewById(R.id.NowVerShow_textView);
        LastVer_textview = findViewById(R.id.LastVerShow_textView);

        NowVer_textview.setText(String.valueOf(Version));
        LastVer_textview.setText("최신버전 확인을 해주세요.");
        CheckVer_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Network n = new Network(UpdateActivity.this);
                String result = "0.0";
                try {
                     result = n.execute("Get_Ver").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("lastver : " + result);
                lastver = Double.valueOf(result);
                LastVer_textview.setText(String.valueOf(lastver));
                if(Version < lastver){
                    Update_botton.setVisibility(Button.VISIBLE);
                    Version = lastver;
                    Toast.makeText(getApplicationContext(),"업데이트가 있습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"이미 최신버전 입니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
        Update_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Network n = new Network(UpdateActivity.this);
                n.execute("Get_App");*/
                App_Download app_download = new App_Download();
                app_download.Set_Context(UpdateActivity.this);
                app_download.execute("http://sonjuhy.iptime.org/home/UpdateAppFolder/Myhome.apk",Environment.getExternalStorageDirectory().toString());
                //FindDownloadApp();
            }
        });
    }
    private void FindDownloadApp(){
        final String strFilePath = Environment.getExternalStorageDirectory() + "/Download/a.apk";
        File clsFile = new File( strFilePath );

        if( clsFile.exists() == false )
        {
            return;
        }
        Uri clsUri;
        Intent clsIntent = new Intent( Intent.ACTION_VIEW );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N )
        {
            clsUri = getUriForFile(this,  BuildConfig.APPLICATION_ID + ".provider", clsFile );
            grantUriPermission( "com.google.android.packageinstaller", clsUri, Intent.FLAG_GRANT_READ_URI_PERMISSION );
        }
        else
        {
            clsUri = Uri.fromFile( clsFile );
        }

        clsIntent.setDataAndType( clsUri, "application/vnd.android.package-archive" );
        clsIntent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
        clsIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(clsIntent);
        /*File file = new File(Environment.getExternalStorageDirectory().toString()+"/Download/a.apk");
        file.delete();*/
    }
    private class App_Download extends AsyncTask<String, Void, String>{
        private String url;

        private Context context;
        private ProgressDialog progressDialog;

        public void Set_Context(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            /*progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Download");
            progressDialog.show();*/
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            int count = 0;
            try {
                Thread.sleep(100);
                URL url = new URL(strings[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();

                int lengthofFile = urlConnection.getContentLength();

                InputStream inputStream = new BufferedInputStream(url.openStream());
                OutputStream outputStream = new FileOutputStream(strings[1]+"/Download/a.apk");

                byte data[] = new byte[1024];
                long totla = 0;
                while((count = inputStream.read(data)) != -1){
                    totla += count;
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            FindDownloadApp();
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}