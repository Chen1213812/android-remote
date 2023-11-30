package com.example.robot_remote;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.robot_remote.util.ToastUtil;
import java.io.IOException;

//public class control extends AppCompatActivity implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener {
@SuppressLint("HandlerLeak")
public class control extends AppCompatActivity implements View.OnClickListener {

    public static String data_flag = "";
    public static String receivedata = "";
    public static String receivedata_spp = "";
    private static TextView data1;
    private static TextView data2;
    private static TextView data3;
    private static TextView data4;
    private static TextView data5;
    private static TextView data6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        MainActivity.transmit_data2[0] = MainActivity.begin_flag1;
        MainActivity.transmit_data2[1] = MainActivity.begin_flag2;
        MainActivity.transmit_data2[13] = MainActivity.end_flag1;
        MainActivity.transmit_data2[14] = MainActivity.end_flag2;//初始标志位

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchb1 = findViewById(R.id.switch1);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchb2 = findViewById(R.id.switch2);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchb3 = findViewById(R.id.switch3);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchb4 = findViewById(R.id.switch4);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchb5 = findViewById(R.id.switch5);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switchb6 = findViewById(R.id.switch6);


/*
        findViewById(R.id.button1).setOnTouchListener(this);
        findViewById(R.id.button2).setOnTouchListener(this);
        findViewById(R.id.button3).setOnTouchListener(this);
        findViewById(R.id.button4).setOnTouchListener(this);
        findViewById(R.id.button5).setOnTouchListener(this);
        findViewById(R.id.button6).setOnTouchListener(this);
        findViewById(R.id.button7).setOnTouchListener(this);
        findViewById(R.id.button8).setOnTouchListener(this);
        findViewById(R.id.button9).setOnTouchListener(this);
        findViewById(R.id.button10).setOnTouchListener(this);
        findViewById(R.id.button11).setOnTouchListener(this);
        findViewById(R.id.button12).setOnTouchListener(this);
        findViewById(R.id.button13).setOnTouchListener(this);
        findViewById(R.id.button14).setOnTouchListener(this);
        findViewById(R.id.button15).setOnTouchListener(this);
        findViewById(R.id.button16).setOnTouchListener(this);
        findViewById(R.id.button17).setOnTouchListener(this);
        findViewById(R.id.button18).setOnTouchListener(this);
        findViewById(R.id.button19).setOnTouchListener(this);
        findViewById(R.id.button20).setOnTouchListener(this);
*/

        findViewById(R.id.button1).setOnClickListener(this);//点击事件
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button10).setOnClickListener(this);

        findViewById(R.id.button11).setOnClickListener(this);
        findViewById(R.id.button12).setOnClickListener(this);
        findViewById(R.id.button13).setOnClickListener(this);
        findViewById(R.id.button14).setOnClickListener(this);
        findViewById(R.id.button15).setOnClickListener(this);
        findViewById(R.id.button16).setOnClickListener(this);
        findViewById(R.id.button17).setOnClickListener(this);
        findViewById(R.id.button18).setOnClickListener(this);
        findViewById(R.id.button19).setOnClickListener(this);
        findViewById(R.id.button20).setOnClickListener(this);

        Button start_button =  findViewById(R.id.start_button);
