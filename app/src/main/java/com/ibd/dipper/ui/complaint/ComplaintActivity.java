package com.ibd.dipper.ui.complaint;

import android.os.Bundle;
import android.widget.TextView;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityComplaintBinding;
import com.ibd.dipper.uiPopupWindow.ComplaintPopupWindow;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import me.goldze.mvvmhabit.base.BaseActivity;

public class ComplaintActivity extends BaseActivity<ActivityComplaintBinding, ComplaintViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_complaint;
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
        tvTitle.setText(getString(R.string.complaint_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        if (getIntent().getBooleanExtra("toMe", false))
            viewModel.type.setValue(1);
        else
            viewModel.type.setValue(0);
        viewModel.page.setValue(1);
        viewModel.setData();

        binding.refreshLayout.autoLoadMore();
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(this));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            viewModel.page.setValue(1);
            viewModel.setData();
        });
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            viewModel.page.setValue((viewModel.page.getValue() + 1));
            viewModel.setData();
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.complaintItemViewModel.observe(this, complaintItemViewModel -> {
            ComplaintPopupWindow complaintPopupWindow = new ComplaintPopupWindow(ComplaintActivity.this, viewModel);
            complaintPopupWindow.showPopupWindow();
        });
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
