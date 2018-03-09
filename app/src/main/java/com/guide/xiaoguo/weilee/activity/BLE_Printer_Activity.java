package com.guide.xiaoguo.weilee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.adapter.BluetoothOperation;
import com.guide.xiaoguo.weilee.mode.IPrinterOpertion;
import com.guide.xiaoguo.weilee.mode.Param_Mode;
import com.guide.xiaoguo.weilee.mode.RTorHis_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.service.BleService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BLE_Printer_Activity extends AppCompatActivity {

    public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;
    private UUID serUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private UUID charUuid = UUID.fromString("0000e1ff-0000-1000-8000-00805f9b34fb");
    BleService bleService;
    BluetoothGattCharacteristic gattChar;
    String config_result = "";
    private List<RTorHis_data_mode> his_list = new ArrayList<>();
    private RTorHis_data_mode his_mode;
    private static boolean isConnected = false;
    private String Device_ID = "";
    private Button getDevice_data;
    private Button bleprinter_connection;
    private Button bleprinter_printer;
    private ListView bleprinter_lv;

    private Context mContext;
    private MyAdapter myAdapter;
    private int i = 0;
    private UserInfo userInfo;
    String endTime;
    String startTime;
    String Max;
    String Min;
    ProgressDialog dialog = null;
    private IPrinterOpertion myOpertion;
    private PrinterInstance mPrinter = null;
    String printer = "20180308155305+20.2 20180308155805+19.9 20180308160305+19.8 20180308160805+19.9 20180308161305+19.9 20180308161805+20.3 20180308162305+20.4 20180308162805+20.7 20180308163305+20.8 20180308163805+20.4 20180308164305+20.3 20180308164805+20.5 20180308165305+20.2";

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
                final String stringValue = intent.getExtras().getString(
                        "StringValue");
                Log.i("---------", "onReceive: " + stringValue);
                final String hexValue = intent.getExtras()
                        .getString("HexValue");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated methdo stub
                        Log.i("333333000033", "run: " + stringValue);
                        if (stringValue != "" && hexValue != null && stringValue.length() > 3) {
                            if (stringValue.substring(0, 4).equals("0103")) {
                                Device_ID = stringValue.substring(0,2)+"-"+stringValue.substring(2,4)+"-"
                                        +stringValue.substring(4,6)+"-"+stringValue.substring(6,8)+"-"
                                        +stringValue.substring(8,10)+"-"+stringValue.substring(10,12);
                                getPrinter_Data();
                            }
                            if (stringValue.substring(0, 2).equals("20")) {
                                config_result = config_result + stringValue;
                                getPrinter2_Data();
                            } else if (stringValue.equals("null")) {
                                Toast.makeText(BLE_Printer_Activity.this, "获取数据完毕！！", Toast.LENGTH_SHORT).show();
                                PullData(config_result);
                                Log.i("........", "run: " + Device_ID);
                            }
                        }
                    }
                });
            }
            if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(BLE_Printer_Activity.this, "设备连接断开",
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


    public String PullDateTime(String date) {
        String result = "";
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String hour = date.substring(8, 10);
        String min = date.substring(10, 12);
        String sec = date.substring(12, 14);
        result = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
        return result;
    }

    public void PullData(String SumData) {
        String data[] = printer.split(" ");
        String time = "";
        String value1 = "";
        String value2 = "";
        for (int k = data.length-1; k>=0; k--) {
            his_mode = new RTorHis_data_mode();
            if(data[k].length()==19) {
                time = PullDateTime(data[k].substring(0, 14));
                value1 = data[k].substring(14, 19);
                value2 = "";
            }else if(data[k].length()>19){
                time = PullDateTime(data[k].substring(0, 14));
                value1 = data[k].substring(14, 19);
                value2 = data[k].substring(19,24);
            }
            his_mode.setParam1(value1);
            his_mode.setParam3(value2);
            his_mode.setUpDataTime(time);
            his_list.add(his_mode);
        }
        myAdapter.clearDeviceList();
        myAdapter.setDeviceList((ArrayList<RTorHis_data_mode>) his_list);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_printer);
        ActionBar actionBar = getSupportActionBar();/*隐藏标题栏*/
        actionBar.hide();
        mContext = this;
        bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
        registerReceiver(mBroadcastReceiver, makeIntentFilter());
        initView();
        Event_dealwith();
    }


    public void initView() {
        userInfo = (UserInfo) this.getApplication();
        getDevice_data = findViewById(R.id.getDevice_data);
        bleprinter_connection = findViewById(R.id.bleprinter_connection);
        bleprinter_printer = findViewById(R.id.bleprinter_printer);
        bleprinter_lv = findViewById(R.id.bleprinter_lv);

        myAdapter = new MyAdapter(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View haed_view = inflater.inflate(R.layout.list_header2, null);
        bleprinter_lv.addHeaderView(haed_view);
        bleprinter_lv.setAdapter(myAdapter);

        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Connecting...");
        dialog.setMessage("Please Wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

    }

    public void getDevice_SN() {
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

    public void getPrinter_Data() {
        gattChar.setValue(Param_Mode.ReadPrinter);
        gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bleService.mBluetoothGatt.writeCharacteristic(gattChar);
        Log.i("000000000000000000", "getPrinter_Data: "+Param_Mode.ReadPrinter);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bleService.mBluetoothGatt.readCharacteristic(gattChar);
    }

    public void getPrinter2_Data() {
        gattChar.setValue(Param_Mode.Read);
        gattChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bleService.mBluetoothGatt.writeCharacteristic(gattChar);
        Log.i("000000000000000000", "getPrinter2_Data: "+Param_Mode.Read);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bleService.mBluetoothGatt.readCharacteristic(gattChar);
    }

    public void Event_dealwith() {
        getDevice_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDevice_SN();
                PullData(printer);
            }
        });
        bleprinter_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        bleprinter_printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        bleprinter_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bleService.disconnect();
                openConn();
                if (!isConnected) {
                    bleprinter_connection.setText("连接打印机");
                }
            }
        });
        bleprinter_printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((isConnected) && (mPrinter != null)) {
                    Log.i("..........", "onClick: " + isConnected + "00000");
                    if (his_list.size() > 0) {
                        getStart_EndDatetime();
                        MAX_MIN_Data();
                        print_date(mPrinter);
                    } else {
                        Toast.makeText(mContext, "无数据!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "请先连接打印机!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    public void print_date(PrinterInstance mPrinter) {
        mPrinter.init();
        // mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT,1);
        mPrinter.printText("********************************\n");
        mPrinter.printText("公司名称：" + userInfo.getCompanyName() + "\n");
        mPrinter.printText("序列号：" + Device_ID + "\n");
        mPrinter.printText("记录开始时间：" + startTime.substring(2) + "\n");
        mPrinter.printText("记录结束时间：" + endTime.substring(2) + "\n");
        mPrinter.printText("总计记录条数：" + his_list.size() + "条" + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("********************************\n");
        mPrinter.printText("温度最大值：" + Max + ",最小值：" + Min + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("********************************\n");
        for (i = his_list.size() - 1; i >= 0; i--) {
            if (his_list.get(i).getParam3().equals("")) {
                if (Float.valueOf(his_list.get(i).getParam1()) > 0) {
                    mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  "  + his_list.get(i).getParam1() + "\n");
                } else {
                    mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + " " + his_list.get(i).getParam1() + "\n");
                }
            } else {
                if (Float.valueOf(his_list.get(i).getParam1()) > 0) {
                    if (Float.valueOf(his_list.get(i).getParam3()) > 0) {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  "  + his_list.get(i).getParam1() + "  "  + his_list.get(i).getParam3() + "\n");
                    } else {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  "  + his_list.get(i).getParam1() + "  "  + his_list.get(i).getParam3() + "\n");
                    }
                } else {
                    if (Float.valueOf(his_list.get(i).getParam3()) > 0) {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + his_list.get(i).getParam1() + "  "  + his_list.get(i).getParam3() + "\n");
                    } else {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  "  + his_list.get(i).getParam1() + "  "  + his_list.get(i).getParam3() + "\n");
                    }
                }
            }
        }
        mPrinter.printText("********************************\n");

        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("时     间：____________________\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("签收人签字：____________________\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 5);
    }

    public void getStart_EndDatetime() {
        List<RTorHis_data_mode> list = new ArrayList<>();
        list = his_list;
        endTime = list.get(0).getUpDataTime();
        startTime = list.get(list.size() - 1).getUpDataTime();
    }

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<RTorHis_data_mode> devices = new ArrayList<>();

        public MyAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        public void setDeviceList(ArrayList<RTorHis_data_mode> list) {
            if (list != null) {
                devices = (ArrayList<RTorHis_data_mode>) list.clone();
                notifyDataSetChanged();
            }
        }

        public void clearDeviceList() {
            if (devices != null) {
                devices.clear();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_item2, null);
                holder.number = view.findViewById(R.id.number);
                holder.param1 = view.findViewById(R.id.param1);
                holder.param3 = view.findViewById(R.id.param3);
                holder.updatetime = view.findViewById(R.id.updatetime);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.number.setText(String.valueOf(position + 1));
            Log.i("SNSNSSNNSNSNSNSSN", "getView: " + devices.get(position).getDeviceName());
            holder.param1.setText(devices.get(position).getParam1());
            holder.param3.setText(devices.get(position).getParam3());
            holder.updatetime.setText(devices.get(position).getUpDataTime().substring(0, 19));
            if (position % 2 == 0) {
                view.setBackgroundColor(getResources().getColor(R.color.evenColor));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.unevenColor));
            }
            return view;
        }
    }

    static class ViewHolder {
        public TextView number;
        public TextView param1;
        public TextView param3;
        public TextView updatetime;
    }

    public void MAX_MIN_Data() {
        int l;
        float MAX = 0;
        float MIN = 100;
        List<RTorHis_data_mode> list = new ArrayList<>();
        list = his_list;
        RTorHis_data_mode mode = new RTorHis_data_mode();
        for (l = 0; l < list.size(); l++) {
            mode = list.get(l);

            if (Float.valueOf(mode.getParam1()) > MAX) {
                MAX = Float.valueOf(mode.getParam1());
            } else if (!(mode.getParam3().equals(""))) {
                if (Float.valueOf(mode.getParam3()) > MAX) {
                    MAX = Float.valueOf(mode.getParam3());
                }

            }
            if (Float.valueOf(mode.getParam1()) < MIN) {
                MIN = Float.valueOf(mode.getParam1());
            } else if (!(mode.getParam3().equals(""))) {
                if (Float.valueOf(mode.getParam3()) < MIN) {
                    MIN = Float.valueOf(mode.getParam3());
                }
            }
        }
        if (MAX > 0) {
            Max = "+" + MAX;
        } else {
            Max = "-" + MAX;
        }
        if (MIN > 0) {
            Min = "+" + MIN;
        } else {
            Min = " " + MIN;
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    mPrinter = myOpertion.getPrinter();
                    bleprinter_connection.setText("断开连接");
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(mContext, "connect failed...", Toast.LENGTH_SHORT).show();
                    bleprinter_connection.setText("连接打印机");
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
                    Toast.makeText(mContext, "connect close...", Toast.LENGTH_SHORT).show();
                    bleprinter_connection.setText("连接打印机");
                    break;
                default:
                    break;
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    };

    private void openConn() {
        if (!isConnected) {
            myOpertion = new BluetoothOperation(mContext, mHandler);
            myOpertion.chooseDevice();
        } else {
            myOpertion.close();
            myOpertion = null;
            mPrinter = null;
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    dialog.show();
                    new Thread(new Runnable() {
                        public void run() {
                            myOpertion.open(data);
                        }
                    }).start();
                }
                break;
            case ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    myOpertion.chooseDevice();
                } else {
                    bleprinter_connection.setText("连接设备");
                    Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        unbindService(conn);
        unregisterReceiver(mBroadcastReceiver);
    }
}
