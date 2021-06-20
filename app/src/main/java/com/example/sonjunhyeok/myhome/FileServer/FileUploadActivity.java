package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.R;
import com.example.sonjunhyeok.myhome.SFTP;
import static com.example.sonjunhyeok.myhome.MainActivity.user;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;

public class FileUploadActivity extends AppCompatActivity {

    private int RESULT_UPLOAD = 1;
    private int RESULT_CLOSE = 0;
    private int Popup_Upload = 2;
    private int Refresh_code = 3;

    private int folder_count = 1;
    private String folder_device;
    private String Local_device;
    private String folder_server;
    private String private_stat;
    private String fnumber;
    private String path_trash;

    private ArrayList<String> folder_list;

    private SFTP sftp;
    private TextView textView;
    private GridLayoutManager gridLayoutManager;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;


    private int RefreshClick_result = 2;
    private int MkdirClick_result = 1;
    private int FileSetting_result = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        getSupportActionBar().setElevation(0);

        Intent intent =  getIntent();
        folder_device = intent.getExtras().getString("Path");
        Local_device = intent.getExtras().getString("Local_path");
        folder_server = intent.getExtras().getString("Server_Path");
        private_stat = intent.getExtras().getString("private_stat");
        fnumber = intent.getExtras().getString("fnumber");
        path_trash = folder_device+"/휴지통";

        folder_list = new ArrayList<>();
        folder_list.add(Local_device);
        folder_list.add(Local_device+"/Myhome");

        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_Upload);
        recyclerView.setLayoutManager(gridLayoutManager);

        textView = findViewById(R.id.upload_subView);

        String tmp_path = folder_server;
        if(private_stat.equals("private")) {
            System.out.println("name : " + user.Get_name());
            tmp_path = tmp_path.replace("/home/disk1/home/private/User_"+fnumber,user.Get_name());
            textView.setText(tmp_path);
        }
        else{
            tmp_path = tmp_path.replace("/home/disk1/home/public","공용폴더");
            textView.setText(tmp_path);
        }

        Search_File(folder_device);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                ListItem listItem = (ListItem)recyclerAdapter.getItem(position);
                if (listItem.getType()) {
                    if (!listItem.getName().equals("위로가기")) {
                        System.out.println("this is folder");
                        folder_count++;
                        folder_list.add(folder_list.get(folder_count - 1) + "/" + listItem.getName());

                    } else {
                        folder_list.remove(folder_count);
                        folder_count--;
                        System.out.println("folder count : " + folder_count);
                    }
                    Search_File(folder_list.get(folder_count));
                } else {
                    OnPopupClick(listItem.getName());
                }
            }

            @Override
            public void OnLongClick(View view, int position) {
                ListItem listItem = (ListItem)recyclerAdapter.getItem(position);
                if(listItem.getName().equals("위로가기")){
                    Toast.makeText(getApplicationContext(),"이 버튼은 설정 할 수 없습니다.",Toast.LENGTH_LONG).show();
                }else{
                    Intent FileSettingIntent = new Intent(FileUploadActivity.this, FileSettingActivity.class);
                    FileSettingIntent.putExtra("Name", listItem.getName());
                    FileSettingIntent.putExtra("Device_path", folder_list.get(folder_count));
                    FileSettingIntent.putExtra("Mode", "device");
                    if(folder_list.get(folder_count).equals(path_trash)){
                        FileSettingIntent.putExtra("trash","true");
                    }
                    else{
                        FileSettingIntent.putExtra("trash","false");
                    }
                    startActivityForResult(FileSettingIntent, FileSetting_result);
                }

            }
        }));
    }
    private void Search_File(String path){
        File root = new File(path);
        File[] files = root.listFiles();

        recyclerAdapter = new RecyclerAdapter();
        recyclerAdapter.setContext(this);

        if(folder_count != 0)
            recyclerAdapter.addItem(new ListItem("위로가기","Folder", "",true));
        for(int i=0;i<files.length;i++){
            if(files[i].isDirectory() == true){
                recyclerAdapter.addItem(new ListItem(files[i].getName(),"Folder", path,true));
            }
            else{
                recyclerAdapter.addItem(new ListItem(files[i].getName(),"File", path,false));
            }
        }
        recyclerView.setAdapter(recyclerAdapter);
    }

    public interface ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private FileUploadActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FileUploadActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.OnLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)){
                clickListener.OnClick(child, recyclerView.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }
    public void OnPopupClick(String filename){
        System.out.println("file name : " + filename);
        Intent intent = new Intent(FileUploadActivity.this, Popup_fileUploadActivity.class);
        intent.putExtra("data",filename);
        intent.putExtra("folder_device",folder_list.get(folder_count));
        intent.putExtra("Server_Path",folder_server);
        startActivityForResult(intent, Popup_Upload);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filename,folder_device,folder_server;
        if(requestCode == Popup_Upload){
            if(resultCode == RESULT_CLOSE){
                Toast.makeText(this, "종료", Toast.LENGTH_LONG).show();
            }else if(resultCode == RESULT_UPLOAD){
                filename = data.getStringExtra("filename");
                folder_device = data.getStringExtra("folder_device");
                folder_server = data.getStringExtra("folder_server");

                sftp = new SFTP("server link","username","password", this);
                sftp.SetMode(3);
                sftp.execute(folder_device+filename, folder_server+"/"+filename);
            }
            else if(resultCode == Refresh_code){
                Search_File(folder_list.get(folder_count));
            }
        }
        else if(requestCode == MkdirClick_result){
            if(resultCode == Refresh_code){
                Search_File(folder_list.get(folder_count));
            }
        }
        else if(requestCode == FileSetting_result){
            if(requestCode == Refresh_code){
                Search_File(folder_list.get(folder_count));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(folder_count == 0) {
            super.onBackPressed();
        }
        else{
            folder_list.remove(folder_count);
            folder_count--;
            Search_File(folder_list.get(folder_count));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.fileupload_mkdir_button:
                Toast.makeText(this, "Make folder button click", Toast.LENGTH_LONG).show();
                System.out.println("Make folder button click");
                Intent mkdir_intent = new Intent(FileUploadActivity.this, MakeFolderActivity.class);
                mkdir_intent.putExtra("Path",folder_device);
                mkdir_intent.putExtra("Device_Path",folder_list.get(folder_count));
                mkdir_intent.putExtra("Mode", "2");
                startActivityForResult(mkdir_intent, MkdirClick_result);
                break;
            case R.id.fileupload_refresh_button:
                System.out.println("folder list : " + folder_list.get(folder_count));
                Search_File(folder_list.get(folder_count));
                break;
            case R.id.refresh_button:
                Toast.makeText(this, "새로고침 중입니다.",Toast.LENGTH_LONG).show();
                Search_File(folder_list.get(folder_count));
                break;
            case R.id.fileupload_back_button:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fileuploadactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}