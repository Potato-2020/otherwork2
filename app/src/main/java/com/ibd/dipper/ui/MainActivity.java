package com.ibd.dipper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ibd.dipper.BR;
import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityMainBinding;
import com.ibd.dipper.service.Data;
import com.ibd.dipper.service.GPSService;
import com.ibd.dipper.service.LongRunningService;
import com.ibd.dipper.ui.bill.BillFragment;
import com.ibd.dipper.ui.mine.MineFragment;
import com.ibd.dipper.ui.orders.OrdersFragment;
import com.ibd.dipper.ui.task.TaskFragment;
import com.leaf.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.KLog;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private List<BaseFragment> mFragments;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(this, LongRunningService.class);
//        startService(intent);
//    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparentForWindow(this);

        //初始化Fragment
        initFragment();

        binding.navView.setTextSize(10);
        binding.navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        Intent intent = new Intent(getApplicationContext(), LongRunningService.class);
//        startService(intent);
//
//        Data.setOrderID("YD20220226120148377");

//        Intent intent2 = new Intent(this, GPSService.class);
//        startService(intent2);
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new OrdersFragment());
        mFragments.add(new TaskFragment());
        mFragments.add(new BillFragment());
        mFragments.add(new MineFragment());

        binding.viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.navView.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = menuItem -> {
        switch (menuItem.getItemId()) {
            case R.id.navigation_info:
                binding.viewPager.setCurrentItem(0);
                break;
            case R.id.navigation_assets:
                binding.viewPager.setCurrentItem(1);
                break;
            case R.id.navigation_market:
                binding.viewPager.setCurrentItem(2);
                break;
            case R.id.navigation_contract:
                binding.viewPager.setCurrentItem(3);
                break;
            default:
                break;
        }
        return true;
    };

    public class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.turnSystem.observe(this, integer -> {
            KLog.e("???????????");
            binding.viewPager.setCurrentItem(integer);
        });
    }
}
