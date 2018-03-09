package com.guide.xiaoguo.weilee.activity;

import android.annotation.SuppressLint;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.Param_Mode;
import com.guide.xiaoguo.weilee.service.BleService;
import com.guide.xiaoguo.weilee.utils.Tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

public class Config3_Activity extends AppCompatActivity {

    private UUID serUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private UUID charUuid = UUID.fromString("0000e1ff-0000-1000-8000-00805f9b34fb");
    BleService bleService;
    BluetoothGattCharacteristic gattChar;
    String Sn = "010318040001";
    String config_result = "";

    private Button temp_adjust_btn;
    private Button hump_adjust_btn;
    private Button temp_uplimit_btn;
    private Button temp_downlimit_btn;
    private Button hump_uplimit_btn;
    private Button hump_downlimit_btn;
    private Button battery_btn;
    private Button IpPort_btn;
    private Button device_mode_btn;
    private Button device_name_btn;
    private Button company_name_btn;
    private Button QueryParam3_btn;
    private Button config3_param1_btn;
    private Button config3_param2_btn;
    private Button config3_param3_btn;

    private EditText temp_adjust_et;
    private EditText hump_adjust_et;
    private EditText temp_uplimit_et;
    private EditText temp_downlimit_et;
    private EditText hump_uplimit_et;
    private EditText hump_downlimit_et;
    private EditText battery_et;
    private EditText ip1_et;
    private EditText ip2_et;
    private EditText ip3_et;
    private EditText ip4_et;
    private EditText port_et;
    private EditText device_name_et;
    private EditText company_name_et;
    private RadioGroup device_mode_rg;
    private Button query_company_btn;

