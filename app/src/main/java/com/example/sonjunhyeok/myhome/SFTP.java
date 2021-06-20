package com.example.sonjunhyeok.myhome;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.sonjunhyeok.myhome.FileServer.GridListAdapter;
import com.example.sonjunhyeok.myhome.FileServer.ListItem;
import com.example.sonjunhyeok.myhome.FileServer.file_class;
import com.jcraft.jsch.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

public class SFTP extends AsyncTask<String, Void, ArrayList<file_class>> {
    private String hostname;
    private String username;
    private String password;
    private int mode;
    private Context context;

    private JSch jsch;
    private Session session;
    private Channel channel;
    private ChannelSftp channelSftp;
    private ChannelExec channelExec;
    private StringBuilder stringBuilder;
    private InputStream inputStream;

    private ProgressDialog progressDialog;
    private GridListAdapter adapter;


    public SFTP(String hostname, String username, String password, Context context){
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.context = context;
    }
    public void SetMode(int mode){
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        if(mode == 3 || mode == 2) {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        else{
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context,"취소 중 입니다.",Toast.LENGTH_LONG).show();
                if(mode == 0){
                    DisConnect(1);
                }
                else{
                    DisConnect(2);
                }
            }
        });
        progressDialog.setMessage("로딩 중 입니다.");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<file_class> doInBackground(final String... strings) {
        channelSftp = null;
        inputStream = null;

        ArrayList<file_class> fileArray = new ArrayList<>();

        try{
            jsch = new JSch();
            session = jsch.getSession(username, hostname, 22);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking","no");
            session.setConfig(config);
            session.connect();

            switch(mode){
                case 0:
                    channel = session.openChannel("exec");
                    channelExec = (ChannelExec)channel;
                    channelExec.setPty(true);
                    channelExec.setCommand("mkdir "+strings[0]);
                    stringBuilder = new StringBuilder();
                    inputStream = channel.getInputStream();
                    ((ChannelExec) channel).getErrStream();

                    channel.connect();

                    break;
                case 1://file list search
                    long start = System.currentTimeMillis();
                    channel = session.openChannel("sftp");
                    channel.connect();
                    channelSftp = (ChannelSftp)channel;
                    Vector<ChannelSftp.LsEntry> list = channelSftp.ls(strings[0]);
                    long end = System.currentTimeMillis();

                    System.out.println("in sftp frist time : "+(end-start));
                    start = System.currentTimeMillis();
                    for(ChannelSftp.LsEntry entry : list) {
                        file_class file_class = new file_class();
                        if(!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                            file_class.Set_name(entry.getFilename());
                            if (entry.getAttrs().isDir()) {
                                file_class.Set_type(true);
                            } else {
                                file_class.Set_type(false);
                            }
                            fileArray.add(file_class);
                        }
                    }
                    end = System.currentTimeMillis();
                    System.out.println("in sftp second time : " + (end-start));
                    break;
                case 2://file download
                    channel = session.openChannel("sftp");
                    channel.connect();
                    channelSftp = (ChannelSftp)channel;
                    channelSftp.cd(strings[2]);
                    System.out.println("strings3 : " + strings[3]);
                    channelSftp.get(strings[1], strings[0] + strings[1], new SftpProgressMonitor() {
                        private long max = Long.valueOf(strings[3]);
                        private long count = 0;
                        private long percent = 0;
                        @Override
                        public void init(int i, String s, String s1, long l) {
                            //this.max = Long.valueOf(strings[3]);
                        }

                        @Override
                        public boolean count(long l) {
                            this.count += l;
                            long percentNow = this.count*100/max;
                            if(percentNow>this.percent){
                                this.percent = percentNow;
                                progressDialog.setProgress((int)this.percent);
                                System.out.println("progress : " + this.percent); // 프로그래스
                            }
                            return true;
                        }

                        @Override
                        public void end() {

                        }
                    });
                    System.out.println("Download file end");
                    channel.disconnect();
                    break;
                case 3://file upload
                    channel = session.openChannel("sftp");
                    channel.connect();
                    channelSftp = (ChannelSftp) channel;
                    channelSftp.put(strings[0], strings[1], new SftpProgressMonitor() {
                        private long max = 0;
                        private long count = 0;
                        private long percent = 0;
                        @Override
                        public void init(int i, String s, String s1, long l) {
                            this.max = l;
                        }

                        @Override
                        public boolean count(long bytes) {
                            this.count += bytes;
                            long percentNow = this.count*100/max;
                            if(percentNow>this.percent){
                                this.percent = percentNow;
                                progressDialog.setProgress((int)this.percent);
                                System.out.println("progress : " + this.percent); // 프로그래스
                            }
                            return true;
                        }

                        @Override
                        public void end() {
                            System.out.println("Upload finish");
                            return;
                        }
                    });
                    break;
                case 4://file delete
                    channel = session.openChannel("exec");
                    channelExec = (ChannelExec)channel;
                    channelExec.setPty(true);
                    channelExec.setCommand("rm -r "+strings[0]);
                    stringBuilder = new StringBuilder();
                    inputStream = channel.getInputStream();
                    ((ChannelExec) channel).getErrStream();

                    channel.connect();
                    break;
                case 5://move to recycle bin
                    channel = session.openChannel("exec");
                    channelExec = (ChannelExec)channel;
                    channelExec.setPty(true);
                    channelExec.setCommand("mv "+strings[0]+strings[1]);
                    System.out.println("mv "+strings[0]+" /home/disk1/home/휴지통/"+strings[1]);
                    stringBuilder = new StringBuilder();
                    inputStream = channel.getInputStream();
                    ((ChannelExec) channel).getErrStream();

                    channel.connect();
                    break;
                case 6://file rename & move
                    channel = session.openChannel("exec");
                    channelExec = (ChannelExec)channel;
                    channelExec.setPty(true);
                    channelExec.setCommand("mv "+strings[0]+" "+strings[1]);
                    System.out.println("mv "+strings[0]+" "+strings[1]);
                    System.out.println("str1 : " + strings[0] + " str2 : " + strings[1]);
                    stringBuilder = new StringBuilder();
                    inputStream = channel.getInputStream();
                    ((ChannelExec) channel).getErrStream();

                    channel.connect();
                    break;
            }
        } catch (JSchException | SftpException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileArray;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        System.out.println("onCancelled is work");

        if(mode == 0 || mode == 4){
            DisConnect(1);
        }
        else{
            DisConnect(2);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<file_class> file_classes) {
        super.onPostExecute(file_classes);
        progressDialog.dismiss();
        Toast.makeText(this.context, "작업 완료",Toast.LENGTH_LONG).show();
    }
    public void DisConnect(int mode){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("DisConnection is work");
        /*if(session.isConnected()) {
            session.disconnect();
        }*/
        if(channel.isConnected()){
            channel.disconnect();
        }
        if(mode == 1) {
            if (!channelExec.isEOF() && channelExec.isConnected()) {
                channelExec.disconnect();
            }
        }
        else{
            if (!channelSftp.isEOF() && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
        }
    }
}