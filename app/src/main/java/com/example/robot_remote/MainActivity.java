package com.example.robot_remote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.robot_remote.util.ToastUtil;

import java.io.InputStream;
import java.io.OutputStream;//------------------------------------------
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static OutputStream outputStream;//创建输出数据流
    public static InputStream inputstream;//创建输入数据流
//    public static String transmit_data = ""; //发送数据包
    public static byte [] transmit_data2 = new byte[15]; //发送数据包2
    public static byte begin_flag1 = (byte) 0xaa;
    public static byte end_flag1 = (byte) 0xaf;
    public static byte begin_flag2 = (byte) 0xbb;
    public static byte end_flag2 = (byte) 0xbf;
//    public static String begin_flag1 = "0x20 0x0f 0x00";
//    public static String end_flag1 = "0x7c 0x9f 0x02";

    public static byte link_flag = 0x01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bluetooth).setOnClickListener(this);
        findViewById(R.id.wifi).setOnClickListener(this);
        findViewById(R.id.spp).setOnClickListener(this);
        findViewById(R.id.control).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.bluetooth)
        {
            startActivity(new Intent(this, bluetooth.class));
        }
        if(view.getId()==R.id.wifi)
        {
            link_flag = 0x01;
            ToastUtil.show(MainActivity.this, "切换WIFI通讯");
            startActivity(new Intent(this, wifi.class));
        }
        if(view.getId()==R.id.spp)
        {
            link_flag = 0x00;
            ToastUtil.show(MainActivity.this, "切换蓝牙通讯");
            startActivity(new Intent(this, spp.class));
        }
        if(view.getId()==R.id.control)
        {
            startActivity(new Intent(this, control.class));
        }
    }


}

