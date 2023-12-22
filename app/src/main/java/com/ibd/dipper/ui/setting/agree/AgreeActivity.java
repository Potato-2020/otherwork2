package com.ibd.dipper.ui.setting.agree;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityAgreeBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import java.util.Objects;

import me.goldze.mvvmhabit.base.BaseActivity;

public class AgreeActivity extends BaseActivity<ActivityAgreeBinding, AgreeViewModel> {
    TextView tvTitle;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_agree;
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

        tvTitle = findViewById(R.id.tv_title);
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        tvTitle.setText(getIntent().getStringExtra("title"));
        viewModel.getData(Objects.requireNonNull(getIntent().getStringExtra("title")));
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.content.observe(this, s -> binding.tvContent.setText(Html.fromHtml(s)));
    }
}
