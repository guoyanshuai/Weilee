package com.guide.xiaoguo.weilee.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.Param_Mode;
import com.guide.xiaoguo.weilee.service.BleService;
import com.guide.xiaoguo.weilee.utils.Tools;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class Config2_Activity extends AppCompatActivity {

    private UUID serUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private UUID charUuid = UUID.fromString("0000e1ff-0000-1000-8000-00805f9b34fb");
    BleService bleService;
    BluetoothGattCharacteristic gattChar;
    String Sn = "010318040001";
    String config_result = "";

    private Button alarm_tel1_btn;
    private Button alarm_tel2_btn;
    private Button alarm_tel3_btn;
    private Button alarm_tel4_btn;
    private Button alarm_tel5_btn;
    private Button QueryParam2_btn;
    private Button config2_param1_btn;
    private Button config2_param3_btn;

    private EditText alarm_tel1_et;
    private EditText alarm_tel2_et;
    private EditText alarm_tel3_et;
    private EditText alarm_tel4_et;
    private EditText alarm_tel5_et;


    String v0 = "";

    private ServiceConnection conn = new ServiceConnection() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            // TODO Auto-generated method stub
            bleService = ((BleService.LocalBinder) service).getService();
            gattChar = bleService.mBluetoothGatt.getService(serUuid)
                    .getCharacteristic(charUuid);
            bleService.mBluetoothGatt.readCharacteristic(gattChar);
            if (gattChar.getDescriptors().size() != 0) {
                BluetoothGattDescriptor des = gattChar.getDescriptors().get(0);
                des.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bleService.mBluetoothGatt.writeDescriptor(des);
            }
            int prop = gattChar.getProperties();
            if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, false);
            }
            if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, false);
            }
            if ((prop & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, false);
            }
            if ((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
            bleService = null;
        }
    };

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BleService.ACTION_CHAR_READED.equals(action)) {
                final String des1String = intent.getExtras().getString(
                        "desriptor1");
//				final String des2String = intent.getExtras().getString(
//						"desriptor2");
                final String stringValue = intent.getExtras().getString(
                        "StringValue");
                Log.i("---------", "onReceive: " + stringValue);
                final String hexValue = intent.getExtras()
                        .getString("HexValue");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated methdo stub
//                        conf_result.setText("返回的数据:" + hexValue);
                        Log.i("33333333", "run: " + hexValue);
                        Log.i("3333333333222033", "run: " + stringValue);
                        if (hexValue != null && hexValue.length() > 4) {
                            if (hexValue.substring(0, 2).equals("68") &&
                                    hexValue.substring(hexValue.length() - 4, hexValue.length() - 2).equals(Tools.makeChecksum(hexValue.substring(0, hexValue.length() - 4)).toUpperCase())) {
                                config_result = hexValue;
                                Log.i("33333312111113", "run: " + config_result+"\n"+Tools.makeChecksum(hexValue.substring(0, hexValue.length() - 4)));

                                if (config_result.length() > 58) {
                                    //680103160800016880036000000000D616
                                    if (config_result.substring(24, 26).equals("02")) {
                                        PullParam2(config_result);
                                    }
                                } else if (config_result.trim().equals("680103160800016880036000000000D616")) {
                                    Toast.makeText(Config2_Activity.this, "配置成功！", Toast.LENGTH_SHORT).show();
                                }

                                Sn = config_result.substring(2, 14);


                            }
                        }
                        Log.i("11111111111", "Test_Activity: " + stringValue);
                        Log.i("11111111111", "Test_Activity: " + hexValue);
                        Log.i("11111111111", "Test_Activity: " + des1String);
//						descriptor2.setText(des2String);
                    }
                });
            }
            if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(Config2_Activity.this, "设备连接断开",
                        Toast.LENGTH_SHORT).show();
                Log.i("DeviceConnect", "ChangeCharActivity----onReceive: ");
                bleService.disconnect();
            }
        }
    };

    /*参数1  68010316080001688A136013010001000200010010000005021707031800005816  66*/
    /*       68010316080001688A216013020156535969120147820351080147820351080147820351080147820351080000A416*/
    /*参数2  68010316080001688A2160130201565359691201478203510801478203510801478203510801478203510800002916   94*/
    /*参数2  68010316080001688A1860130300000000E803FA00EE02900176B2100A6419AC0D020000EB16  84*/



    public void PullParam2(String param2) {
        String tel1 = "";
        String tel2 = "";
        String tel3 = "";
        String tel4 = "";
        String tel5 = "";
        tel1 = param2.substring(27, 38);
        tel2 = param2.substring(39, 50);
        tel3 = param2.substring(51, 62);
        tel4 = param2.substring(63, 74);
        tel5 = param2.substring(75, 86);
        alarm_tel1_et.setText(tel1);
        alarm_tel2_et.setText(tel2);
        alarm_tel3_et.setText(tel3);
        alarm_tel4_et.setText(tel4);
        alarm_tel5_et.setText(tel5);
    }


    private IntentFilter makeIntentFilter() {
        // TODO Auto-generated method stub
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_CHAR_READED);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
        intentFilter.addAction(BleService.ACTION_GATT_RSSI);
        return intentFilter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_param2);
        ActionBar actionBar = getSupportActionBar();/*隐藏标题栏*/
        actionBar.hide();
        bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
        registerReceiver(mBroadcastReceiver, makeIntentFilter());
        initview();
        Events_dealwith();
