package com.ibd.dipper.ui.taskResult;

import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.databinding.ActivityTaskResultBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class TaskResultActivity extends BaseActivity<ActivityTaskResultBinding, TaskResultViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_task_result;
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

        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("运单详情");

        binding.rcv0.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rcv1.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rcv2.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rcv3.setLayoutManager(new GridLayoutManager(this, 3));

        viewModel.type.setValue(getIntent().getIntExtra("type", 0));
        viewModel.isresign.setValue(getIntent().getBooleanExtra("isresign", false));
        viewModel.reasonShow.setValue(false);
        if (getIntent().getSerializableExtra("data") != null)
            viewModel.beanOrders.setValue((BeanOrdersDetail) getIntent().getSerializableExtra("data"));

//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            public void uncaughtException(Thread thread, Throwable ex) {
////任意一个线程异常后统一的处理
//                System.out.println(ex.getLocalizedMessage());
//
//                finish();
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.taskDetail(getIntent().getStringExtra("id"));
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.doneSuccess.observe(this, o -> {
            setResult(301);
            finish();
        });
    }
}
