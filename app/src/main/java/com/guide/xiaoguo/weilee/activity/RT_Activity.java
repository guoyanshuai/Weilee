package com.guide.xiaoguo.weilee.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.Group_data_mode;
import com.guide.xiaoguo.weilee.mode.RTorHis_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RT_Activity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    private Spinner spinner;
    private ListView rt_lv;
    private String[] Sp_item;
    private List<Group_data_mode> list_group;
    private Group_data_mode group_data_mode = new Group_data_mode();
    private UserInfo userInfo;
    private String Change_Group_ID = "";
    private String Change_Group_Name = "";
    private int i = 0;
    private Tools tools;
    private String result = "";
    private List<RTorHis_data_mode> rt_list = new ArrayList<>();
    private RTorHis_data_mode rt_mode;
    private String Company_id = new String();
    MyAdapter myAdapter;
    private Thread rt_thread;
    TimerTask task;
    Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_data);
        ActionBar actionBar = getSupportActionBar();/*隐藏标题栏*/
        actionBar.hide();
        InitView();
        DealWith();
    }

    public void InitView() {
        userInfo = (UserInfo) this.getApplication();
        spinner = findViewById(R.id.spinner);
        Company_id = userInfo.getCompany_ID();
        Log.i("IDIDIDIDIDIDID", "InitView: " + Company_id + userInfo.getAccount() + userInfo.getAccount_ID());
        list_group = userInfo.getGroup();
        Log.i("------", "InitView: " + list_group.size());
        Group_data_mode group_data_mode = new Group_data_mode();
        Sp_item = new String[list_group.size()];
        for (i = 0; i < list_group.size(); i++) {
            group_data_mode = list_group.get(i);
            Sp_item[i] = group_data_mode.getGroupName();
            Log.i("ITEM", "InitView:++++++++ " + Sp_item[i]);
        }
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Sp_item);
        spinner.setAdapter(spAdapter);
        rt_lv = findViewById(R.id.rt_lv);
        myAdapter = new MyAdapter(RT_Activity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.list_header, null);
        rt_lv.addHeaderView(view);
//        GetRealTimeData();
        rt_lv.setAdapter(myAdapter);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                rt_thread.start();
            }
        };

    }

    public void DealWith() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapterView.getItemAtPosition(i).toString();
                int j = 0;
                for (j = 0; j < Sp_item.length; j++) {
                    if (str.equals(Sp_item[j])) {
                        Change_Group_Name = Sp_item[j];
                        Log.i("GroupName", "onItemSelected: " + Change_Group_Name);
                        ChangeGroupNameToID();
                        GetRealTimeData();
                        timer.schedule(task,1000);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void ChangeGroupNameToID() {
        for (i = 0; i < list_group.size(); i++) {
            group_data_mode = list_group.get(i);
            if (Change_Group_Name.equals(group_data_mode.getGroupName())) {
                Change_Group_ID = group_data_mode.getGroup_ID();
                Log.i("Group_ID+++++", "ChangeGroupNameToID: " + Change_Group_ID);
            }
        }
    }

    public void GetRealTimeData() {
        rt_list.removeAll(rt_list);
        tools = new Tools();
        rt_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "ssjk");
                    jsonObject.put("com_id", Company_id);
                    jsonObject.put("g_id", Change_Group_ID);

                    String json = String.valueOf(jsonObject);
                    result = tools.post(userInfo.getUrl(), json);
                    JSONObject Json = new JSONObject(result);
                    Log.i("---------", "run: " + result);
                    String msg = Json.getString("message");
                    if (msg.equals("success")) {
                        JSONArray jsonArray = Json.getJSONArray("data");
                        for (i = 0; i < jsonArray.length(); i++) {
                            JSONObject rt_json = new JSONObject();
                            rt_json = jsonArray.getJSONObject(i);
                            rt_mode = new RTorHis_data_mode();
                            rt_mode.setSN(rt_json.getString("sn"));
                            rt_mode.setStatus(rt_json.getString("status"));
                            rt_mode.setParam1(rt_json.getString("param1"));
                            rt_mode.setParam2(rt_json.getString("param2"));
                            rt_mode.setParam3(rt_json.getString("param3"));
                            rt_mode.setParam4(rt_json.getString("param4"));
                            rt_mode.setUpDataTime(rt_json.getString("updatetime"));
                            rt_mode.setDeviceName(rt_json.getString("d_name"));
                            Log.i("------", "run: " + rt_mode.getSN() + "  " + rt_mode.getParam1());
                            rt_list.add(rt_mode);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                myAdapter.notifyDataSetChanged();
                                myAdapter.clearDeviceList();
                                myAdapter.setDeviceList((ArrayList<RTorHis_data_mode>) rt_list);
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
        //rt_thread.start();

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
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
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
                holder = (ViewHolder) view.getTag();
            }
            holder.number.setText(String.valueOf(i + 1));
            holder.sn.setText(devices.get(i).getDeviceName());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        if (rt_thread != null)
            rt_thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (rt_thread != null)
            rt_thread.interrupt();
    }
}
