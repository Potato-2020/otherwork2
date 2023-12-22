package com.ibd.dipper.ui.msg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityMsgBinding;
import com.ibd.dipper.ui.complaint.ComplaintActivity;
import com.ibd.dipper.ui.msgDetail.MsgDetailActivity;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

public class MsgActivity extends BaseActivity<ActivityMsgBinding, MsgViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_msg;
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
        tvTitle.setText(getString(R.string.msg));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.toMsgDetail.observe(this, bundle -> {
            Intent intent = new Intent(MsgActivity.this, MsgDetailActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            switch (resultCode) {
                case 200:
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("toMe", true);
                    startActivity(ComplaintActivity.class, bundle);
                    break;
                case 201:
                    finish();
                    RxBus.getDefault().post(new SystemTurnEvent(0, 0));
                    break;
                case 202:
                    finish();
                    RxBus.getDefault().post(new SystemTurnEvent(1, 0));
                    break;
                case 203:
                    finish();
                    RxBus.getDefault().post(new SystemTurnEvent(1, 1));
                    break;
                case 204:
                    finish();
                    RxBus.getDefault().post(new SystemTurnEvent(2, 0));
                    break;

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.info();
    }
}
