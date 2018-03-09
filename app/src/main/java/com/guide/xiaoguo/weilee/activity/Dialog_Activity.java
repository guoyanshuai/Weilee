package com.guide.xiaoguo.weilee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.fragment.Home_Fragment;
import com.guide.xiaoguo.weilee.service.BleService;

public class Dialog_Activity extends Activity {

    private RadioGroup dialog_RG;
    private Button home_submit_btn;
    private Button home_cancel_btn;
    int status = 0;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static final String CANCEL_DEVICE_ALARM = "find.device.cancel.alarm";
    public static final String FIND_DEVICE_ALARM_ON = "find.device.alarm.on";

    public static String bleAddress;
    BleService bleService;
    ProgressDialog mpd = null;
    private final ServiceConnection conn = new ServiceConnection() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            bleService = ((BleService.LocalBinder) service).getService();
            if (!bleService.init()) {
                finish();
            }
            bleService.connect(Home_Fragment.Device_Address);
            mpd = ProgressDialog.show(Dialog_Activity.this, null, "正在连接设备...");
            Log.i("DeviceConnect", "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            bleService.disconnect();
            bleService = null;
        }
    };

    BroadcastReceiver mbtBroadcastReceiver = new BroadcastReceiver() {

        @SuppressLint({"NewApi", "DefaultLocale"})
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Dialog_Activity.this, "设备连接成功！",
                                Toast.LENGTH_LONG).show();
                    }
                });
                if (mpd != null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                    mpd.dismiss();
                }
            }
            if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(Dialog_Activity.this, "设备断开！", Toast.LENGTH_LONG)
                        .show();
                Log.i("DeviceConnect", "DeviceConnect---onReceive: ");
                bleService.disconnect();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        initView();
        Event_Dealwith();
        bindBleSevice();
        registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter());
    }

    public void initView() {
        dialog_RG = findViewById(R.id.home_rg_btn);
        dialog_RG.check(R.id.config_home);
        status = 1;
        home_submit_btn = findViewById(R.id.home_submit_btn);
        home_cancel_btn = findViewById(R.id.home_cancel_btn);

    }

    private void bindBleSevice() {
        Intent serviceIntent = new Intent(this, BleService.class);
        bindService(serviceIntent, conn, BIND_AUTO_CREATE);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
        intentFilter.addAction(BleService.ACTION_GATT_RSSI);
        return intentFilter;
    }


    public void Event_Dealwith() {
        dialog_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int rg_ID = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(rg_ID);
                Log.i("setOnCheckedChange", "onCheckedChanged: " + rb.getText().toString());
                String checkStr = rb.getText().toString();
                if (checkStr.equals("参数配置")) {
                    status = 1;
                } else if (checkStr.equals("打印数据")) {
                    status = 2;
                } else if (checkStr.equals("指令发送")) {
                    status = 3;
                }

            }
        });
        home_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                switch (status) {
                    case 1:
                        Intent intent = new Intent(Dialog_Activity.this, Config_Activity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        Intent intent1 = new Intent(Dialog_Activity.this, BLE_Printer_Activity.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case 3:
                        Intent intent2 = new Intent(Dialog_Activity.this, Cmd_Activity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
        home_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        unbindService(conn);
        unregisterReceiver(mbtBroadcastReceiver);
    }
}
