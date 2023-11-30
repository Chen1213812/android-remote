package com.example.robot_remote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.robot_remote.adapter.SeacherDeviceAdapter;
import com.example.robot_remote.util.BleUtil;
import com.example.robot_remote.util.DigitalTrans;
import com.example.robot_remote.util.GpsUtil;
import com.example.robot_remote.util.LogUtil;
import com.example.robot_remote.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class bluetooth extends AppCompatActivity implements View.OnClickListener {

    private BluetoothAdapter bluetoothAdapter;
    private static int REQUEST_ENABLE_BT = 1; // 打开蓝牙页面请求代码
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1; // 位置权限
    private static final int SET_GPS_OPEN_STATE = 2; // 设置GPS是否打开了
//    private static final int REQUEST_STORY_CODE = 3; // 文件读取权限

    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 5000; // 蓝牙扫描停止时间 10S

    private RecyclerView recyclerView;
    private SeacherDeviceAdapter adapter;

    private List<BluetoothDevice> deviceData = new ArrayList<>();

    public static int delay = 1500;

    private byte change_flag = 0x01;


//    public static BluetoothGattCharacteristic mGattCharacteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blue_tooth);

        initView();
        initDevice();
    }
    private void initView() {
        findViewById(R.id.btn_seach).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);

        recyclerView = findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SeacherDeviceAdapter(this, new SeacherDeviceAdapter.CallBack() {
            @Override
            public void onItemClick(int position, BluetoothDevice device) {
                bindBlueTooth(device); // 绑定选中的蓝牙设备
            }

        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_seach)
        {
            seach();
        }
        if(view.getId() == R.id.btn_back)
        {
            Intent intent = new Intent(bluetooth.this,MainActivity.class);
            startActivity(intent);
        }

    }

    private void initDevice() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    //----------------------------------------------------------------
    @SuppressLint("MissingPermission")
    private void seach() {
        //1. 当前设备是否支持BLE蓝牙设备
        if (BleUtil.checkDeviceSupportBleBlueTooth(this)) {
           // ToastUtil.show(this, "支持BLE蓝牙功能！");
            // 2.判断蓝牙设备是否打开了
            if (checkBlueIsOpen()) {
                //ToastUtil.show(this, "蓝牙已打开");
                // 3.断设备的api是否需要开启定位权限
                checkGPS();
            } else { // 没有打开,跳转到系统蓝牙页面
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                registerForActivityResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            ToastUtil.show(this, "当前设备不支持BLE蓝牙功能！");
        }
    }

    /**
     * GPS是否开启了
     */
    private void checkGPS() {
        // 3.1GPS是否打开了
        if (GpsUtil.isOPen(this)) { // GPS已经开启了
            //ToastUtil.show(this, "GPS开启！");
            checkGpsPermission();
        } else {
            // 3.2是否拥有GPS权限，需要使用GPS才能使用蓝牙设备
            tipGPSSetting();
        }
    }

    /**
     * 蓝牙是否打开了
     *
     * @return true 打开了，false 没有打开
     */
    private boolean checkBlueIsOpen() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 搜索蓝牙设备
     * 创建搜索callback 返回扫描到的信息
     * 创建定时任务，在指定时间内结束蓝牙扫描，蓝牙扫描是一个很耗电的操作！
     */
    @SuppressLint("MissingPermission")
    private void seachBlueTooth() {
        ToastUtil.show(this, "开始搜索蓝牙设备");
        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(null, createScanSetting(), scanCallback);

        bluetoothAdapter.startDiscovery();

        handler.postDelayed(new Runnable() { // 指定时间内停止蓝牙搜索
            @Override
            public void run() {
                closeSeach();
            }
        }, SCAN_PERIOD);

        deviceData.clear();
    }

    /**
     * 回调
     */
    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            LogUtil.i("name:" + result.getDevice().getName() + ";强度：" + result.getRssi());

            if (device != null) {

                if (deviceData.size() > 0) {

                    if (!deviceData.contains(device)) { // 扫描到会有很多重复的数据,剔除，只添加第一次扫描到的设备
                        deviceData.add(device);
                    }
                } else {
                    deviceData.add(device);
                }
                adapter.setData(deviceData);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    /**
     * 停止扫描
     */
    @SuppressLint("MissingPermission")
    private void closeSeach() {
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(scanCallback);
            //ToastUtil.show(this, (SCAN_PERIOD / 1000) + "秒搜索时间已到，停止搜索");
        }
    }

    /**
     * 扫描广播数据设置
     *
     * @return
     */
    public ScanSettings createScanSetting() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
//        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER); // 耗电最少，扫描时间间隔最短
//        builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED); // 平衡模式，耗电适中，扫描时间间隔一般，我使用这种模式来更新设备状态
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);//最耗电，扫描延迟时间短，打开扫描需要立马返回结果可以使用
        //builder.setReportDelay(100);//设置延迟返回时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        }
        return builder.build();
    }

    /**
     * 提示需要开启蓝牙
     */
    private void tipGPSSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(bluetooth.this);
        builder.setTitle("提示");
        builder.setMessage("安卓6.0以后使用蓝牙需要开启定位功能，但本应用不会使用到您的位置信息，开始定位只是为了扫描到蓝牙设备。是否确定打开");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GpsUtil.openGPS(bluetooth.this, SET_GPS_OPEN_STATE);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtil.show(bluetooth.this, "您无法使用此功能");
            }
        });
        builder.show();
    }

    /**
     * 蓝牙需要的定位权限
     */
    private void checkGpsPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) { // 如果当前版本是9.0（包含）以下的版本
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, REQUEST_CODE_ACCESS_COARSE_LOCATION);
            } else {
                //ToastUtil.show(this, "android 9.0");
                seachBlueTooth();
            }
        } else {
            // 10.0系统
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED)
            {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, REQUEST_CODE_ACCESS_COARSE_LOCATION);
            } else {
                ToastUtil.show(this, "android 10.0");
                seachBlueTooth();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) { // 从蓝牙页面返回了，在检查一次是否打开了
            if (checkBlueIsOpen()) {
                // 蓝牙打开了
                seach();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(bluetooth.this);
                builder.setTitle("提示");
                builder.setMessage("蓝牙没有打开将无法使用此功能，是否确定打开");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seach(); // 再次执行搜索
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ToastUtil.show(bluetooth.this, "您无法使用此功能");
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } else if (requestCode == SET_GPS_OPEN_STATE) { // GPS是否打开了
            if (GpsUtil.isOPen(this)) { // GPS打开了
                checkGpsPermission();
            } else {
                tipGPSSetting();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // 得到了权限
                    seachBlueTooth();
                } else {
//                    Log.d("ceshi", String.valueOf(grantResults[0]));
//                    ToastUtil.show(this, "错误1！");
                    AlertDialog.Builder builder = new AlertDialog.Builder(bluetooth.this);
                    builder.setTitle("提示");
                    builder.setMessage("安卓6.0以后使用蓝牙需要开启定位功能，但本应用不会使用到您的位置信息，开启定位只是为了扫描到蓝牙设备。是否确定打开");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            launchAppDetailsSettings(bluetooth.this);
                        }
                    });

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ToastUtil.show(bluetooth.this, "您无法使用此功能");
                        }
                    });
                    builder.setCancelable(false);
                    builder.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * 跳转权限Activity
     */
    public void launchAppDetailsSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));

        if (!isIntentAvailable(this, intent)) {
            ToastUtil.show(this, "请手动跳转到权限页面，给予权限!");
            return;
        }
        activity.startActivity(intent);
    }

    /**
     * 意图是否可用
     *
     * @param intent The intent.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public boolean isIntentAvailable(Activity activity, Intent intent) {
        return activity
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    // ===================================================================================================================
    public static BluetoothGatt mBluetoothGatt; // 自定义Gatt实现类
    private BluetoothGattCallback mGattCallback = new mWBluetoothGattCallback();
    //定义重连次数
    private int reConnectionNum = 0;
    //最多重连次数
    private int maxConnectionNum = 3;
    private BluetoothDevice mBluetoothDevice;

    private final String UUDI_1 = "91680001-1111-6666-8888-0123456789AB"; // 服务1

    private final String UUDI_2 = "0000ffe0-0000-1000-8000-00805f9b34fb"; // 服务2

    private String CHARACTERISTIC_UUID_1 = "91680003-1111-6666-8888-0123456789AB"; // 特征1

    private String CHARACTERISTIC_UUID_2 = "0000ffe1-0000-1000-8000-00805f9b34fb"; // 特征2

//    public static BluetoothGattCharacteristic gattCharacteristic;
    /**
     * 绑定蓝牙
     *
     * @param device
     */
    @SuppressLint("MissingPermission")
    private void bindBlueTooth(BluetoothDevice device) {
        //连接设备
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
            ToastUtil.show(this, "方式1连接");
        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
            ToastUtil.show(this, "方式2连接");
        }
    }

    //定义蓝牙Gatt回调类
    public class mWBluetoothGattCallback extends BluetoothGattCallback {
        //连接状态回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // status 用于返回操作是否成功,会返回异常码。
            // newState 返回连接状态，如BluetoothProfile#STATE_DISCONNECTED、BluetoothProfile#STATE_CONNECTED

            runOnUiThread(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    //操作成功的情况下
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //判断是否连接码
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            runOnUiThread(new Runnable() {
                                @SuppressLint("MissingPermission")
                                @Override
                                public void run() {
                                    ToastUtil.show(bluetooth.this, "蓝牙已连接");
//                                    LogUtil.i("设备已连接上，开始扫描服务");
                                    // 发现服务
                                    mBluetoothGatt.discoverServices();
                                }
                            });
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            //判断是否断开连接码
                            ToastUtil.show(bluetooth.this, "连接已断开");
                        }
                    } else {
                        //异常码
                        // 重连次数不大于最大重连次数
                        if (reConnectionNum < maxConnectionNum) {
                            // 重连次数自增
                            reConnectionNum++;
                            LogUtil.i("重新连接：" + reConnectionNum);
                            // 连接设备
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                mBluetoothGatt = mBluetoothDevice.connectGatt(bluetooth.this,
                                        false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
                            } else {
                                mBluetoothGatt = mBluetoothDevice.connectGatt(bluetooth.this, false, mGattCallback);
                            }
                        } else {
                            // 断开连接，失败回调
                            ToastUtil.show(bluetooth.this, "蓝牙连接失败，建议重启APP，或者重启蓝牙，或重启设备");
                            closeBLE();
                        }

                    }
                }
            });
        }

        //服务发现回调
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread(new Runnable() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {

                        LogUtil.i("mmmm:" + mBluetoothGatt.getServices().size());//获取   UUID数量
                        for (int i = 0; i < mBluetoothGatt.getServices().size(); i++) {
                            LogUtil.i("mmmm service:" + mBluetoothGatt.getServices().get(i).getUuid());//获取服务号


                            for (int k = 0; k < mBluetoothGatt.getServices().get(i).getCharacteristics().size(); k++) {
                                LogUtil.i("mmmm      Characteristic:" + mBluetoothGatt.getServices().get(i).getCharacteristics().get(k).getUuid());
                            }//获取特征号
                        }
                        //获取指定uuid的service
                        BluetoothGattService gattService = mBluetoothGatt.getService(UUID.fromString(UUDI_1));
//                        bluetoothGattServiceList.add(gattService);
                        //获取到特定的服务不为空
                        if (gattService != null) {
                            LogUtil.i("获取服务成功!");

                            BluetoothGattCharacteristic gattCharacteristic =
                                    gattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID_1));
                            //BluetoothGattCharacteristic mGattCharacteristic = gattCharacteristic;

                            if (gattCharacteristic != null) {
                                LogUtil.i("获取特征成功!");

                                @SuppressLint("MissingPermission") boolean isEnableNotification = mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);

                                if (isEnableNotification) {
                                    LogUtil.i("开启通知成功!");
//                                    //通过GATt实体类将，特征值写入到外设中。
////                                    mBluetoothGatt.writeCharacteristic(gattCharacteristic);
//                                    //如果只是需要读取外设的特征值：
//                                    //通过Gatt对象读取特定特征（Characteristic）的特征值
                                    mBluetoothGatt.readCharacteristic(gattCharacteristic);
                                } else {
                                    LogUtil.i("开启通知失败!");
                                }

                            } else {
                                LogUtil.i("获取特征失败!");
                            }

                        } else {
                            //获取特定服务失败
                            LogUtil.i("获取服务失败!");
                        }
                    }
                });
            }

        }

