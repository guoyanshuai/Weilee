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
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.Param_Mode;
import com.guide.xiaoguo.weilee.service.BleService;

import java.util.UUID;

public class Cmd_Activity extends AppCompatActivity {


    private UUID serUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private UUID charUuid = UUID.fromString("0000e1ff-0000-1000-8000-00805f9b34fb");
    BleService bleService;
    BluetoothGattCharacteristic gattChar;
    private Button cmd_sn_btn;
    private Button cmd_read_version;
    private Button cmd_erasehis_btn;
    private Button cmd_eraseprinter_btn;
    private EditText cmd_sn;
    private EditText cmd_hard_version;
    private EditText cmd_ware_version;

    private ServiceConnection conn = new ServiceConnection() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            // TODO Auto-generated method stub
            bleService = ((BleService.LocalBinder) service).getService();
            gattChar = bleService.mBluetoothGatt.getService(serUuid)
                    .getCharacteristic(charUuid);
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
                        if (stringValue != "" && hexValue != "" && stringValue.length() >= 2) {
                            if (stringValue.length() >= 4) {
                                if (stringValue.substring(0, 4).equals("0103") && stringValue.length() == 12) {
                                    cmd_sn.setText(stringValue.substring(0, 2) + "-" + stringValue.substring(2, 4) + "-"
                                            + stringValue.substring(4, 6) + "-" + stringValue.substring(6, 8) + "-"
                                            + stringValue.substring(8, 10) + "-" + stringValue.substring(10, 12));
                                } else if (stringValue.trim().substring(0, 3).equals("SWV")) {
                                    cmd_ware_version.setText("软件版本：" + stringValue.substring(4, 11));
                                    cmd_hard_version.setText("硬件版本：" + stringValue.substring(15, 22));
                                }
                            }
                            if (stringValue.trim().equals("OK")) {
                                Toast.makeText(Cmd_Activity.this, "操作成功！", Toast.LENGTH_SHORT).show();
                            } else if (stringValue.trim().equals("empty")) {
                                Toast.makeText(Cmd_Activity.this, "暂无数据可擦除！", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
            if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(Cmd_Activity.this, "设备连接断开",
                        Toast.LENGTH_SHORT).show();
                Log.i("DeviceConnect", "ChangeCharActivity----onReceive: ");
                bleService.disconnect();
            }
        }
    };

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
        setContentView(R.layout.activity_cmd);
        ActionBar actionBar = getSupportActionBar();/*隐藏标题栏*/
        actionBar.hide();
        initview();
        Events_dealwith();
        bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
        registerReceiver(mBroadcastReceiver, makeIntentFilter());
    }

    public void initview() {
        cmd_sn_btn = findViewById(R.id.cmd_sn_btn);
        cmd_read_version = findViewById(R.id.cmd_read_version);
        cmd_erasehis_btn = findViewById(R.id.cmd_erasehis_btn);
        cmd_eraseprinter_btn = findViewById(R.id.cmd_eraseprinter_btn);
        cmd_sn = findViewById(R.id.cmd_sn);
        cmd_hard_version = findViewById(R.id.cmd_hard_version);
        cmd_ware_version = findViewById(R.id.cmd_ware_version);
    }

    public void Events_dealwith() {
        cmd_sn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gattChar.setValue(Param_Mode.ReadSN);
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
        cmd_read_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gattChar.setValue(Param_Mode.ReadVersion);
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
        cmd_erasehis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gattChar.setValue(Param_Mode.EraseHistory);
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
        cmd_eraseprinter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gattChar.setValue(Param_Mode.ErasePrinter);
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
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        unbindService(conn);
        unregisterReceiver(mBroadcastReceiver);
    }
}
