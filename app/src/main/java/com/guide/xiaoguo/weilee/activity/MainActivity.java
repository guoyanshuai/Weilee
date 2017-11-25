package com.guide.xiaoguo.weilee.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.mode.Group_data_mode;
import com.guide.xiaoguo.weilee.mode.GrouporDevice_data_mode;
import com.guide.xiaoguo.weilee.mode.UserInfo;
import com.guide.xiaoguo.weilee.utils.Tools;
import com.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button rt_btn;
    private Button his_btn;
    private Button gps_btn;
    private UserInfo userInfo;
    private List<Group_data_mode> list_group;
    private List<GrouporDevice_data_mode> list_groupordevice;
    private long mExitTime;
    private Tools tools;
    private int i = 0;
    private String result;
    private Thread mthread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();/*隐藏标题栏*/
        actionBar.hide();
        //Sliding_Menu();
        InitView();
        GetDeviceorGroup();
    }

    public void GetDeviceorGroup() {

        list_group = userInfo.getGroup();
        list_groupordevice = userInfo.getGroupordevice();
        list_group.removeAll(list_group);
        list_groupordevice.removeAll(list_groupordevice);
        tools = new Tools();
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
        rt_btn = findViewById(R.id.rt_btn);
        rt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RT_Activity.class);
                startActivity(intent);
            }
        });
        his_btn = findViewById(R.id.his_btn);
        his_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, His_Activity.class);
                startActivity(intent);
            }
        });
        gps_btn = findViewById(R.id.gps_btn);
        gps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GPS_Activity.class);
                startActivity(intent);
            }
        });
    }

 /*   public void Sliding_Menu() {
        final SlidingMenu menu = new SlidingMenu(this);
        // 设置为左滑菜单
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置滑动阴影的宽度
        menu.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动阴影的图像资源
        menu.setShadowDrawable(R.drawable.shadow);
        // 设置滑动菜单划出时主页面显示的剩余宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        // 附加在Activity上
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // 设置滑动菜单的布局
        menu.setMenu(R.layout.slidingmenu);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.home2);
        ImageView imageView = findViewById(R.id.imageView);
        Tools tools = new Tools();
        bitmap = tools.toRoundBitmap(bitmap);
        bitmap = tools.zoomImg(bitmap, 200, 200);
        imageView.setImageBitmap(bitmap);
    }*/

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
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
        if (mthread!=null)
        mthread.interrupt();
    }
}
