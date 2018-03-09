package com.guide.xiaoguo.weilee.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.guide.xiaoguo.weilee.mode.RTorHis_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class Printer_Activity extends AppCompatActivity {

    public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;

    private UserInfo userInfo;
    private List<RTorHis_data_mode> his_list = new ArrayList<>();
    private MyAdapter myAdapter;
    private static boolean isConnected = false;

    ProgressDialog dialog = null;


    private IPrinterOpertion myOpertion;
    private PrinterInstance mPrinter = null;

    private ListView his_lv;
    private Button his_connection;
    private Button his_printer;
    private Context mContext;
    private String Device_ID = "";
    String endTime;
    String startTime;
    String Max;
    String Min;
    private int i = 0;
    private int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mContext = this;
        initview();
        Event_DealWith();
    }

    public void initview() {

        userInfo = (UserInfo) this.getApplication();
        his_lv = findViewById(R.id.his_lv);
        his_list = (List<RTorHis_data_mode>) userInfo.his_printer_list.clone();
        myAdapter = new MyAdapter(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View haed_view = inflater.inflate(R.layout.list_header, null);
        his_lv.addHeaderView(haed_view);
        his_lv.setAdapter(myAdapter);

        his_connection = findViewById(R.id.his_connection);
        his_printer = findViewById(R.id.his_printer);
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Connecting...");
        dialog.setMessage("Please Wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        myAdapter.clearDeviceList();
        myAdapter.setDeviceList((ArrayList<RTorHis_data_mode>) his_list);
    }

    public void Event_DealWith() {
        his_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConn();
                if (!isConnected) {
                    his_connection.setText("连接打印机");
                }
            }
        });
        his_printer.setOnClickListener(new View.OnClickListener() {
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
        mPrinter.printText("总计记录调数：" + his_list.size() + "条" + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("********************************\n");
        mPrinter.printText("温度最大值：" + Max + ",最小值：" + Min + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("********************************\n");
        for (i = his_list.size() - 1; i >= 0; i--) {
            if (his_list.get(i).getParam3().equals("")) {
                if (Float.valueOf(his_list.get(i).getParam1()) > 0) {
                    mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "+" + his_list.get(i).getParam1() + "\n");
                } else {
                    mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + " " + his_list.get(i).getParam1() + "\n");
                }
            } else {
                if (Float.valueOf(his_list.get(i).getParam1()) > 0) {
                    if (Float.valueOf(his_list.get(i).getParam3()) > 0) {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "+" + his_list.get(i).getParam1() + "  " + "+" + his_list.get(i).getParam3() + "\n");
                    } else {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "+" + his_list.get(i).getParam1() + "  " + " " + his_list.get(i).getParam3() + "\n");
                    }
                } else {
                    if (Float.valueOf(his_list.get(i).getParam3()) > 0) {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + " " + his_list.get(i).getParam1() + "  " + "+" + his_list.get(i).getParam3() + "\n");
                    } else {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + " " + his_list.get(i).getParam1() + "  " + " " + his_list.get(i).getParam3() + "\n");
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
            Min = " "+ MIN;
        }
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
                view = inflater.inflate(R.layout.list_item, null);
                holder.number = view.findViewById(R.id.number);
                holder.status = view.findViewById(R.id.status);
                holder.param1 = view.findViewById(R.id.param1);
                holder.param2 = view.findViewById(R.id.param2);
                holder.param3 = view.findViewById(R.id.param3);
                holder.param4 = view.findViewById(R.id.param4);
                holder.updatetime = view.findViewById(R.id.updatetime);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.number.setText(String.valueOf(position + 1));
            Log.i("SNSNSSNNSNSNSNSSN", "getView: " + devices.get(position).getDeviceName());
            holder.status.setText(devices.get(position).getStatus());
            holder.param1.setText(devices.get(position).getParam1());
            holder.param2.setText(devices.get(position).getParam2());
            holder.param3.setText(devices.get(position).getParam3());
            holder.param4.setText(devices.get(position).getParam4());
            holder.updatetime.setText(devices.get(position).getUpDataTime().substring(0, 19));
            if (position % 2 == 0) {
                view.setBackgroundColor(getResources().getColor(R.color.evenColor));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.unevenColor));
            }

            if (Integer.valueOf(devices.get(position).getStatus()) == 2) {
                holder.param1.setTextColor(Color.RED);
                holder.param2.setTextColor(Color.RED);
                holder.param3.setTextColor(Color.RED);
                holder.param4.setTextColor(Color.RED);
            } else {
                holder.param1.setTextColor(Color.BLACK);
                holder.param2.setTextColor(Color.BLACK);
                holder.param3.setTextColor(Color.BLACK);
                holder.param4.setTextColor(Color.BLACK);
            }
            return view;
        }
    }

    static class ViewHolder {
        public TextView number;
        public TextView status;
        public TextView param1;
        public TextView param2;
        public TextView param3;
        public TextView param4;
        public TextView updatetime;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    isConnected = true;
                    mPrinter = myOpertion.getPrinter();
                    his_connection.setText("断开连接");
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(mContext, "connect failed...", Toast.LENGTH_SHORT).show();
                    his_connection.setText("连接打印机");
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
                    Toast.makeText(mContext, "connect close...", Toast.LENGTH_SHORT).show();
                    his_connection.setText("连接打印机");
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
            myOpertion = new BluetoothOperation(Printer_Activity.this, mHandler);
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
                    his_connection.setText("连接设备");
                    Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myOpertion != null) {
            myOpertion.close();
            myOpertion = null;
            mPrinter = null;
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isConnected) {
            myOpertion.close();
            myOpertion = null;
            mPrinter = null;
        }
    }
}