//        @Override
//        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            super.onCharacteristicRead(gatt, characteristic, status);
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                LogUtil.i("获取读取到的特征值成功!");
//                //获取读取到的特征值
//                characteristic.getValue();
//                LogUtil.i("获取读取特征Value：" + characteristic.getValue().toString());
//            } else {
//                LogUtil.i("获取读取到的特征值失败!");
//            }
//        }

        //特征写入回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        LogUtil.i("特征写入回调成功！");
                       // ToastUtil.show(MainActivity.this, "写入特征成功");
                        //获取写入到外设的特征值
                        characteristic.getValue();
                    } else {
                        LogUtil.i("特征写入回调失败！");
                    }
                }
            });
        }

        //外设特征值改变回调
        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            final byte[] value = characteristic.getValue();

            new Thread(() -> {
                // 需要执行的方法
                try
                {
                    //获取输出流
//                        MainActivity.outputStream = MainActivity2.clicksSocket.getOutputStream();
                    //发送数据
//                        MainActivity.transmit_data = DigitalTrans.byte2hex(value);


                    if(MainActivity.link_flag == 0x01)
                    {
                        MainActivity.outputStream = wifi.clicksSocket.getOutputStream();//通道建立
                        Log.d("ceshi", Arrays.toString(value));
                        MainActivity.outputStream.write(value,0,15);//数据发送---------------
//                        if(value[3]==-128&&value[4]==-128&&value[5]==-128&&value[6]==-128)
//                        {
//                            for(int i=10;i>0;i--)
//                            {
//                                MainActivity.outputStream.write(value,0,15);
//                            }
//                        }


                    }
                    else
                    {
                        spp.mGattCharacteristic.setValue(value);
                        //Log.d("ceshi", String.valueOf(value[3]));
                        spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                        if(value[3]==-128&&value[4]==-128&&value[5]==-128&&value[6]==-128)
                        {
                            for(int i=10;i>0;i--)
                            {
                                value[3]=-128;
                                value[4]=-128;
                                value[5]=-128;
                                value[6]=-128;
                                spp.mGattCharacteristic.setValue(value);
                                spp.mBluetoothGatt.writeCharacteristic(spp.gattCharacteristic);
                                for(delay = 1500;delay>0;delay--);
                                delay = 1500;
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }).start();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    // value为设备发送的数据，根据数据协议进行解析
////                    LogUtil.i("原始数据：" + new String(characteristic.getValue()));
////                    LogUtil.i("设备发送数据：" + DigitalTrans.byte2hex(value));//数据获取
//                }
//            });
        }

        //描述写入回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            LogUtil.i("开启监听成功");
        }
    }

    /**
     * 关闭BLE蓝牙连接
     */
    public void closeBLE() {
        if (mBluetoothGatt == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        ToastUtil.show(this, "蓝牙已断开");
    }
}


