package com.guide.xiaoguo.weilee.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.GPS_data_mode;
import com.guide.xiaoguo.weilee.mode.Group_data_mode;
import com.guide.xiaoguo.weilee.mode.GrouporDevice_data_mode;
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
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class GPS_Fragment extends Fragment {


    private Button gps_done;
    private EditText gps_b_time;
    private EditText gps_e_time;
    private Spinner gps_d_spinner;
    private Spinner gps_g_spinner;
    private RadioGroup check_RG;
    private MapView map;
    private BaiduMap mBaidu;
    private LocationClient mLocationClient;//位置
    private BDLocationListener myListener;    //位置接口
    //记录经纬度
    private double jing = 0;
    private double wei = 0;
    private boolean isFirst = true;
    private String Device_ID = "";
    private String Change_Group_ID = "";
    private Thread gps_thread;
    private String result;


    List<LatLng> points2 = new ArrayList<LatLng>();
    List<LatLng> points = new ArrayList<>();
    Polyline mMarkerPolyLine = null;
    Marker mMarkerA;
    private Tools tools;
    private UserInfo userInfo;
    private int i = 0;
    private int j = 0;
    int status = 1;


    private List<Group_data_mode> list_group;
    private String[] Sp_Gitem;
    private String[] Sp_Ditem;
    private Group_data_mode group_data_mode = new Group_data_mode();
    private List<GrouporDevice_data_mode> list_groupordevice;
    private GrouporDevice_data_mode grouporDevice_data_mode = new GrouporDevice_data_mode();
    private ArrayAdapter<String> D_spAdapter;
    List<GPS_data_mode> list_gps = new ArrayList<>();
    GPS_data_mode gps_data_mode;

    ProgressDialog gpd;
    View main_view;


    public GPS_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SDKInitializer.initialize(getActivity().getApplicationContext());
        main_view = inflater.inflate(R.layout.gps, container, false);
        initView();
        initLocation();
        Dealwith();
        return main_view;
    }

    private void initView() {
        map = main_view.findViewById(R.id.gps_map);
        //把视图对象转成BaiduMap
        mBaidu = map.getMap();
        mBaidu.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        tools = new Tools();
        userInfo = (UserInfo) getActivity().getApplication();
        gps_b_time = main_view.findViewById(R.id.gps_b_time);
        gps_b_time.setText(tools.getCurrentTime());
        gps_b_time.setFocusableInTouchMode(false);
        gps_e_time = main_view.findViewById(R.id.gps_e_time);
        gps_e_time.setText(tools.getCurrentTime());
        gps_e_time.setFocusableInTouchMode(false);
        gps_g_spinner = main_view.findViewById(R.id.gps_g_spinner);
        list_group = userInfo.getGroup();
        Sp_Gitem = new String[list_group.size() - 1];
        for (i = 1; i < list_group.size(); i++) {
            group_data_mode = list_group.get(i);
            Sp_Gitem[i - 1] = group_data_mode.getGroupName();
            Log.i("ITEM", "InitView:+++++22222 " + Sp_Gitem[i - 1]);
        }
        ArrayAdapter<String> G_spAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Sp_Gitem);
        G_spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gps_g_spinner.setAdapter(G_spAdapter);
        gps_d_spinner = main_view.findViewById(R.id.gps_d_spinner);
        list_groupordevice = userInfo.getGroupordevice();
        Sp_Ditem = new String[list_groupordevice.size()];
        for (i = 0; i < list_groupordevice.size(); i++) {
            grouporDevice_data_mode = list_groupordevice.get(i);
            Sp_Ditem[i] = grouporDevice_data_mode.getDeviceName();
            Log.i("ITEM", "InitView:-----11111" + Sp_Ditem[i]);
        }
        D_spAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Sp_Ditem);
        D_spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gps_d_spinner.setAdapter(D_spAdapter);
        check_RG = main_view.findViewById(R.id.check_RG);
        gps_done = main_view.findViewById(R.id.gps_done);
    }

    public void Dealwith() {
        gps_g_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        gps_d_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String device = adapterView.getItemAtPosition(position).toString();
                DeviceNameToSN(device);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        check_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int rg_ID = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = main_view.findViewById(rg_ID);
                Log.i("setOnCheckedChange", "onCheckedChanged: " + rb.getText().toString());
                String checkStr = rb.getText().toString();
                if (checkStr.equals("实时数据")) {
                    status = 1;
                } else if (checkStr.equals("历史数据")) {
                    status = 2;
                } else if (checkStr.equals("轨迹回放")) {
                    status = 3;
                }

            }
        });
        gps_b_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.GetDataTime(getActivity(), gps_b_time.getText().toString(), gps_b_time);
            }
        });
        gps_e_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.GetDataTime(getActivity(), gps_e_time.getText().toString(), gps_e_time);
            }
        });
        gps_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBaidu.clear();
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date b_date = null;
                Date e_date = null;
                try {
                    b_date = sdf.parse(gps_b_time.getText().toString());
                    e_date = sdf.parse(gps_e_time.getText().toString());
                    if (b_date.getTime() > e_date.getTime()) {
                        Toast.makeText(getActivity(), "请检查开始与结束时间是否冲突!", Toast.LENGTH_LONG).show();
                    } else {
                        gpd = ProgressDialog.show(getActivity(), null, "正在获取数据...");
                        GetGPSData();
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
        gps_d_spinner.setAdapter(D_spAdapter);
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

    public void GetGPSData() {
        list_gps.removeAll(list_gps);
        gps_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (status == 1) {
                        jsonObject.put("type", "gps");
                        jsonObject.put("status", 1);
                        jsonObject.put("sn", Device_ID);
                    } else {
                        jsonObject.put("type", "gps");
                        jsonObject.put("status", 2);
                        jsonObject.put("sn", Device_ID);
                        jsonObject.put("b_time", gps_b_time.getText());
                        jsonObject.put("e_time", gps_e_time.getText());
                    }
                    String json = String.valueOf(jsonObject);
                    result = tools.post(userInfo.getUrl(), json);
                    JSONObject Json = new JSONObject(result);
                    if (Json.getString("message").equals("success")) {
                        JSONArray GPS_JA = Json.getJSONArray("data");
                        if (GPS_JA.length() >= 1) {
                            int flg = 1;
                            for (i = 0; i < GPS_JA.length(); i++) {
                                JSONObject gps_json = GPS_JA.getJSONObject(i);
                                if (gps_json != null && gps_json.length() > 1) {
                                    gps_data_mode = new GPS_data_mode();
                                    gps_data_mode.setLatitude(gps_json.getString("lat"));
                                    gps_data_mode.setLongitude(gps_json.getString("lon"));
                                    Log.i("定位数据" + flg++, "run: " + gps_json.getString("lat") + "    " + gps_json.getString("lon"));
                                    list_gps.add(gps_data_mode);
                                } else if (gps_json != null && gps_json.length() == 1) {
                                    Log.i("历史数据", "run: " + gps_json.getString("lat") + "111" + gps_json.getString("lon"));
                                    wei = Double.valueOf(gps_json.getString("lat"));
                                    jing = Double.valueOf(gps_json.getString("lon"));
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!(list_gps.size() >= 1)) {
                                        Toast.makeText(getActivity(), "无定位数据！", Toast.LENGTH_LONG).show();
                                    }
                                    if (gpd != null) {
                                        gpd.dismiss();
                                        gpd = null;
                                    }
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "无定位数据！", Toast.LENGTH_LONG).show();
                                    if (gpd != null) {
                                        gpd.dismiss();
                                        gpd = null;
                                    }
                                }
                            });
                        }
                    }
                    if (status == 1) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setRTMark(wei, jing);
                            }
                        });
                    } else if (status == 2) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (list_gps.size() > 0)
                                    setALlMark(list_gps);
                            }
                        });
                    } else if (status == 3) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (list_gps.size() > 0) {
                                    LatandLon(list_gps);
                                    addCustomElementsDemo();
                                    Refresh();
                                    list_gps.removeAll(list_gps);
                                    list_gps = null;
                                }
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
        gps_thread.start();
    }

    //设置所有的数据点---历史查询
    public void setALlMark(List<GPS_data_mode> list) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.small_mark);
        Marker marker;
        GPS_data_mode gps_data_mode = new GPS_data_mode();

        for (i = 0; i < list.size(); i++) {
            gps_data_mode = list.get(i);
            LatLng latLng = new LatLng(Double.valueOf(gps_data_mode.getLatitude()), Double.valueOf(gps_data_mode.getLongitude()));
            Log.i("setALlMark", "setALlMark: " + gps_data_mode.getLatitude() + "   " + gps_data_mode.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(bitmap);
            marker = (Marker) mBaidu.addOverlay(markerOptions);

        }
        setRTMark(Double.valueOf(gps_data_mode.getLatitude()), Double.valueOf(gps_data_mode.getLongitude()));
    }

    public void setRTMark(double lat, double lot) {
        LatLng la = new LatLng(lat,
                lot);
        MapStatus mapStatus = new MapStatus.Builder().target(la).zoom(16).build();
        Log.i("--------------", "onReceiveLocation: " + wei + "+++++++++" + jing);
        MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaidu.animateMapStatus(msu);
    }

    public void LatandLon(List<GPS_data_mode> list) {
        GPS_data_mode gps_data_mode = new GPS_data_mode();
        for (i = 0; i < list.size(); i++) {
            gps_data_mode = list.get(i);
            LatLng latLng = new LatLng(Double.valueOf(gps_data_mode.getLatitude()), Double.valueOf(gps_data_mode.getLongitude()));
            points.add(latLng);
        }
    }

    /**
     * 添加一条初始折线
     */
    public void addCustomElementsDemo() {
        points2.removeAll(points2);
        points2.add(points.get(points.size() - 1));
        points2.add(points.get(points.size() - 2));
        setRTMark(Double.valueOf(list_gps.get(0).getLatitude()), Double.valueOf(list_gps.get(0).getLongitude()));
        OverlayOptions ooPolyline = new PolylineOptions().width(12)
                .color(0xAAFF0000).points(points2);
        MapStatus mapStatus = new MapStatus.Builder().target(points2.get(0)).zoom(17).build();
        MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaidu.animateMapStatus(msu);
        mMarkerPolyLine = (Polyline) mBaidu.addOverlay(ooPolyline);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.small_mark);

        OverlayOptions ooA = new MarkerOptions().position(points.get(1)).icon(bitmap);
        mMarkerA = (Marker) (mBaidu.addOverlay(ooA));
    }

    /**
     * 动态轨迹回放
     */
    int p;
    Timer timer;

    public void Refresh() {
        if (points.size() > 3) {
            p = points.size() - 3;
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                LatLng k = points.get(p);
                MapStatus mapStatus = new MapStatus.Builder().target(k).zoom(17).build();
                MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mapStatus);
                mBaidu.animateMapStatus(msu);
                if (p > 0) {
                    p--;
                    Log.i("pppppppppppppp", "handleMessage: " + p);
                } else {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    MapStatus mapStatus1 = new MapStatus.Builder().target(k).zoom(6).build();
                    MapStatusUpdate msu1 = MapStatusUpdateFactory.newMapStatus(mapStatus);
                }
                points2.add(k);
                mMarkerPolyLine.setPoints(points2);
                mMarkerA.setPosition(k);

            }
        };

        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        timer = new Timer(true);
        timer.schedule(task, 900, 2000);
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getActivity());
        myListener = new MyLocationListener();
        //注册监听
        mLocationClient.registerLocationListener(myListener);
        //设置LocationClient
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        mLocationClient.setLocOption(option);
        //初始化图标
