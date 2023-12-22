package com.ibd.dipper.ui.withdrawDetail;

import android.os.Bundle;
import android.widget.TextView;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityWithdrawDetailBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class WithdrawDetailActivity extends BaseActivity<ActivityWithdrawDetailBinding,WithdrawDetailViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_withdraw_detail;
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
        tvTitle.setText(getString(R.string.withdraw_detail_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        viewModel.page.setValue(1);

        viewModel.setData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.observe(this, o -> {
            //结束刷新
            binding.refreshLayout.finishRefresh();
            binding.refreshLayout.setNoMoreData(false);
        });
        //监听上拉加载完成
        viewModel.uc.finishLoadmore.observe(this, o -> {
            //结束刷新
            binding.refreshLayout.finishLoadMore();
        });
        viewModel.uc.finishNoMore.observe(this, o -> {
            //没有更多
            binding.refreshLayout.finishLoadMoreWithNoMoreData();
        });
    }
}
