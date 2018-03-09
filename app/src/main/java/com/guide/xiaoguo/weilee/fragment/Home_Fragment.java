package com.guide.xiaoguo.weilee.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.activity.Dialog_Activity;
import com.guide.xiaoguo.weilee.adapter.BleDeviceListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home_Fragment extends Fragment {

    private View main_view;
    ListView listView;
    SwipeRefreshLayout swagLayout;
    BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    BleDeviceListAdapter mBleDeviceListAdapter;
    public static String Device_Address;

    public Home_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        getBleAdapter();
        getScanResualt();
        new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        }).start();
        return main_view;
    }

    private void init() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        }
        // TODO Auto-generated method stub
        listView = (ListView) main_view.findViewById(R.id.lv_deviceList);
        listView.setEmptyView(main_view.findViewById(R.id.pb_empty));
        swagLayout = (SwipeRefreshLayout) main_view.findViewById(R.id.swagLayout);
        swagLayout.setVisibility(View.VISIBLE);
        swagLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                mBleDeviceListAdapter.clear();
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                swagLayout.setRefreshing(false);
            }
        });
        mBleDeviceListAdapter = new BleDeviceListAdapter(getActivity());
        listView.setAdapter(mBleDeviceListAdapter);
        setListItemListener();
    }

    @SuppressLint("NewApi")
    private void getBleAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    @SuppressLint("NewApi")
    private void getScanResualt() {
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi,
                                 final byte[] scanRecord) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mBleDeviceListAdapter.addDevice(device);
                        mBleDeviceListAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }

    private void setListItemListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                BluetoothDevice device = mBleDeviceListAdapter
                        .getDevice(position);
                Device_Address = device.getAddress();
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                final Intent intent = new Intent(getActivity(),
                        Dialog_Activity.class);
                intent.putExtra(Dialog_Activity.EXTRAS_DEVICE_NAME,
                        device.getName());
                intent.putExtra(Dialog_Activity.EXTRAS_DEVICE_ADDRESS,
                        device.getAddress());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBleDeviceListAdapter.clear();
            mBluetoothAdapter.cancelDiscovery();
        }
    }

}
