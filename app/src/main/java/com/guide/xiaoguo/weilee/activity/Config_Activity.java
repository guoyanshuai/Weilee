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
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.Param_Mode;
import com.guide.xiaoguo.weilee.service.BleService;
import com.guide.xiaoguo.weilee.utils.Tools;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class Config_Activity extends AppCompatActivity {

    private UUID serUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private UUID charUuid = UUID.fromString("0000e1ff-0000-1000-8000-00805f9b34fb");
    BleService bleService;
    BluetoothGattCharacteristic gattChar;
    public static String Sn = "010318040001";
    String config_result = "";

    private Button delay_time_btn;
    private Button normal_interval_btn;
    private Button abnormal_interval_btn;
    private Button mess_interval_btn;
    private Button mess_switch_btn;
    private Button soud_switch_btn;
    private Button realtime_btn;
    private Button QueryParam1_btn;
    private Button config_param1_btn;
    private Button config_param2_btn;
    private Button config_param3_btn;


    private EditText delay_time_et;
    private EditText normal_interval_et;
    private EditText abnormal_interval_et;
    private EditText mess_interval_et;
    private RadioGroup mess_switch_rg;
    private RadioGroup soud_switch_rg;
    private EditText real_year_et;
    private EditText real_month_et;
    private EditText real_day_et;
    private EditText real_hour_et;
    private EditText real_min_et;
    private EditText real_sec_et;


    int mess_swicth = 1;
    int soud_swicth = 1;
    String v0 = "";
    String v1 = "";
    String v2 = "";
    String v3 = "";
    String v4 = "";
    String v5 = "";
    String v6 = "";

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
                        Log.i("33333333", "run: " + stringValue);

                        if (hexValue != null && hexValue.length() > 4) {
                            if (hexValue.substring(0, 2).equals("68") &&
                                    hexValue.substring(hexValue.length() - 4, hexValue.length() - 2).equals(Tools.makeChecksum(hexValue.substring(0, hexValue.length() - 4)).toUpperCase())) {
                                config_result = hexValue;
                                Log.i("3333333", "run: " + config_result);
                                if (config_result.length() > 58) {
                                    //680103160800016880036000000000D616
                                    if (config_result.substring(24, 26).equals("01")) {
                                        PullParam1(config_result);
                                    }
                                } else if (config_result.trim().equals("680103160800016880036000000000D616")) {
                                    Toast.makeText(Config_Activity.this, "配置成功！", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Config_Activity.this, "设备连接断开",
                        Toast.LENGTH_SHORT).show();
                Log.i("DeviceConnect", "ChangeCharActivity----onReceive: ");
                bleService.disconnect();
            }
        }
    };

    public void PullParam1(String param1) {
        int daley_time;
        int interval_time;
        int interval_time_abnomarl;
        int mess_switch;
        int soud_switch;
        int mess_interval;
        int year;
        int month;
        int day;
        int hour;
        int min;
        int sec;
        daley_time = Integer.parseInt(param1.substring(26, 30));
        interval_time = Integer.parseInt(param1.substring(30, 34));
        interval_time_abnomarl = Integer.parseInt(param1.substring(34, 38));
        mess_interval = Integer.parseInt(param1.substring(38, 42));
        soud_switch = Integer.parseInt(param1.substring(42, 44));
        mess_switch = Integer.parseInt(param1.substring(44, 46));
        year = Integer.parseInt(param1.substring(56, 58));
        month = Integer.parseInt(param1.substring(54, 56));
        day = Integer.parseInt(param1.substring(52, 54));
        hour = Integer.parseInt(param1.substring(50, 52));
        min = Integer.parseInt(param1.substring(48, 50));
        sec = Integer.parseInt(param1.substring(46, 48));

        delay_time_et.setText(daley_time + "");
        normal_interval_et.setText(interval_time + "");
        abnormal_interval_et.setText(interval_time_abnomarl + "");
        mess_interval_et.setText(mess_interval + "");
        if (soud_switch == 1) {
            soud_switch_rg.check(R.id.config_on_soud);
        } else {
            soud_switch_rg.check(R.id.config_off_soud);
        }
        if (mess_switch == 1) {
            mess_switch_rg.check(R.id.config_on_mess);
        } else {
            mess_switch_rg.check(R.id.config_off_mess);
        }
        real_year_et.setText(year + "");
        real_month_et.setText(month + "");
        real_day_et.setText(day + "");
        real_hour_et.setText(hour + "");
        real_min_et.setText(min + "");
        real_sec_et.setText(sec + "");
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
        setContentView(R.layout.config_param1);
        ActionBar actionBar = getSupportActionBar();/*隐藏标题栏*/
        actionBar.hide();
        bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
        registerReceiver(mBroadcastReceiver, makeIntentFilter());
        initview();
        Events_dealwith();
    }

    public void initview() {
        delay_time_btn = findViewById(R.id.delay_time_btn);
        normal_interval_btn = findViewById(R.id.normal_interval_btn);
        abnormal_interval_btn = findViewById(R.id.abnormal_interval_btn);
        mess_interval_btn = findViewById(R.id.mess_interval_btn);
        mess_switch_btn = findViewById(R.id.mess_switch_btn);
        soud_switch_btn = findViewById(R.id.sound_switch_btn);
        realtime_btn = findViewById(R.id.setDevice_time_btn);
        QueryParam1_btn = findViewById(R.id.config_getParams1_btn);
        config_param1_btn = findViewById(R.id.config_param1_btn);
        config_param2_btn = findViewById(R.id.config_param2_btn);
        config_param3_btn = findViewById(R.id.config_param3_btn);

        delay_time_et = findViewById(R.id.delay_time);
        normal_interval_et = findViewById(R.id.normal_time);
        abnormal_interval_et = findViewById(R.id.abnormal_time);
        mess_interval_et = findViewById(R.id.alarminterval_time);
        mess_switch_rg = findViewById(R.id.config_mess_switch);
        mess_switch_rg.check(R.id.config_on_mess);
        soud_switch_rg = findViewById(R.id.config_soud_switch);
        soud_switch_rg.check(R.id.config_on_soud);
        real_year_et = findViewById(R.id.setDevice_time_year);
        real_month_et = findViewById(R.id.setDevice_time_month);
        real_day_et = findViewById(R.id.setDevice_time_day);
        real_hour_et = findViewById(R.id.setDevice_time_hour);
        real_min_et = findViewById(R.id.setDevice_time_min);
        real_sec_et = findViewById(R.id.setDevice_time_sec);
    }


    public void Events_dealwith() {
        mess_switch_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int rg_ID = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(rg_ID);
                Log.i("setOnCheckedChange", "onCheckedChanged: " + rb.getText().toString());
                String checkStr = rb.getText().toString();
                if (checkStr.equals("关")) {
                    mess_swicth = 0;
                } else {
                    mess_swicth = 1;
                }
            }
        });
        soud_switch_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int rg_ID = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(rg_ID);
                Log.i("setOnCheckedChange", "onCheckedChanged: " + rb.getText().toString());
                String checkStr = rb.getText().toString();
                if (checkStr.equals("关")) {
                    soud_swicth = 0;
                } else {
                    soud_swicth = 1;
                }
            }
        });

        delay_time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = delay_time_et.getText().toString().trim();
                while (v0.length() < 4) {
                    v0 = "0" + v0;
                }
                if (v0.length() == 4) {
                    Log.i("000000000", "onClick: " + v0 + "     " + Sn);
                    gattChar.setValue(str2Byte(Param_Mode.getDelayInterval(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bleService.mBluetoothGatt.readCharacteristic(gattChar);
                }
            }
        });
        normal_interval_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = normal_interval_et.getText().toString().trim();
                while (v0.length() < 4) {
                    v0 = "0" + v0;
                }
                if (v0.length() == 4) {
                    Log.i("normal", "onClick: " + v0 + "   " + Param_Mode.getCaptureTime(Sn, v0));
                    gattChar.setValue(str2Byte(Param_Mode.getCaptureTime(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bleService.mBluetoothGatt.readCharacteristic(gattChar);
                }
            }
        });
        abnormal_interval_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = abnormal_interval_et.getText().toString().trim();
                while (v0.length() < 4) {
                    v0 = "0" + v0;
                }
                if (v0.length() == 4) {
                    gattChar.setValue(str2Byte(Param_Mode.getExcCaptureTime(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                    Log.i("abnormal", "onClick: " + v0 + "   " + Param_Mode.getExcCaptureTime(Sn, v0));


                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bleService.mBluetoothGatt.readCharacteristic(gattChar);
                }
            }
        });
        mess_interval_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = mess_interval_et.getText().toString().trim();
                while (v0.length() < 4) {
                    v0 = "0" + v0;
                }
                if (v0.length() == 4) {
                    gattChar.setValue(str2Byte(Param_Mode.getMessageInterval(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bleService.mBluetoothGatt.readCharacteristic(gattChar);
                }
            }
        });
        mess_switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = String.valueOf(mess_swicth);
                if (v0.length() == 1) {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getMessageSwitch(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bleService.mBluetoothGatt.readCharacteristic(gattChar);
                }
            }
        });
        soud_switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = String.valueOf(soud_swicth);
                if (v0.length() == 1) {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getSoundSwitch(Sn, v0)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bleService.mBluetoothGatt.readCharacteristic(gattChar);
                }
            }
        });
        realtime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = real_year_et.getText().toString().trim();
                v1 = real_month_et.getText().toString().trim();
                v2 = real_day_et.getText().toString().trim();
                v3 = real_hour_et.getText().toString().trim();
                v4 = real_min_et.getText().toString().trim();
                v5 = real_sec_et.getText().toString().trim();
                if (v0.length() < 2) {
                    v0 = "0" + v0;
                } else if (v0.length() > 2) {
                    Toast.makeText(Config_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                }
                if (v1.length() < 2) {
                    v1 = "0" + v1;
                } else if (v1.length() > 2) {
                    Toast.makeText(Config_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                }
                if (v2.length() < 2) {
                    v2 = "0" + v2;
                } else if (v2.length() > 2) {
                    Toast.makeText(Config_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                }
                if (v3.length() < 2) {
                    v3 = "0" + v3;
                } else if (v3.length() > 2) {
                    Toast.makeText(Config_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                }
                if (v4.length() < 2) {
                    v4 = "0" + v4;
                } else if (v4.length() > 2) {
                    Toast.makeText(Config_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                }
                if (v5.length() < 2) {
                    v5 = "0" + v5;
                } else if (v5.length() > 2) {
                    Toast.makeText(Config_Activity.this, "请检查输入的数据是否正确", Toast.LENGTH_LONG);
                }
                if (v0.length() == 2 && v1.length() == 2 && v2.length() == 2 && v3.length() == 2 && v4.length() == 2 && v5.length() == 2) {
                    v6 = v5 + v4 + v3 + v2 + v1 + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getDeviceTime(Sn, v6)));
                    gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bleService.mBluetoothGatt.readCharacteristic(gattChar);
                }
            }
        });
        QueryParam1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getparam1();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        config_param2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config_Activity.this, Config2_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        config_param3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config_Activity.this, Config3_Activity.class);
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


    public void getparam1() {
        gattChar.setValue(str2Byte(Param_Mode.QureyParam1));
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