//------------------------------开关按键监听-----------------------------------------------
        switchb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[3] = 0x01;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                } else {
                    MainActivity.transmit_data2[3] = 0x00;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });
        switchb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[4] = 0x01;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                } else {
                    MainActivity.transmit_data2[4] = 0x00;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });
        switchb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[5] = 0x01;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                } else {
                    MainActivity.transmit_data2[5] = 0x00;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });
        switchb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[6] = 0x01;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                } else {
                    MainActivity.transmit_data2[6] = 0x00;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });
        switchb5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[7] = 0x01;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                } else {
                    MainActivity.transmit_data2[7] = 0x00;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });
        switchb6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[8] = 0x01;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                } else {
                    MainActivity.transmit_data2[8] = 0x00;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });

        data1 = findViewById(R.id.data1);
        data2 = findViewById(R.id.data2);
        data3 = findViewById(R.id.data3);
        data4 = findViewById(R.id.data4);
        data5 = findViewById(R.id.data5);
        data6 = findViewById(R.id.data6);

        RadioButton radioButton1 = findViewById(R.id.radiobutton1);
        RadioButton radioButton2 = findViewById(R.id.radiobutton2);
        RadioButton radioButton3 = findViewById(R.id.radiobutton3);

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[9] = 0x01;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });

        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[9] = 0x02;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });

        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.transmit_data2[9] = 0x03;
                    ToastUtil.show(control.this, "发送");
                    new Thread(() -> {
                        if(MainActivity.link_flag == 0x01)
                        {
                            try {
                                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                                MainActivity.outputStream.write(MainActivity.transmit_data2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else
                        {
                            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
                            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        }
                    }).start();
                }
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifi.ServerSocket_thread serversocket_thread = new wifi.ServerSocket_thread();
                serversocket_thread.start();
                ToastUtil.show(control.this, "WiFi服务已开启");
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button1)
        {
            MainActivity.transmit_data2[2] = 0x01;
        }
        if(view.getId() == R.id.button2)
        {
            MainActivity.transmit_data2[2] = 0x02;
        }
        if(view.getId() == R.id.button3)
        {
            MainActivity.transmit_data2[2] = 0x03;
        }
        if(view.getId() == R.id.button4)
        {
            MainActivity.transmit_data2[2] = 0x04;
        }
        if(view.getId() == R.id.button5)
        {
            MainActivity.transmit_data2[2] = 0x05;
        }
        if(view.getId() == R.id.button6)
        {
            MainActivity.transmit_data2[2] = 0x06;
        }
        if(view.getId() == R.id.button7)
        {
            MainActivity.transmit_data2[2] = 0x07;
        }
        if(view.getId() == R.id.button8)
        {
            MainActivity.transmit_data2[2] = 0x08;
        }
        if(view.getId() == R.id.button9)
        {
            MainActivity.transmit_data2[2] = 0x09;
        }
        if(view.getId() == R.id.button10)
        {
            MainActivity.transmit_data2[2] = 0x10;
        }
        if(view.getId() == R.id.button11)
        {
            MainActivity.transmit_data2[2] = 0x11;
        }
        if(view.getId() == R.id.button12)
        {
            MainActivity.transmit_data2[2] = 0x12;
        }
        if(view.getId() == R.id.button13)
        {
            MainActivity.transmit_data2[2] = 0x13;
        }
        if(view.getId() == R.id.button14)
        {
            MainActivity.transmit_data2[2] = 0x14;
        }
        if(view.getId() == R.id.button15)
        {
            MainActivity.transmit_data2[2] = 0x15;
        }
        if(view.getId() == R.id.button16)
        {
            MainActivity.transmit_data2[2] = 0x16;
        }
        if(view.getId() == R.id.button17)
        {
            MainActivity.transmit_data2[2] = 0x17;
        }
        if(view.getId() == R.id.button18)
        {
            MainActivity.transmit_data2[2] = 0x18;
        }
        if(view.getId() == R.id.button19)
        {
            MainActivity.transmit_data2[2] = 0x19;
        }
        if(view.getId() == R.id.button20)
        {
            MainActivity.transmit_data2[2] = 0x20;
        }
        new Thread(() -> {
        if(MainActivity.link_flag == 0x01)
        {
            try {
                MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                MainActivity.outputStream.write(MainActivity.transmit_data2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else
        {
            spp.mGattCharacteristic.setValue(MainActivity.transmit_data2);
            spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
        }
        MainActivity.transmit_data2[2] = 0x00;
        }).start();
        ToastUtil.show(control.this, "发送成功");
    }

    //---------------------------数据处理显示进程------------------------------------------------
    @SuppressLint("HandlerLeak")
    static Handler handler= new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MainActivity.link_flag == 0x01) {
                data_flag = receivedata.substring(0, 1);

                if (data_flag.equals("1")) {
                //Log.d("ceshi",receivedata);
                    data1.setText(receivedata.substring(1));
                }
                if (data_flag.equals("2")) {
                    data2.setText(receivedata.substring(1));
                }
                if (data_flag.equals("3")) {
                    data3.setText(receivedata.substring(1));
                }
                if (data_flag.equals("4")) {
                    data4.setText(receivedata.substring(1));
                }
                if (data_flag.equals("5")) {
                    data5.setText(receivedata.substring(1));
                }
                if (data_flag.equals("6")) {
                    data6.setText(receivedata.substring(1));
                }
            }
            else
            {
                data_flag = receivedata_spp.substring(0, 1);
                if (data_flag.equals("1")) {
                Log.d("ceshi",receivedata_spp);
                    data1.setText(receivedata_spp.substring(1));
                }
                if (data_flag.equals("2")) {
                    data2.setText(receivedata_spp.substring(1));
                }
                if (data_flag.equals("3")) {
                    data3.setText(receivedata_spp.substring(1));
                }
                if (data_flag.equals("4")) {
                    data4.setText(receivedata_spp.substring(1));
                }
                if (data_flag.equals("5")) {
                    data5.setText(receivedata_spp.substring(1));
                }
                if (data_flag.equals("6")) {
                    data6.setText(receivedata_spp.substring(1));
                }
            }
        }
    };

    public static void spp_data_change() {
        data_flag = receivedata_spp.substring(0, 1);
        if (data_flag.equals("1")) {
            Log.d("ceshi",receivedata_spp);
            data1.setText(receivedata_spp.substring(1));
        }
        if (data_flag.equals("2")) {
            data2.setText(receivedata_spp.substring(1));
        }
        if (data_flag.equals("3")) {
            data3.setText(receivedata_spp.substring(1));
        }
        if (data_flag.equals("4")) {
            data4.setText(receivedata_spp.substring(1));
        }
        if (data_flag.equals("5")) {
            data5.setText(receivedata_spp.substring(1));
        }
        if (data_flag.equals("6")) {
            data6.setText(receivedata_spp.substring(1));
        }
    }
}
//--------------------------------------------复选按钮监听----------------------------

