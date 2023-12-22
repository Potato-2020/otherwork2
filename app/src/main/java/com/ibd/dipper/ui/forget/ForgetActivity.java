package com.ibd.dipper.ui.forget;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityForgetBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class ForgetActivity extends BaseActivity<ActivityForgetBinding, ForgetViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_forget;
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
        tvTitle.setText(getString(R.string.forget_psd_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        viewModel.account.setValue("");
        viewModel.code.setValue("");
        viewModel.password.setValue("");

        binding.etAccount.addTextChangedListener(viewModel.accountListen);
        binding.etCodePsd.addTextChangedListener(viewModel.codeListen);
        binding.etPsd.addTextChangedListener(viewModel.passwordListen);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.ontickCall.observe(this, s -> {
            binding.tvCode.setClickable(false);
            binding.tvCode.setText(s);
        });
        viewModel.uc.onfinishCall.observe(this, s -> {
            binding.tvCode.setClickable(true);
            binding.tvCode.setText(s);
        });
        viewModel.uc.loginSuccess.observe(this, o -> {
            setResult(101);
            finish();
        });
    }
}
