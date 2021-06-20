package com.example.sonjunhyeok.myhome.FileServer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sonjunhyeok.myhome.R;

public class RenameInputActivity extends AppCompatActivity {

    private int RESULT_CLOSE = 0;
    private int RESULT_CHANGE = 1;

    private String filename;

    private TextView Rename_textView;
    private EditText RENAME_EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rename_input);

        RENAME_EditText = findViewById(R.id.editRenameText);
    }

    @Override
    public void onBackPressed() {
        return ;
    }
    public void mOnRenameClose(View view){
        setResult(RESULT_CLOSE);
        finish();
    }
    public void mOnRenameOK(View view){
        filename = RENAME_EditText.getText().toString();
        System.out.println("rename activity filename : " + filename);
        Intent intent = new Intent();
        intent.putExtra("filename",filename);
        setResult(RESULT_CHANGE, intent);
        finish();
    }
}