/*
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId() == R.id.button1)
        {
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
            {
                MainActivity.transmit_data2[1] = 0x01;
                ToastUtil.show(control.this, "发送");
                new Thread(() -> {
                    try {
                        MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                        MainActivity.outputStream.write(MainActivity.transmit_data2);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
            else
            {
                MainActivity.transmit_data2[1] = 0x00;
                ToastUtil.show(control.this, "发送");
                new Thread(() -> {
                    try {
                        MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
                        MainActivity.outputStream.write(MainActivity.transmit_data2);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }

        }
        return false;
    }
*/

//------------------------------开关按键监听-----------------------------------------------
//    @Override
//    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//        if(isChecked)
//        {
//            MainActivity.transmit_data2[1] = '1';
//            ToastUtil.show(control.this, "发送");
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
//                        MainActivity.outputStream.write((MainActivity.begin_flag2 + String.valueOf(MainActivity.transmit_data2) + MainActivity.end_flag2).getBytes());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }.start();
//        }
//        else
//        {
//            MainActivity.transmit_data2[1] = '1';
//            ToastUtil.show(control.this, "发送");
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        MainActivity.outputStream = wifi.clicksSocket.getOutputStream();
//                        MainActivity.outputStream.write((MainActivity.begin_flag2 + String.valueOf(MainActivity.transmit_data2) + MainActivity.end_flag2).getBytes());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }.start();
//        }
//    }