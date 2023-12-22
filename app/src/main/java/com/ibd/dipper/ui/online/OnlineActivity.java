package com.ibd.dipper.ui.online;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityOnlineBinding;
import com.ibd.dipper.uiPopupWindow.PhonePopupWindow;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class OnlineActivity extends BaseActivity<ActivityOnlineBinding, OnlineViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_online;
    }

    @Override
    public int initVariableId() {
        return com.ibd.dipper.BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColor(this, UIUtils.getColor(R.color.blue));

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.online_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());
    }

    @SuppressLint("CheckResult")
    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.phonePop.observe(this, s -> {
            PhonePopupWindow phonePopupWindow = new PhonePopupWindow(OnlineActivity.this, viewModel, s);
            phonePopupWindow.showPopupWindow();
        });

        viewModel.uc.phonePermissions.observe(this, s -> {
            //请求打开电话
            RxPermissions rxPermissions = new RxPermissions(OnlineActivity.this);
            rxPermissions.request(Manifest.permission.CALL_PHONE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + s));
                                startActivity(intent);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtils.showShort("拨打电话权限被拒绝，请到设置中打开！");
                        }
                    });
        });
    }
}
