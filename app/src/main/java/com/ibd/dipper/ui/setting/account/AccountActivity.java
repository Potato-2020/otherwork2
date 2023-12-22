package com.ibd.dipper.ui.setting.account;

import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityAccountBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class AccountActivity extends BaseActivity<ActivityAccountBinding, AccountViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_account;
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
        tvTitle.setText(getString(R.string.account_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        binding.tvName.setText(getIntent().getStringExtra("name"));
        binding.tvMobile.setText(getIntent().getStringExtra("mobile"));
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.safePop.observe(this, o -> {
            MaterialDialog dialog = new MaterialDialog.Builder(AccountActivity.this)
                    .title("禁用账号")//标题
                    .content("1.禁用账号后将不能再次登录此账号\n2.此账号的财产将被冻结")//内容
                    .positiveText("确定禁用") //肯定按键
                    .negativeText("取消") //否定按键
                    .cancelable(true)
                    .onPositive((dialog1, which) -> {
                        viewModel.disableAccount();
                        dismissDialog();
                    })
                    .onNegative((dialog12, which) -> {
                        dismissDialog();
                    })
                    .build();
            dialog.show();
        });
    }
}
