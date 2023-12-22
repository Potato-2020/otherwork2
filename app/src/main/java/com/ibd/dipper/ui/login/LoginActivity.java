package com.ibd.dipper.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityLoginBinding;
import com.ibd.dipper.ui.MainActivity;
import com.ibd.dipper.ui.forget.ForgetActivity;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public int initVariableId() {
        return com.ibd.dipper.BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        StatusBarUtil.setDarkMode(this);
        StatusBarUtil.setTransparentForWindow(this);

        viewModel.type.setValue(0);
        viewModel.shipperList();

        viewModel.type.observe(this, integer -> {
            binding.etCodePsd.setText("");
            binding.etPsd.setText("");
            if (integer == 0) {
                binding.etCodePsd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                binding.etCodePsd.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });
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
        viewModel.uc.toForgetActivity.observe(this, o -> {
            Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 & resultCode == 101) {
            startActivity(MainActivity.class);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (viewModel.type.getValue() == 1)
            viewModel.type.setValue(0);
        else
            finish();
    }
}