    int mode_select = 1;
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
                                    if (config_result.substring(24, 26).equals("03")) {
                                        PullParam3(config_result);
                                    }
                                } else if (config_result.trim().equals("680103160800016880036000000000D616")) {
                                    Toast.makeText(Config3_Activity.this, "配置成功！", Toast.LENGTH_SHORT).show();
                                } else if (config_result.substring(22, 24).equals("12")) {
                                    String Cname = PullCompanyName(config_result);
                                    company_name_et.setText(Cname);
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
                Toast.makeText(Config3_Activity.this, "设备连接断开",
                        Toast.LENGTH_SHORT).show();
                Log.i("DeviceConnect", "ChangeCharActivity----onReceive: ");
                bleService.disconnect();
            }
        }
    };

    /*参数1  68010316080001688A136013010001000200010010000005021707031800005816  66*/
    /*参数2  68010316080001688A2160130201565359691201478203510801478203510801478203510801478203510800002916   94*/
    /*参数2  68010316080001688A1860130300000000E803FA00EE02900176B2100A6419AC0D020000EB16  84*/
    /*参数2  68010316080001688A0C60120008B9F3B5C2D0C5CFA200002C16 */

    public void PullParam3(String param3) {
        String temp_ad;
        String hump_ad;
        String temp_up;
        String temp_down;
        String hump_up;
        String hump_down;
        String ip1;
        String ip2;
        String ip3;
        String ip4;
        String port;
        String device_mode_sensor;
        String battery_threshold;
        temp_ad = String.valueOf(HexToDec(param3.substring(26, 30)));
        hump_ad = String.valueOf(HexToDec(param3.substring(30, 34)));
        temp_up = String.valueOf(HexToDec(param3.substring(34, 38)));
        temp_down = String.valueOf(HexToDec(param3.substring(38, 42)));
        hump_up = String.valueOf(HexToDec(param3.substring(42, 46)));
        hump_down = String.valueOf(HexToDec(param3.substring(46, 50)));
        ip1 = String.valueOf(Integer.parseInt(param3.substring(50, 52), 16));
        ip2 = String.valueOf(Integer.parseInt(param3.substring(52, 54), 16));
        ip3 = String.valueOf(Integer.parseInt(param3.substring(54, 56), 16));
        ip4 = String.valueOf(Integer.parseInt(param3.substring(56, 58), 16));
        port = String.valueOf(Integer.parseInt(param3.substring(58, 62), 16));
        battery_threshold = String.valueOf(HexToDec(param3.substring(62, 66)));
        device_mode_sensor = String.valueOf(Integer.parseInt(param3.substring(66, 68), 16));
        temp_adjust_et.setText(temp_ad);
        hump_adjust_et.setText(hump_ad);
        temp_uplimit_et.setText(temp_up);
        temp_downlimit_et.setText(temp_down);
        hump_uplimit_et.setText(hump_up);
        hump_downlimit_et.setText(hump_down);
        ip1_et.setText(ip1);
        ip2_et.setText(ip2);
        ip3_et.setText(ip3);
        ip4_et.setText(ip4);
        port_et.setText(port);
        battery_et.setText(battery_threshold);
        if (Integer.parseInt(device_mode_sensor) == 1) {
            device_mode_rg.check(R.id.device_mode_one);
        } else if (Integer.parseInt(device_mode_sensor) == 2) {
            device_mode_rg.check(R.id.device_mode_two);
        } else {
            device_mode_rg.check(R.id.device_mode_three);
        }
    }

    public static String PullCompanyName(String hex) {
        String name = "";
        String name_pull = "";
        int leng = Integer.parseInt(hex.substring(26, 28));
        System.out.println(leng);
        String name_hex = hex.substring(28, 28 + leng*2);
        System.out.println(name_hex);
        for (int p = 0; p < name_hex.length() / 2; p++) {
            name_pull += "%" + name_hex.substring(0 + p * 2, 2 + p * 2);
            System.out.println(name_pull);
        }
        try {
            name = URLDecoder.decode(name_pull, "gbk");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            name = "";
        }
        return name;
    }

    public float HexToDec(String Hex) {
        String before = Hex.substring(0, 2);
        String after = Hex.substring(2, 4);
        String value = after + before;
        short x = (short) Integer.parseInt(value, 16);
        float y = (float) x / 10;
        return y;
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
        setContentView(R.layout.config_param3);
        ActionBar actionBar = getSupportActionBar();/*隐藏标题栏*/
        actionBar.hide();
        bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
        registerReceiver(mBroadcastReceiver, makeIntentFilter());
        initview();
        Events_dealwith();
    }

    public void initview() {

        temp_adjust_btn = findViewById(R.id.temp_adjust_btn);
        hump_adjust_btn = findViewById(R.id.hump_adjust_btn);
        temp_uplimit_btn = findViewById(R.id.temp_uplimit_btn);
        temp_downlimit_btn = findViewById(R.id.temp_downlimit_btn);
        hump_uplimit_btn = findViewById(R.id.hump_uplimit_btn);
        hump_downlimit_btn = findViewById(R.id.hump_downlimit_btn);
        battery_btn = findViewById(R.id.battery_btn);
        IpPort_btn = findViewById(R.id.set_IpPort_btn);
        device_mode_btn = findViewById(R.id.set_Device_Mode);
        device_name_btn = findViewById(R.id.set_BLEName_btn);
        company_name_btn = findViewById(R.id.set_companyName_btn);
        QueryParam3_btn = findViewById(R.id.config_getParams3_btn);
        config3_param1_btn = findViewById(R.id.config3_param1_btn);
        config3_param2_btn = findViewById(R.id.config3_param2_btn);
        config3_param3_btn = findViewById(R.id.config3_param3_btn);
        query_company_btn = findViewById(R.id.query_company_btn);

        temp_adjust_et = findViewById(R.id.temp_adjust);
        hump_adjust_et = findViewById(R.id.hump_adjust);
        temp_uplimit_et = findViewById(R.id.temp_uplimit);
        temp_downlimit_et = findViewById(R.id.temp_downlimit);
        hump_uplimit_et = findViewById(R.id.hump_uplimit);
        hump_downlimit_et = findViewById(R.id.hump_downlimit);
        battery_et = findViewById(R.id.battery);
        ip1_et = findViewById(R.id.set_Ip1);
        ip2_et = findViewById(R.id.set_Ip2);
        ip3_et = findViewById(R.id.set_Ip3);
        ip4_et = findViewById(R.id.set_Ip4);
        port_et = findViewById(R.id.set_port);
        device_name_et = findViewById(R.id.set_BLEName);
        company_name_et = findViewById(R.id.set_companyName);
        device_mode_rg = findViewById(R.id.Device_Mode_RG);
        device_mode_rg.check(R.id.device_mode_one);
    }


    public void Events_dealwith() {

        temp_adjust_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                v0 = temp_adjust_et.getText().toString().trim();
                float temp_adjust_f = Float.parseFloat(v0);
                short temp_adjust = (short) (temp_adjust_f * 10);
                if (temp_adjust >= 0) {
                    v1 = Tools.decTo2ByteHex(temp_adjust);
                    while (v1.length() < 4) {
                        v1 = v1 + "0";
                    }
                } else {
                    v2 = String.format("%X", temp_adjust);
                    v3 = v2.substring(v2.length() - 2);
                    v4 = v2.substring(v2.length() - 4, v2.length() - 2);
                    v1 = v3 + v4;
                }
                Log.i("....", "onClick: " + v1);
                gattChar.setValue(str2Byte(Param_Mode.getTempCalibration(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        hump_adjust_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                v0 = hump_adjust_et.getText().toString().trim();
                float hum_adjust_f = Float.parseFloat(v0);
                short hum_adjust = (short) (hum_adjust_f * 10);
                if (hum_adjust >= 0) {
                    v1 = Tools.decTo2ByteHex(hum_adjust);
                    while (v1.length() < 4) {
                        v1 = v1 + "0";
                    }
                } else {
                    v2 = String.format("%X", hum_adjust);
                    v3 = v2.substring(v2.length() - 2);
                    v4 = v2.substring(v2.length() - 4, v2.length() - 2);
                    v1 = v3 + v4;
                }
                gattChar.setValue(str2Byte(Param_Mode.getHumCalibration(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        temp_uplimit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                v0 = temp_uplimit_et.getText().toString().trim();
                float temp_uplimit_f = Float.parseFloat(v0);
                short temp_uplimit = (short) (temp_uplimit_f * 10);
                if (temp_uplimit >= 0) {
                    v1 = Tools.decTo2ByteHex(temp_uplimit);
                    while (v1.length() < 4) {
                        v1 = v1 + "0";
                    }
                } else {
                    v2 = String.format("%X", temp_uplimit);
                    v3 = v2.substring(v2.length() - 2);
                    v4 = v2.substring(v2.length() - 4, v2.length() - 2);
                    v1 = v3 + v4;
                }
                gattChar.setValue(str2Byte(Param_Mode.getTempUpLimit(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        temp_downlimit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = temp_downlimit_et.getText().toString().trim();
                float temp_downlimit_f = Float.parseFloat(v0);
                short temp_downlimit = (short) (temp_downlimit_f * 10);
                if (temp_downlimit >= 0) {
                    v1 = Tools.decTo2ByteHex(temp_downlimit);
                    while (v1.length() < 4) {
                        v1 = v1 + "0";
                    }
                } else {
                    v2 = String.format("%X", temp_downlimit);
                    v3 = v2.substring(v2.length() - 2);
                    v4 = v2.substring(v2.length() - 4, v2.length() - 2);
                    v1 = v3 + v4;
                }
                gattChar.setValue(str2Byte(Param_Mode.getTempDownLimit(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        hump_uplimit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = hump_uplimit_et.getText().toString().trim();
                float hum_uplimit_f = Float.parseFloat(v0);
                int hum_uplimit = (int) (hum_uplimit_f * 10);
                v1 = Tools.decTo2ByteHex(hum_uplimit);
                while (v1.length() < 4) {
                    v1 = v1 + "0";
                }
                gattChar.setValue(str2Byte(Param_Mode.getHumUpLimit(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        hump_downlimit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = hump_downlimit_et.getText().toString().trim();
                float hum_downlimit_f = Float.parseFloat(v0);
                int hum_downlimit = (int) (hum_downlimit_f * 10);
                v1 = Tools.decTo2ByteHex(hum_downlimit);
                while (v1.length() < 4) {
                    v1 = v1 + "0";
                }
                gattChar.setValue(str2Byte(Param_Mode.getHumDownLimit(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        battery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = battery_et.getText().toString().trim();
                float battery_f = Float.parseFloat(v0);
                short battery = (short) (battery_f * 10);
                if (battery >= 0) {
                    v1 = Tools.decTo2ByteHex(battery);
                    while (v1.length() < 4) {
                        v1 = v1 + "0";
                    }
                } else {
                    Toast.makeText(Config3_Activity.this, "请输入正确的参数!!", Toast.LENGTH_SHORT).show();
                }
                gattChar.setValue(str2Byte(Param_Mode.getBattery(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        IpPort_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = ip1_et.getText().toString().trim();
                int ip1 = Integer.parseInt(v0);
                String str_ip1 = Tools.DecTo1ByteHexString(ip1);
                v1 = ip2_et.getText().toString().trim();
                int ip2 = Integer.parseInt(v1);
                String str_ip2 = Tools.DecTo1ByteHexString(ip2);
                v2 = ip3_et.getText().toString().trim();
                int ip3 = Integer.parseInt(v2);
                String str_ip3 = Tools.DecTo1ByteHexString(ip3);
                v3 = ip4_et.getText().toString().trim();
                int ip4 = Integer.parseInt(v3);
                String str_ip4 = Tools.DecTo1ByteHexString(ip4);
                v4 = port_et.getText().toString().trim();
                int port1 = Integer.parseInt(v4);
                String str_port = Tools.decTo2ByteHex(port1);
                while (str_port.length() < 4) {
                    str_port = str_port + "0";
                }
                v5 = str_ip1 + str_ip2 + str_ip3 + str_ip4 + str_port;
                gattChar.setValue(str2Byte(Param_Mode.getIpAndPort(Sn, v5)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        device_mode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = String.valueOf(mode_select);
                if (v0.length() == 1) {
                    v0 = "0" + v0;
                    gattChar.setValue(str2Byte(Param_Mode.getDeviceMode(Sn, v0)));
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
        device_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                v0 = device_name_et.getText().toString().trim();
                try {
                    v2 = Tools.encode(v0, "UTF-8");
                    v1 = v2.replace("%", "");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                gattChar.setValue(str2Byte(Param_Mode.getBLEName(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        company_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v0 = company_name_et.getText().toString().trim();
                try {
                    v1 = Tools.encode(v0, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                gattChar.setValue(str2Byte(Param_Mode.getCompanyName(Sn, v1)));
                gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                bleService.mBluetoothGatt.writeCharacteristic(gattChar);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });

        QueryParam3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getparam3();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        query_company_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCompanyName();
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bleService.mBluetoothGatt.readCharacteristic(gattChar);
            }
        });
        config3_param1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config3_Activity.this, Config_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        config3_param2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Config3_Activity.this, Config2_Activity.class);
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


    public void getparam3() {
        gattChar.setValue(str2Byte(Param_Mode.QureyParam3));
        gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bleService.mBluetoothGatt.writeCharacteristic(gattChar);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bleService.mBluetoothGatt.readCharacteristic(gattChar);
    }

    public void getCompanyName() {
        gattChar.setValue(str2Byte(Param_Mode.QureyCompany));
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
