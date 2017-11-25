
package com.guide.xiaoguo.weilee.activity;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.guide.xiaoguo.weilee.R;
import com.guide.xiaoguo.weilee.fragment.GPS_Fragment;
import com.guide.xiaoguo.weilee.fragment.His_Fragment;
import com.guide.xiaoguo.weilee.fragment.Home_Fragment;
import com.guide.xiaoguo.weilee.fragment.Mine_Fragment;
import com.guide.xiaoguo.weilee.fragment.RT_Fragment;

import java.util.ArrayList;
import java.util.List;

public class Home_Activity extends AppCompatActivity {

    List<Fragment> mFragments = new ArrayList<>();
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private String[] mTitles = {"首页", "实时", "GPS", "历史", "我的"};
    private TabLayout.Tab mHome;
    private TabLayout.Tab mRT;
    private TabLayout.Tab mHis;
    private TabLayout.Tab mGPS;
    private TabLayout.Tab mMine;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homebase);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        InitView();

    }

    public void InitView() {
        mFragments.add(new Home_Fragment());
        mFragments.add(new RT_Fragment());
        mFragments.add(new GPS_Fragment());
        mFragments.add(new His_Fragment());
        mFragments.add(new Mine_Fragment());
        mViewPager = findViewById(R.id.viewPager);
        mTablayout = findViewById(R.id.tabLayout);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        mTablayout.setupWithViewPager(mViewPager);

        mHome = mTablayout.getTabAt(0);
        mRT = mTablayout.getTabAt(1);
        mGPS = mTablayout.getTabAt(2);
        mHis = mTablayout.getTabAt(3);
        mMine = mTablayout.getTabAt(4);

        mHome.setIcon(R.mipmap.homelog);
        mRT.setIcon(R.mipmap.rtlog);
        mGPS.setIcon(R.mipmap.gpslog);
        mHis.setIcon(R.mipmap.hislog);
        mMine.setIcon(R.mipmap.mine);

    }
    public void initEvents(){
        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == mTablayout.getTabAt(0)) {
                    mHome.setIcon(R.mipmap.homelog);
                    mViewPager.setCurrentItem(0);
                } else if (tab == mTablayout.getTabAt(1)) {
                    mRT.setIcon(R.mipmap.rtlog);
                    mViewPager.setCurrentItem(1);
                } else if (tab == mTablayout.getTabAt(2)) {
                    mGPS.setIcon(R.mipmap.gpslog);
                    mViewPager.setCurrentItem(2);
                }else if (tab == mTablayout.getTabAt(3)){
                    mHis.setIcon(R.mipmap.hislog);
                    mViewPager.setCurrentItem(3);
                }else if (tab == mTablayout.getTabAt(3)){
                    mMine.setIcon(R.mipmap.mine);
                    mViewPager.setCurrentItem(3);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == mTablayout.getTabAt(0)) {
                    mHome.setIcon(R.mipmap.homelog);
                } else if (tab == mTablayout.getTabAt(1)) {
                    mRT.setIcon(R.mipmap.rtlog);
                } else if (tab == mTablayout.getTabAt(2)) {
                    mGPS.setIcon(R.mipmap.gpslog);
                }else if (tab == mTablayout.getTabAt(3)){
                    mHis.setIcon(R.mipmap.hislog);
                }else if (tab == mTablayout.getTabAt(3)){
                    mMine.setIcon(R.mipmap.mine);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
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
}