//        mIconLocation = BitmapDescriptorFactory.fromResource(R.mipmap.home1);

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //设置我当前位置的数据
            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(20)//
                    .direction(location.getDirection())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude())//
                    .speed(location.getSpeed())
                    .build();
            mBaidu.setMyLocationData(data);
            //设置自定义的图片
//            MyLocationConfiguration config = new MyLocationConfiguration(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.FOLLOWING
//                    , true,mIconLocation);
//            mBaidu.setMyLocationConfigeration(config);

            //如果是第一次进入，会显示当前位置
            if (isFirst) {
                jing = location.getLongitude();
                wei = location.getLatitude();

                LatLng la = new LatLng(wei,
                        jing);
                MapStatus mapStatus = new MapStatus.Builder().target(la).zoom(16).build();
                Log.i("--------------", "onReceiveLocation: " + wei + "+++++++++" + jing);
                MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mapStatus);
                mBaidu.animateMapStatus(msu);

                isFirst = false;
//                当前位置的名称            Toast.makeText(MainActivity.this,location.getAddrStr(),0).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        if (gps_thread != null)
            gps_thread.interrupt();
        if (gpd != null) {
            gpd.dismiss();
            gpd = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mLocationClient != null)
        {
            mLocationClient =null;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mBaidu.setMyLocationEnabled(false);
        //停止定位
        mLocationClient.stop();
        if (gps_thread != null)
            gps_thread.interrupt();
        if (gpd != null) {
            gpd.dismiss();
            gpd = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBaidu.setMyLocationEnabled(true);
        if (!mLocationClient.isStarted())
            //开启定位
            mLocationClient.start();
    }
}
