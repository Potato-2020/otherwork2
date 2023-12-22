package com.ibd.dipper.ui.balance;

import android.os.Bundle;
import android.widget.TextView;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityBalanceBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class BalanceActivity extends BaseActivity<ActivityBalanceBinding, BalanceViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_balance;
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
        tvTitle.setText(getString(R.string.balance_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.accInfoQuery();
    }
}
