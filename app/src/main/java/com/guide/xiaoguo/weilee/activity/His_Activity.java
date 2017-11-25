package com.guide.xiaoguo.weilee.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.android.print.sdk.Table;
import com.baidu.mapapi.SDKInitializer;
import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.adapter.BluetoothOperation;
import com.guide.xiaoguo.weilee.mode.Group_data_mode;
import com.guide.xiaoguo.weilee.mode.GrouporDevice_data_mode;
import com.guide.xiaoguo.weilee.mode.IPrinterOpertion;
import com.guide.xiaoguo.weilee.mode.RTorHis_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class His_Activity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;
    private Context mContext;
    private int i = 0;
    private int j = 0;
    private Spinner g_spinner;
    private Spinner d_spinner;
    private TextView b_time;
    private TextView e_time;
    private Button query_btn;
    private Button connection_btn;
    private Button print_btn;
    private LinearLayout ll_b_time;
    private LinearLayout ll_e_time;
    private ListView his_lv;
    private UserInfo userInfo;
    private String[] Sp_Gitem;
    private String[] Sp_Ditem;
    private List<Group_data_mode> list_group;
    private List<GrouporDevice_data_mode> list_groupordevice;
    private Group_data_mode group_data_mode = new Group_data_mode();
    private GrouporDevice_data_mode grouporDevice_data_mode = new GrouporDevice_data_mode();
    private String Change_Group_ID = "";
    private String Device_ID = "";
    ArrayAdapter<String> D_spAdapter;
    private Tools tools;
    private Thread his_thread;
    private String result;
    private List<RTorHis_data_mode> his_list = new ArrayList<>();
    private RTorHis_data_mode his_mode;
    private MyAdapter myAdapter;
    private ProgressDialog dialog;
    private static boolean isConnected;
    private IPrinterOpertion myOpertion;
    private PrinterInstance mPrinter = null;
    String endTime;
    String startTime;
    String Max;
    String Min;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_data);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        InitView();
        Dealwith();
    }

    public void InitView() {
        tools = new Tools();
        userInfo = (UserInfo) this.getApplication();
        g_spinner = findViewById(R.id.g_spinner);
        list_group = userInfo.getGroup();
        Sp_Gitem = new String[list_group.size() - 1];
        for (i = 1; i < list_group.size(); i++) {
            group_data_mode = list_group.get(i);
            Sp_Gitem[i - 1] = group_data_mode.getGroupName();
            Log.i("ITEM", "InitView:+++++22222 " + Sp_Gitem[i - 1]);
        }
        ArrayAdapter<String> G_spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Sp_Gitem);
        g_spinner.setAdapter(G_spAdapter);
        d_spinner = findViewById(R.id.d_spinner);
        list_groupordevice = userInfo.getGroupordevice();
        Sp_Ditem = new String[list_groupordevice.size()];
        for (i = 0; i < list_groupordevice.size(); i++) {
            grouporDevice_data_mode = list_groupordevice.get(i);
            Sp_Ditem[i] = grouporDevice_data_mode.getDeviceName();
            Log.i("ITEM", "InitView:-----11111" + Sp_Ditem[i]);
        }
        D_spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Sp_Ditem);
        d_spinner.setAdapter(D_spAdapter);

        b_time = findViewById(R.id.b_time);
        b_time.setText(tools.getCurrentTime());
        e_time = findViewById(R.id.e_time);
        e_time.setText(tools.getCurrentTime());
        ll_b_time = findViewById(R.id.ll_b_time);
        ll_e_time = findViewById(R.id.ll_e_time);
        query_btn = findViewById(R.id.query_btn);
        connection_btn = findViewById(R.id.connection_btn);
        print_btn = findViewById(R.id.print_btn);
        his_lv = findViewById(R.id.his_lv);
        myAdapter = new MyAdapter(His_Activity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.list_header, null);
        his_lv.addHeaderView(view);
        GetHistoryData();
        his_lv.setAdapter(myAdapter);

        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Connecting...");
        dialog.setMessage("Please Wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
    }

    public void Dealwith() {
        g_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String group = adapterView.getItemAtPosition(position).toString();
                Log.i("-----", "onItemSelected: " + group);
                Select_Group_device(group);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        d_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String device = adapterView.getItemAtPosition(position).toString();
                DeviceNameToSN(device);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ll_b_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tools.GetDataTime(His_Activity.this, b_time);
            }
        });
        ll_e_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tools.GetDataTime(His_Activity.this, e_time);
            }
        });
        query_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetHistoryData();

            }
        });
        connection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConn();
            }
        });
        print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (his_list.size() > 0) {
                    getStart_EndDatetime();
                    MAX_MIN_Data();
                    //   printTable(mContext.getResources(), mPrinter, true);
                    print_date(mPrinter);
                }else {
                    Toast.makeText(His_Activity.this,"没有数据哦！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void print_date(PrinterInstance mPrinter) {
        mPrinter.init();
        // mPrinter.setPrinter(PrinterConstants.Command.LINE_HEIGHT,1);
        mPrinter.printText("******************************************\n");
        mPrinter.printText("公司名称：" + userInfo.getCompanyName() + "\n");
        mPrinter.printText("序列号：" + Device_ID + "\n");
        mPrinter.printText("记录开始时间：" + startTime.substring(2) + "\n");
        mPrinter.printText("记录结束时间：" + endTime.substring(2) + "\n");
        mPrinter.printText("总计记录调数：" + his_list.size() + "条" + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("******************************************\n");
        mPrinter.printText("温度最大值：" + Max + ",最小值：" + Min + "\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("******************************************\n");
        for (i = 0; i < his_list.size(); i++) {
            if (his_list.get(i).getParam3().equals("")) {
                if (Float.valueOf(his_list.get(i).getParam1()) > 0) {
                    mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "+" + his_list.get(i).getParam1() + "\n");
                } else {
                    mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "-" + his_list.get(i).getParam1() + "\n");
                }
            } else {
                if (Float.valueOf(his_list.get(i).getParam1()) > 0) {
                    if (Float.valueOf(his_list.get(i).getParam3()) > 0) {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "+" + his_list.get(i).getParam1() + "  " + "+" + his_list.get(i).getParam3() + "\n");
                    } else {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "+" + his_list.get(i).getParam1() + "  " + "-" + his_list.get(i).getParam3() + "\n");
                    }
                } else {
                    if (Float.valueOf(his_list.get(i).getParam3()) > 0) {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "-" + his_list.get(i).getParam1() + "  " + "+" + his_list.get(i).getParam3() + "\n");
                    } else {
                        mPrinter.printText(his_list.get(i).getUpDataTime().substring(2) + "  " + "-" + his_list.get(i).getParam1() + "  " + "-" + his_list.get(i).getParam3() + "\n");
                    }
                }
            }
        }
        mPrinter.printText("******************************************\n");

        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("时        间：____________________\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.printText("签收人签字：____________________\n");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 5);


        // getTable方法:参数1,以特定符号分隔的列名; 2,列名分隔符;
        // 3,各列所占字符宽度,中文2个,英文1个. 默认字体总共不要超过48
        // 表格超出部分会另起一行打印.若想手动换行,可加\n.

//        mPrinter.setCharacterMultiple(0, 0);
////        String column = resources.getString(R.string.note_title);
//        Table table;
////        table = new Table(column, ";", new int[]{14, 6, 6, 6});
//
////        table.setColumnAlignRight(true);
//
////        mPrinter.printTable(table);
//
//        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
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
            Min = "-" + MIN;
        }
    }

    public void Select_Group_device(String group) {
        j = 0;
        String groupname = group.trim();
        Log.i("ssssss", "Select_Group_device: " + list_group.size());
        for (i = 0; i < list_group.size(); i++) {
            group_data_mode = list_group.get(i);
            Log.i("ssssss", "Select_Group_device: " + group_data_mode.getGroupName());
            if (groupname.equals(group_data_mode.getGroupName().trim())) {
                Change_Group_ID = group_data_mode.getGroup_ID();
                Log.i("Group_ID+++++", "ChangeGroupNameToID: " + Change_Group_ID);
            }
        }
        for (i = 0; i < list_groupordevice.size(); i++) {
            grouporDevice_data_mode = list_groupordevice.get(i);

            if (Change_Group_ID.equals(grouporDevice_data_mode.getGroup_ID())) {
                Sp_Ditem[j] = grouporDevice_data_mode.getDeviceName();
                Log.i("--------12-36", "Select_Group_device: " + Sp_Ditem[j]);
                j++;

            }
        }
        String[] New_SP_Ditem = new String[j];
        for (i = 0; i < j; i++) {
            New_SP_Ditem[i] = Sp_Ditem[i];
            Log.i("New_SP_Ditem", "Select_Group_device:000000 " + New_SP_Ditem[i]);
        }

        D_spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, New_SP_Ditem);
        d_spinner.setAdapter(D_spAdapter);
    }

    public void DeviceNameToSN(String DeviceName) {
        String name = DeviceName;
        for (i = 0; i < list_groupordevice.size(); i++) {
            grouporDevice_data_mode = list_groupordevice.get(i);
            if (name.equals(grouporDevice_data_mode.getDeviceName())) {
                Device_ID = grouporDevice_data_mode.getSN();
                Log.i("--------12-36", "Select_Group_device: " + Device_ID);
            }
        }
    }

    public void GetHistoryData() {

        his_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                his_list.removeAll(his_list);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "lssj");
                    jsonObject.put("com_id", userInfo.getCompany_ID());
                    jsonObject.put("sn", Device_ID);
                    jsonObject.put("b_time", b_time.getText().toString());
                    jsonObject.put("e_time", e_time.getText().toString());
                    String json = String.valueOf(jsonObject);
                    Log.i("HHHHHHHH", "run: " + json);
                    result = tools.post(userInfo.getUrl(), json);
                    JSONObject Json = new JSONObject(result);
                    if (Json.getString("message").equals("success")) {
                        JSONArray jsonArray = Json.getJSONArray("data");
                        for (i = 0; i < jsonArray.length(); i++) {
                            JSONObject his_json = jsonArray.getJSONObject(i);
                            his_mode = new RTorHis_data_mode();
                            his_mode.setSN(his_json.getString("sn"));
                            his_mode.setStatus(his_json.getString("status"));
                            his_mode.setParam1(his_json.getString("param1"));
                            his_mode.setParam2(his_json.getString("param2"));
                            his_mode.setParam3(his_json.getString("param3"));
                            his_mode.setParam4(his_json.getString("param4"));
                            his_mode.setUpDataTime(his_json.getString("updatetime").substring(0, 19));
                            Log.i("------", "run: " + his_mode.getSN() + "  " + his_mode.getParam1());
                            his_list.add(his_mode);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                myAdapter.notifyDataSetChanged();
                                myAdapter.clearDeviceList();
                                myAdapter.setDeviceList((ArrayList<RTorHis_data_mode>) his_list);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        his_thread.start();
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
        public Object getItem(int i) {
            return devices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            RT_Activity.ViewHolder holder = null;
            if (view == null) {
                holder = new RT_Activity.ViewHolder();
                view = inflater.inflate(R.layout.list_item, null);
                holder.number = view.findViewById(R.id.number);
                holder.sn = view.findViewById(R.id.SN);
                holder.status = view.findViewById(R.id.status);
                holder.param1 = view.findViewById(R.id.param1);
                holder.param2 = view.findViewById(R.id.param2);
                holder.param3 = view.findViewById(R.id.param3);
                holder.param4 = view.findViewById(R.id.param4);
                holder.updatetime = view.findViewById(R.id.updatetime);
                view.setTag(holder);
            } else {
                holder = (RT_Activity.ViewHolder) view.getTag();
            }
            holder.number.setText(String.valueOf(i + 1));
            holder.sn.setText(devices.get(i).getSN());
            Log.i("SNSNSSNNSNSNSNSSN", "getView: " + devices.get(i).getSN());
            holder.status.setText(devices.get(i).getStatus());
            holder.param1.setText(devices.get(i).getParam1());
            holder.param2.setText(devices.get(i).getParam2());
            holder.param3.setText(devices.get(i).getParam3());
            holder.param4.setText(devices.get(i).getParam4());
            holder.updatetime.setText(devices.get(i).getUpDataTime().substring(0, 19));
            return view;
        }
    }

    static class ViewHolder {
        public TextView number;
        public TextView sn;
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
                    break;
                case PrinterConstants.Connect.FAILED:
                    isConnected = false;
                    Toast.makeText(mContext, "connect failed...", Toast.LENGTH_SHORT).show();
                    break;
                case PrinterConstants.Connect.CLOSED:
                    isConnected = false;
                    Toast.makeText(mContext, "connect close...", Toast.LENGTH_SHORT).show();
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
            myOpertion = new BluetoothOperation(His_Activity.this, mHandler);
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
                    Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        his_thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        his_thread.interrupt();
    }
}

