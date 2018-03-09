package com.guide.xiaoguo.weilee.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.activity.Printer_Activity;
import com.guide.xiaoguo.weilee.mode.Group_data_mode;
import com.guide.xiaoguo.weilee.mode.GrouporDevice_data_mode;
import com.guide.xiaoguo.weilee.mode.RTorHis_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class History_Fragment extends Fragment {

    private Tools tools;
    private UserInfo userInfo;
    private Button his_done;
    private EditText his_b_time;
    private EditText his_e_time;
    private Spinner his_d_spinner;
    private Spinner his_g_spinner;
    private ListView his_lv;

    private String[] Sp_Gitem;
    private String[] Sp_Ditem;
    private String Device_ID = "";
    private String Change_Group_ID = "";
    private int i = 0;
    private int j = 0;
    private Button his_printer_btn;

    private Group_data_mode group_data_mode = new Group_data_mode();
    private List<GrouporDevice_data_mode> list_groupordevice;
    private List<Group_data_mode> list_group;
    private GrouporDevice_data_mode grouporDevice_data_mode = new GrouporDevice_data_mode();
    private ArrayAdapter<String> D_spAdapter;

    private Thread his_thread;
    private String result;
    private ArrayList<RTorHis_data_mode> his_list = new ArrayList<>();
    private RTorHis_data_mode his_mode;
    private MyAdapter myAdapter;

    private ProgressDialog hpd;
    ProgressDialog dialog = null;
    private View main_view;

    public History_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_view = inflater.inflate(R.layout.history, container, false);
        InitView();
        Event_DealWith();
        return main_view;
    }

    public void InitView() {
        tools = new Tools();
        his_printer_btn = main_view.findViewById(R.id.his_printer_btn);
        his_printer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.i(".........", "onClick:ssssssssss ");
                Intent intent = new Intent(getActivity(), Printer_Activity.class);
                startActivity(intent);
            }
        });
        userInfo = (UserInfo) getActivity().getApplication();
        his_b_time = main_view.findViewById(R.id.his_b_time);
        his_b_time.setText(tools.getCurrentTime());
        his_b_time.setFocusableInTouchMode(false);
        his_e_time = main_view.findViewById(R.id.his_e_time);
        his_e_time.setText(tools.getCurrentTime());
        his_e_time.setFocusableInTouchMode(false);
        his_g_spinner = main_view.findViewById(R.id.his_g_spinner);
        list_group = userInfo.getGroup();
        Sp_Gitem = new String[list_group.size() - 1];
        for (i = 1; i < list_group.size(); i++) {
            group_data_mode = list_group.get(i);
            Sp_Gitem[i - 1] = group_data_mode.getGroupName();
            Log.i("ITEM", "InitView:+++++22222 " + Sp_Gitem[i - 1]);
        }
        ArrayAdapter<String> G_spAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Sp_Gitem);
        G_spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        his_g_spinner.setAdapter(G_spAdapter);
        his_d_spinner = main_view.findViewById(R.id.his_d_spinner);
        list_groupordevice = userInfo.getGroupordevice();
        Sp_Ditem = new String[list_groupordevice.size()];
        for (i = 0; i < list_groupordevice.size(); i++) {
            grouporDevice_data_mode = list_groupordevice.get(i);
            Sp_Ditem[i] = grouporDevice_data_mode.getDeviceName();
            Log.i("ITEM", "InitView:-----11111" + Sp_Ditem[i]);
        }
        D_spAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Sp_Ditem);
        D_spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        his_d_spinner.setAdapter(D_spAdapter);

        his_lv = main_view.findViewById(R.id.his_lv);
        myAdapter = new MyAdapter(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View haed_view = inflater.inflate(R.layout.list_header, null);
        his_lv.addHeaderView(haed_view);
        his_lv.setAdapter(myAdapter);
        his_done = main_view.findViewById(R.id.his_done);

    }

    public void Event_DealWith() {
        his_g_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        his_d_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String device = adapterView.getItemAtPosition(position).toString();
                DeviceNameToSN(device);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        his_b_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.GetDataTime(getActivity(), his_b_time.getText().toString(), his_b_time);
            }
        });
        his_e_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.GetDataTime(getActivity(), his_e_time.getText().toString(), his_e_time);
            }
        });
        his_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date b_date = null;
                Date e_date = null;
                try {
                    b_date = sdf.parse(his_b_time.getText().toString());
                    e_date = sdf.parse(his_e_time.getText().toString());
                    if (b_date.getTime() > e_date.getTime()) {
                        Toast.makeText(getActivity(), "请检查开始与结束时间是否冲突!", Toast.LENGTH_LONG).show();
                    } else {
                        hpd = ProgressDialog.show(getActivity(), null, "正在获取数据...");
                        hpd.setCanceledOnTouchOutside(true);
                        GetHistoryData();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
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

        D_spAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, New_SP_Ditem);
        D_spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        his_d_spinner.setAdapter(D_spAdapter);
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
                    jsonObject.put("b_time", his_b_time.getText().toString());
                    jsonObject.put("e_time", his_e_time.getText().toString());
                    String json = String.valueOf(jsonObject);
                    Log.i("HHHHHHHH", "run: " + json);
                    result = tools.post(userInfo.getUrl(), json);
                    JSONObject Json = new JSONObject(result);
                    if (Json.getString("message").equals("success")) {
                        JSONArray jsonArray = Json.getJSONArray("data");
                        if (jsonArray.length() >= 1) {
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hpd.dismiss();
                                    myAdapter.clearDeviceList();
                                    myAdapter.setDeviceList((ArrayList<RTorHis_data_mode>) his_list);
                                }
                            });
                            userInfo.his_printer_list = (ArrayList<RTorHis_data_mode>) his_list.clone();
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (hpd != null) {
                                        hpd.dismiss();
                                    }
                                    Toast.makeText(getActivity(), "无数据！", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (hpd != null) {
                                    hpd.dismiss();
                                }
                                Toast.makeText(getActivity(), "请检查您的网络！", Toast.LENGTH_LONG).show();
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


}
