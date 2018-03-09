package com.guide.xiaoguo.weilee.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.fragment.GPS_Fragment;
import com.guide.xiaoguo.weilee.fragment.History_Fragment;
import com.guide.xiaoguo.weilee.fragment.Home_Fragment;
import com.guide.xiaoguo.weilee.fragment.Mine_Fragment;
import com.guide.xiaoguo.weilee.fragment.RealTime_Fragment;
import com.guide.xiaoguo.weilee.mode.Device_Info;
import com.guide.xiaoguo.weilee.mode.Group_data_mode;
import com.guide.xiaoguo.weilee.mode.GrouporDevice_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {


    private UserInfo userInfo;
    private List<Group_data_mode> list_group;
    private List<GrouporDevice_data_mode> list_groupordevice;
    private long mExitTime;
    private Tools tools;
    private int i = 0;
    private String result;
    private Thread mthread;
    private TabLayout mTablayout;
//    private ProgressDialog mpd;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    boolean IsFrist = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home);
        InitView();
        initEvents();
        if(IsFrist) {
            IsFrist = false;
            GetDeviceorGroup();
        }
    }

    public void GetDeviceorGroup() {

        list_group = userInfo.getGroup();
        list_groupordevice = userInfo.getGroupordevice();
        list_group.removeAll(list_group);
        list_groupordevice.removeAll(list_groupordevice);
        tools = new Tools();
//        mpd = ProgressDialog.show(MainActivity.this, null, "加载中...");

        mthread = new Thread(new Runnable() {
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
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mpd.dismiss();
//                            }
//                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mthread.start();
    }

    public void InitView() {
        userInfo = (UserInfo) getApplication();
        mTablayout = findViewById(R.id.home_tabLayout);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.realcontent, new Home_Fragment());
        fragmentTransaction.commit();
    }

    public void initEvents() {

        mTablayout.addTab(mTablayout.newTab().setIcon(R.mipmap.hlog).setText("首页"), 0);
        mTablayout.addTab(mTablayout.newTab().setIcon(R.mipmap.rtlog).setText("实时"), 1);
        mTablayout.addTab(mTablayout.newTab().setIcon(R.mipmap.gpslog).setText("GPS"), 2);
        mTablayout.addTab(mTablayout.newTab().setIcon(R.mipmap.hislog).setText("历史"), 3);
        mTablayout.addTab(mTablayout.newTab().setIcon(R.mipmap.mine).setText("我的"), 4);
        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == mTablayout.getTabAt(0)) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.realcontent, new Home_Fragment());
                    fragmentTransaction.commit();
                } else if (tab == mTablayout.getTabAt(1)) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.realcontent, new RealTime_Fragment());
                    fragmentTransaction.commit();
                    Log.i("-------", "onTabSelected: RealTime");
                } else if (tab == mTablayout.getTabAt(2)) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.realcontent, new GPS_Fragment());
                    fragmentTransaction.commit();
                } else if (tab == mTablayout.getTabAt(3)) {

                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.realcontent, new History_Fragment());
                    fragmentTransaction.commit();
                } else if (tab == mTablayout.getTabAt(4)) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.realcontent, new Mine_Fragment());
                    fragmentTransaction.commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mthread != null)
            mthread.interrupt();
//        if (mpd != null) {
//            mpd.dismiss();
//            mpd = null;
//        }
    }
}