//        initTabhost();
    }

    public void initview() {
        alarm_tel1_btn = findViewById(R.id.alarmtel1_btn);
        alarm_tel2_btn = findViewById(R.id.alarmtel2_btn);
        alarm_tel3_btn = findViewById(R.id.alarmtel3_btn);
        alarm_tel4_btn = findViewById(R.id.alarmtel4_btn);
        alarm_tel5_btn = findViewById(R.id.alarmtel5_btn);
        QueryParam2_btn = findViewById(R.id.config_getParams2_btn);
        config2_param1_btn = findViewById(R.id.config2_param1_btn);
        config2_param3_btn = findViewById(R.id.config2_param3_btn);


        alarm_tel1_et = findViewById(R.id.alarmtel1);
        alarm_tel2_et = findViewById(R.id.alarmtel2);
        alarm_tel3_et = findViewById(R.id.alarmtel3);
        alarm_tel4_et = findViewById(R.id.alarmtel4);
        alarm_tel5_et = findViewById(R.id.alarmtel5);

    }


    public void Events_dealwith() {


        alarm_tel1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = alarm_tel1_et.getText().toString().trim();
                if (v0.length() != 11) {
                    Toast.makeText(Config2_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                } else {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getTel1(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        alarm_tel2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = alarm_tel2_et.getText().toString().trim();
                if (v0.length() != 11) {
                    Toast.makeText(Config2_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                } else {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getTel2(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        alarm_tel3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = alarm_tel3_et.getText().toString().trim();
                if (v0.length() != 11) {
                    Toast.makeText(Config2_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                } else {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getTel3(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        alarm_tel4_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = alarm_tel4_et.getText().toString().trim();
                if (v0.length() != 11) {
                    Toast.makeText(Config2_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                } else {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getTel4(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        alarm_tel5_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = alarm_tel5_et.getText().toString().trim();
                if (v0.length() != 11) {
                    Toast.makeText(Config2_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                } else {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getTel5(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        QueryParam2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getparam2();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        config2_param1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config2_Activity.this, Config_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        config2_param3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config2_Activity.this, Config3_Activity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public static byte[] str2Byte(String hexStr) {
        int b = hexStr.length() % 2;
        if (b != 0) {
            hexStr = "0" + hexStr;
        }
        String[] a = new String[hexStr.length() / 2];
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            a[i] = hexStr.substring(2 * i, 2 * i + 2);
        }
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(a[i], 16);
        }
        return bytes;
    }
    public void getparam2() {
        gattChar.setValue(str2Byte(Param_Mode.QureyParam2));
        gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bleService.mBluetoothGatt.writeCharacteristic(gattChar);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bleService.mBluetoothGatt.readCharacteristic(gattChar);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        unbindService(conn);
        unregisterReceiver(mBroadcastReceiver);
    }
}
