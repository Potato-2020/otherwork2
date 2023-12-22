package com.ibd.dipper.ui.setting;

import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivitySettingBinding;
import com.ibd.dipper.ui.SplashActivity;
import com.ibd.dipper.ui.login.LoginActivity;
import com.ibd.dipper.ui.setting.about.AboutActivity;
import com.ibd.dipper.ui.setting.account.AccountActivity;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class SettingActivity extends BaseActivity<ActivitySettingBinding, SettingViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_setting;
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
        tvTitle.setText(getString(R.string.setting_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        binding.tvAppVersion.setText(AboutActivity.getPackageInfo(this).versionName);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.loginOut.observe(this, o -> {
            setResult(201);
            finish();
            startActivity(SplashActivity.class);
        });
        viewModel.uc.toAccountAc.observe(this, o -> {
            Bundle bundle = new Bundle();
            bundle.putString("mobile", getIntent().getStringExtra("mobile"));
            bundle.putString("name", getIntent().getStringExtra("name"));
            startActivity(AccountActivity.class, bundle);
        });
    }
}
