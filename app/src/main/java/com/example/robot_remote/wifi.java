package com.example.robot_remote;

import androidx.appcompat.app.AppCompatActivity;
import static com.example.robot_remote.MainActivity.inputstream;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robot_remote.util.DigitalTrans;
import com.example.robot_remote.util.ToastUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class wifi extends AppCompatActivity {


    public static ServerSocket serverSocket;//创建ServerSocket对象
    public static Socket clicksSocket;//连接通道，创建Socket对象

    public static boolean socket_thread = false; //读取连接状态
    public static boolean bRun = true; //运行状态
    public static boolean bThread = false; //读取线程状态


    Button startButton;  //启动服务按钮
    EditText portEditText;  //端口号编辑框
    TextView ip_Text;//IP地址
    Button ReturnMVButton;//返回按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);


        /**
         * 读一下手机wifi状态下的ip地址，只有知道它的ip才能连接它嘛
         */
        Toast.makeText(wifi.this, getLocalIpAddress(), Toast.LENGTH_SHORT).show();

        startButton = (Button) findViewById(R.id.start_button);   //启动服务按钮
        ip_Text=(TextView)findViewById(R.id.ip_Text);      //ip地址输入框
        portEditText = (EditText) findViewById(R.id.port_EditText);     //端口号输入框
        startButton.setOnClickListener(startButtonListener);        //启动服务监听
        ReturnMVButton=(Button)findViewById(R.id.return_button);      //返回按钮

        ip_Text.setText(getLocalIpAddress());     //显示服务器IP地址

        /**
         * 切换主界面
         */
        ReturnMVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(wifi.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //刷新socket端口号
        try
        {
            int port =Integer.valueOf(portEditText.getText().toString());//获取portEditText中的端口号
            serverSocket = new ServerSocket(port);//监听port端口，这个程序的通信端口就是port了
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }//---------------------------------------------------oncreate--------------------------------


    /**
     * 启动服务按钮监听事件
     */
    private View.OnClickListener startButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            /**
             * 启动服务器监听线程
             */
            ServerSocket_thread serversocket_thread = new ServerSocket_thread();
            serversocket_thread.start();
            ToastUtil.show(wifi.this, "WiFi服务已开启");

        }
    };

    /**
     * 服务器监听线程
     */
    static class ServerSocket_thread extends Thread
    {
        public void run()//重写Thread的run方法
        {
            while (true)
            {
                try
                {
                    //监听连接 ，如果无连接就会处于阻塞状态，一直在这等着
                    clicksSocket = serverSocket.accept();
                    socket_thread=true; //已有连接 改变状态
                    inputstream = clicksSocket.getInputStream();//获取输入流
                    //启动接收线程
                    Receive_Thread receive_Thread = new Receive_Thread();
                    receive_Thread.start();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * 接收线程
     *
     */
    static class Receive_Thread extends Thread//继承Thread
    {
        public void run()//重写run方法
        {
            while (true)
            {
                try
                {
                    if (bThread == false) {
                        readThread.start();
                        bThread = true;}
                    else {
                        bRun = true;}
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    //    /*****接收wifi数据线程*****/
    static Thread readThread=new Thread(){
        public void run(){
            int num = 0;
            byte[] buffer = new byte[100];
//            byte[] buffer_new = new byte[1024];
            bRun = true;
            try {
                inputstream = clicksSocket.getInputStream();//获取输入流
            } catch (IOException e) {
                e.printStackTrace();
            }

            //接收线程
            while(true){
                try{
                    while(inputstream.available()==0){
                        while(bRun == false){}
                    }
                    while(true){
                        if(!bThread)//跳出循环
                            return;
//                        num = inputstream.read(buffer);         //读入数据
                        control.receivedata = DigitalTrans.bytetoString(buffer);
                        buffer = new byte[100];
                       if(inputstream.available()==0)break;  //短时间没有数据才跳出进行显示
                    }
                    //发送显示消息，进行显示刷新
                    control.handler.sendMessage(control.handler.obtainMessage());
                    //Log.d("ceshi",control.receivedata);
                }catch(IOException e){
                }
            }
        }
    };

    /**
     *
     * 获取WIFI下ip地址
     */
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }


}