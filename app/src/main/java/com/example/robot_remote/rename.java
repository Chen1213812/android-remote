package com.example.robot_remote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.InputStream;

public class rename extends AppCompatActivity implements View.OnClickListener {

    EditText buttonname;  //端口号编辑框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);

        buttonname = (EditText) findViewById(R.id.buttonname);
        findViewById(R.id.continuebutton).setOnClickListener(this);
        findViewById(R.id.endbotton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.continuebutton)
        {
            String inputText=buttonname.getText().toString();
        }
    }
}