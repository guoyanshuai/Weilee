package com.guide.xiaoguo.weilee.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RealTime_Fragment extends Fragment {

    private Spinner spinner;
    private GridView rt_gv;
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
    private ProgressDialog rtpd;
    private TabLayout rt_Tablayout;
    private long mExitTime;
    private View main_view;

    public RealTime_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_view = inflater.inflate(R.layout.realtime, container, false);
        InitView();
        DealWith();
        return main_view;
    }
    public void InitView() {
        userInfo = (UserInfo) getActivity().getApplication();
        spinner = main_view.findViewById(R.id.rt_g_spinner);
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
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Sp_item);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spAdapter);
        rt_gv = main_view.findViewById(R.id.rt_gv);
        myAdapter = new MyAdapter(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.list_header, null);
//        GetRealTimeData();
        rt_gv.setAdapter(myAdapter);
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
        rtpd = ProgressDialog.show(getActivity(), null, "加载中...");
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
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rtpd.dismiss();
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
        rt_thread.start();

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
                view = inflater.inflate(R.layout.gv_item, null);
                holder.sn = view.findViewById(R.id.sn_name);
                holder.param1 = view.findViewById(R.id.temp1);
                holder.param2 = view.findViewById(R.id.hum1);
                holder.param3 = view.findViewById(R.id.temp2);
                holder.param4 = view.findViewById(R.id.hum2);
                holder.updatetime = view.findViewById(R.id.gv_updatetime);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.sn.setText(devices.get(position).getDeviceName());
            if (!devices.get(position).getParam1().trim().equals("")) {
                holder.param1.setText(devices.get(position).getParam1() + "℃");
            } else {
                holder.param1.setText("");
            }
            if (!devices.get(position).getParam2().trim().equals("")) {
                holder.param2.setText(devices.get(position).getParam2() + "%");
            }else {
                holder.param2.setText("");
            }
            if (!devices.get(position).getParam3().trim().equals("")) {
                holder.param3.setText(devices.get(position).getParam3() + "℃");
            }else {
                holder.param3.setText("");
            }
            if (!devices.get(position).getParam4().trim().equals("")) {
                holder.param4.setText(devices.get(position).getParam4() + "%");
            }else {
                holder.param4.setText("");
            }
            holder.updatetime.setText(devices.get(position).getUpDataTime().substring(0, 19));
            view.setBackgroundColor(Color.WHITE);
            if (Integer.valueOf(devices.get(position).getStatus().trim()) == 2) {
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
        public TextView sn;
        public TextView param1;
        public TextView param2;
        public TextView param3;
        public TextView param4;
        public TextView updatetime;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rt_thread != null)
            rt_thread.interrupt();
        if (rtpd != null) {
            rtpd.dismiss();
            rtpd = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (rt_thread != null)
            rt_thread.interrupt();
        if (rtpd != null) {
            rtpd.dismiss();
            rtpd = null;
        }
    }
}
