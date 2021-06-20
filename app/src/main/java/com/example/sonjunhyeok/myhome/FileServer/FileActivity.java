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
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;
import com.example.sonjunhyeok.myhome.SFTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FileActivity extends AppCompatActivity {
    private int server_folder_count = 0;
    private int server_folder_private_count = 0;
    private int server_folder_public_count = 0;
    private int RESULT_DOWNLOAD = 1;
    private int RESULT_CLOSE = 0;
    private int Refresh_code = 3;
    private boolean switch_check = false;

    private SFTP sftp;
    private String folder_device;
    private String Local_device;
    private String path_trash_can, path_private_trash_can, path_public_trash_can;
    private String fnumber;
    private ArrayList<String> server_folder = new ArrayList<>();
    private ArrayList<String> server_folder_private = new ArrayList<>();
    private ArrayList<String> server_folder_public = new ArrayList<>();

    private GridLayoutManager gridLayoutManager;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private Switch aSwitch;
    private TextView textView_sub;

    private int PopupClick_result = 1;
    private int UploadClick_result = 2;
    private int MkdirClick_result = 3;
    private int FileSetting_result = 4;
    private int RecycleBin_result = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        getSupportActionBar().setElevation(0);

        aSwitch = findViewById(R.id.Serverpublic_switch);
        textView_sub = findViewById(R.id.Serverpublic_subView);

        Intent intent = getIntent();
        folder_device = intent.getExtras().getString("folder_device");
        Local_device = intent.getExtras().getString("Loacl_device");
        fnumber = intent.getExtras().getString("fnumber");
        path_private_trash_can = "/home/disk1/home/private/User_"+fnumber+"/휴지통";
        path_public_trash_can ="/home/disk1/home/public/휴지통";
        server_folder_private.add("/home/disk1/home/private/User_"+fnumber);
        server_folder_public.add("/home/disk1/home/public");

        gridLayoutManager = new GridLayoutManager(FileActivity.this, 3);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_Download);
        recyclerView.setLayoutManager(gridLayoutManager);

        System.out.println("private folder path : " + server_folder_private.get(server_folder_private_count));
        Search_List(server_folder_private.get(server_folder_private_count));
        server_folder = server_folder_private;
        path_trash_can = path_private_trash_can;

        textView_sub.setText("개인 폴더 입니다.");

        aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//public
                    textView_sub.setText("공용 폴더 입니다.");
                    server_folder = server_folder_public;
                    server_folder_private_count = server_folder_count;
                    server_folder_count = server_folder_public_count;
                    path_trash_can = path_public_trash_can;
                    switch_check = true;
                }
                else{//private
                    textView_sub.setText("개인 폴더 입니다.");
                    server_folder = server_folder_private;
                    server_folder_public_count = server_folder_count;
                    server_folder_count = server_folder_private_count;
                    path_trash_can = path_private_trash_can;
                    switch_check = false;
                }
                Search_List(server_folder.get(server_folder_count));
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void OnClick(View view, int position) {
                ListItem listItem = (ListItem)recyclerAdapter.getItem(position);
                if (listItem.getType()) {
                    if (listItem.getName().equals("위로가기")) {
                        server_folder.remove(server_folder_count);
                        server_folder_count--;
                        System.out.println("folder count : " + server_folder_count);
                    }
                    else if(server_folder.get(server_folder_count).equals("/home/disk1/home/휴지통")){
                        Intent RecycleBinIntent = new Intent(FileActivity.this, RecycleBinActivity.class);
                        RecycleBinIntent.putExtra("Name", listItem.getName());
                        RecycleBinIntent.putExtra("PrePath",listItem.getPrePath());
                        RecycleBinIntent.putExtra("Server_path", server_folder.get(server_folder_count));
                        startActivityForResult(RecycleBinIntent, RecycleBin_result);
                    }
                    else {
                        System.out.println("this is folder");
                        server_folder_count++;
                        server_folder.add(server_folder.get(server_folder_count - 1) + "/" + listItem.getName());
                    }
                    Search_List(server_folder.get(server_folder_count));
                }
                else {
                    if(server_folder.get(server_folder_count).equals(path_trash_can)){
                        Toast.makeText(getApplicationContext(),"휴지통에 있는 파일은 설정만 할 수 있습니다.",Toast.LENGTH_LONG).show();
                    }
                    else {
                        System.out.println("send file size : " + listItem.get_sizelong());
                        OnPopupClick(listItem.getName(), listItem.get_sizelong());
                    }
                }
            }

            @Override
            public void OnLongClick(View view, int position) {
                ListItem listItem = (ListItem) recyclerAdapter.getItem(position);

                if (listItem.getName().equals("위로가기")) {
                    Toast.makeText(getApplicationContext(), "이 버튼은 설정 할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                else if (listItem.getName().equals("휴지통")) {
                    Toast.makeText(getApplicationContext(), "이 폴더는 설정 할 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                else if (listItem.getName().equals("복원")) {
                    Toast.makeText(getApplicationContext(), "이 폴더는 설정 할 수 없습니다.", Toast.LENGTH_LONG).show();
                }else {
                    Intent FileSettingIntent = new Intent(FileActivity.this, FileSettingActivity.class);
                    FileSettingIntent.putExtra("Name", listItem.getName());
                    FileSettingIntent.putExtra("Server_path", server_folder.get(server_folder_count));
                    FileSettingIntent.putExtra("Mode", "server");
                    if(server_folder.get(server_folder_count).equals(path_trash_can)){
                        FileSettingIntent.putExtra("trash","true");
                    }
                    else{
                        FileSettingIntent.putExtra("trash","false");
                    }
                    System.out.println("server folder get : " +server_folder.get(server_folder_count));
                    if(switch_check){ //public
                        FileSettingIntent.putExtra("private", "public");
                    }
                    else{ //private
                        FileSettingIntent.putExtra("private", "private");
                        FileSettingIntent.putExtra("fnumber",fnumber);
                    }
                    startActivityForResult(FileSettingIntent, FileSetting_result);
                }

            }
        }));
    }
    private String Cal_size(String size){
        String[] size_count = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        double factor = Math.floor((size.length()-1)/3);
        double tmp_size = Double.valueOf(size);
        for(int i=0;i<(int)factor;i++){
            tmp_size = tmp_size/(double)1024;
        }
        tmp_size = Math.floor(tmp_size);
        String result = Double.toString(tmp_size)+size_count[(int)factor];
        return result;
    }
    private void Search_List(String path){
        recyclerAdapter = new RecyclerAdapter();
        recyclerAdapter.setContext(this);

        String result = null, name = null, size = null, type = null, result_size = null;
        boolean type_boolean = true;

        Network network = new Network(FileActivity.this);
        if(server_folder_count != 0){
            recyclerAdapter.addItem(new ListItem("위로가기","Folder", "",true));
        }
        try {
            result = network.execute("FileList",path).get();
            if(result.equals("Error")){
                Toast.makeText(getApplicationContext(),"예상치 못한 에러가 발생했습니다. 다시 시도해보세요.",Toast.LENGTH_LONG).show();
            }
            else {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("Filelist");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsontmp = jsonArray.getJSONObject(i);
                    name = jsontmp.getString("filename");
                    size = jsontmp.getString("size");
                    type = jsontmp.getString("type");
                    result_size = Cal_size(size);
                    System.out.println("result size : " +result_size);
                    ListItem tmp_listitem = null;
                    if (type.equals("file")) {
                        type_boolean = false;
                        tmp_listitem = new ListItem(name, result_size, path, type_boolean);
                        tmp_listitem.set_sizelong(Long.valueOf(size));
                    } else {
                        type_boolean = true;
                        tmp_listitem = new ListItem(name,"Folder", path, type_boolean);
                        tmp_listitem.set_sizelong(Long.valueOf(size));
                    }
                    recyclerAdapter.addItem(tmp_listitem);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(recyclerAdapter);
    }

    private interface  ClickListener{
        void OnClick(View view, int position);
        void OnLongClick(View view, int position);
    }
    private static class RecyclerTouchListener implements  RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private FileActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FileActivity.ClickListener clickListener) {
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
    @Override
    public void onBackPressed() {
        if(server_folder_count == 0) {
            super.onBackPressed();
            overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
        }
        else{
            server_folder.remove(server_folder_count);
            server_folder_count--;
            Search_List(server_folder.get(server_folder_count));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filename, folder_server, filesize;
        if(requestCode == PopupClick_result){
            filename = data.getStringExtra("filename");
            folder_server = data.getStringExtra("folder_server");
            filesize = data.getStringExtra("filesize");
            System.out.println("onactivity 3 : " + filesize);
            if(resultCode == RESULT_CLOSE){
                Toast.makeText(getApplicationContext(),"팝업 종료", Toast.LENGTH_LONG).show();
            }
            else if(resultCode == RESULT_DOWNLOAD){
                sftp = new SFTP("server link","username","password", this);
                sftp.SetMode(2);
                sftp.execute(folder_device, filename, folder_server, filesize);
            }
        }
        else if(requestCode == UploadClick_result){

        }
        else if(requestCode == MkdirClick_result){
            if(resultCode == Refresh_code){
                Search_List(server_folder.get(server_folder_count));
            }
        }
        else if(requestCode == FileSetting_result){
            if(resultCode == Refresh_code){
                Search_List(server_folder.get(server_folder_count));
            }
        }
        else if(requestCode == RecycleBin_result){
            if(resultCode == Refresh_code){
                Search_List(server_folder.get(server_folder_count));
            }
        }
    }
    public void OnPopupClick(String filename, long filesize){
        Intent intent = new Intent(FileActivity.this, Popup_fileActivity.class);
        intent.putExtra("folder_device",folder_device);
        intent.putExtra("folder_server",server_folder.get(server_folder_count));
        intent.putExtra("data",filename);
        intent.putExtra("size",Long.toString(filesize));
        startActivityForResult(intent,PopupClick_result);
        overridePendingTransition(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_button:
                Toast.makeText(this, "Upload button click", Toast.LENGTH_LONG).show();
                System.out.println("Upload button click");
                Intent Upload_intent = new Intent(FileActivity.this, FileUploadActivity.class);
                Upload_intent.putExtra("Path",folder_device);
                Upload_intent.putExtra("Local_path",Local_device);
                Upload_intent.putExtra("Server_Path",server_folder.get(server_folder_count));
                if(switch_check){
                    Upload_intent.putExtra("private_stat","public");
                }
                else{
                    Upload_intent.putExtra("private_stat","private");
                    Upload_intent.putExtra("fnumber",fnumber);
                }

                startActivityForResult(Upload_intent, UploadClick_result);
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                break;
            case R.id.mkdir_button:
                Toast.makeText(this, "Make folder button click", Toast.LENGTH_LONG).show();
                System.out.println("Make folder button click");
                Intent mkdir_intent = new Intent(FileActivity.this, MakeFolderActivity.class);
                mkdir_intent.putExtra("Path",server_folder.get(server_folder_count));
                mkdir_intent.putExtra("Mode","1");
                startActivityForResult(mkdir_intent, MkdirClick_result);
                overridePendingTransition(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop);
                break;
            case R.id.refresh_button:
                Toast.makeText(this, "새로고침 중입니다.",Toast.LENGTH_LONG).show();
                Search_List(server_folder.get(server_folder_count));
                break;
            case R.id.back_button:
                finish();
                overridePendingTransition(R.anim.fragment_enter, R.anim.fragment_exit);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fileactivity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}