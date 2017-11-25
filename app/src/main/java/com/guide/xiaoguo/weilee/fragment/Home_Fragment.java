package com.guide.xiaoguo.weilee.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.activity.LoginActivity;
import com.guide.xiaoguo.weilee.mode.Group_data_mode;
import com.guide.xiaoguo.weilee.mode.GrouporDevice_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home_Fragment extends Fragment {

    private UserInfo userInfo;
    private List<Group_data_mode> list_group;
    private List<GrouporDevice_data_mode> list_groupordevice;
    private Tools tools;
    private int i = 0;
    private String result;
    private Thread hthread;
    ProgressDialog hpd;

    public Home_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        InitView();
        GetDeviceorGroup();
        return view;
    }

    public void InitView() {
        userInfo = (UserInfo) getActivity().getApplication();
    }

    public void GetDeviceorGroup() {
        hpd = ProgressDialog.show(getActivity(), " ", "缓冲中...");
        list_group = userInfo.getGroup();
        list_groupordevice = userInfo.getGroupordevice();
        list_group.removeAll(list_group);
        list_groupordevice.removeAll(list_groupordevice);
        tools = new Tools();
        hthread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "dg");
                    jsonObject.put("com_id", userInfo.getCompany_ID());
                    String json = String.valueOf(jsonObject);
                    result = tools.post(userInfo.getUrl(), json);
                    JSONObject Json = new JSONObject(result);
                    if (Json.getString("message").equals("success")) {
                        JSONArray Group_JA = Json.getJSONArray("group_data");
                        JSONArray Device_JA = Json.getJSONArray("device_data");
                        Group_data_mode group0 = new Group_data_mode();
                        group0.setGroup_ID("");
                        group0.setGroupName("全部分组");
                        Log.i("group", "GetDeviceorGroup:++++ " + group0.getGroup_ID() + "   " + group0.getGroupName());
                        list_group.add(group0);
                        for (i = 0; i < Group_JA.length(); i++) {
                            JSONObject Object = Group_JA.getJSONObject(i);
                            Group_data_mode group = new Group_data_mode();
                            group.setGroup_ID(Object.getString("g_id"));
                            group.setGroupName(Object.getString("g_name"));
                            Log.i("group", "GetDeviceorGroup:++++ " + group.getGroup_ID() + "   " + group.getGroupName());
                            list_group.add(group);
                        }
                        for (i = 0; i < Device_JA.length(); i++) {
                            JSONObject Object = Device_JA.getJSONObject(i);
                            GrouporDevice_data_mode groupordevice = new GrouporDevice_data_mode();
                            groupordevice.setSN(Object.getString("d_sn"));
                            groupordevice.setDeviceName(Object.getString("d_name"));
                            groupordevice.setGroup_ID(Object.getString("g_id"));
                            Log.i("device", "GetDeviceorGroup:++++ " + groupordevice.getGroup_ID() + "   " + groupordevice.getSN() + "   " + groupordevice.getDeviceName());
                            list_groupordevice.add(groupordevice);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hpd.dismiss();
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
        hthread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hthread != null) {
            hthread.interrupt();
        }
        if (hpd !=null){
            hpd.dismiss();
        }
    }

}
