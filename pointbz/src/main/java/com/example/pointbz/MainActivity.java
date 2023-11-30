package com.example.pointbz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static byte clear_fllag = 0x00;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView data = findViewById(R.id.data);
        findViewById(R.id.clear).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
                   }
}