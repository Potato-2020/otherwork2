package com.ibd.dipper.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ibd.dipper.R;
import com.ibd.dipper.ui.login.LoginActivity;
import com.ibd.dipper.ui.online.OnlineActivity;
import com.leaf.library.StatusBarUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class SplashActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        StatusBarUtil.setDarkMode(this);
        StatusBarUtil.setTransparentForWindow(this);

//        SPUtils.getInstance(USER).put(TOKEN,"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzIiwiZXhwIjoxNjE5OTU5NTMxLCJpYXQiOjE2MTg0ODgzMDJ9.v2uE2U_9hOdu6ZoXB13kfmEfzcFgLAWT_LIV0CFsl1o");

        //请求打开相机权限
        RxPermissions rxPermissions = new RxPermissions(SplashActivity.this);
        rxPermissions.request(Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        try {
                            new Handler().postDelayed(() -> runOnUiThread(() -> {
                                if (SPUtils.getInstance(USER).getString(TOKEN).isEmpty())
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                else
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }), 1000);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtils.showShort("权限被拒绝，请到设置中打开！");
                    }
                });
    }
}
