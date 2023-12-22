package com.ibd.dipper.ui.bank;

import android.os.Bundle;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityBankBinding;
import com.ibd.dipper.ui.orders.OrdersViewModel;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class BankActivity extends BaseActivity<ActivityBankBinding, BankViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_bank;
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
        tvTitle.setText(getString(R.string.bank_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.unbindCard.observe(this, s -> {
            //监听肯定按键
            MaterialDialog dialog = new MaterialDialog.Builder(BankActivity.this)
                    .title("提示")//标题
                    .content("确定解除绑定银行卡？")//内容
                    .positiveText("确定") //肯定按键
                    .negativeText("取消") //否定按键
                    .cancelable(true)
                    .onPositive((dialog1, which) -> {
                        viewModel.unBind();
                    })
                    .onNegative((dialog12, which) -> {
                        dismissDialog();
                    })
                    .build();
            dialog.show();
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setData();
    }
}
