package com.ibd.dipper.ui.msgDetail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityMsgDetailBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import me.goldze.mvvmhabit.base.BaseActivity;

public class MsgDetailActivity extends BaseActivity<ActivityMsgDetailBinding, MsgDetailViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_msg_detail;
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

        viewModel.type.setValue(getIntent().getIntExtra("type", 0));
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
        viewModel.uc.toComplaintAc.observe(this, o -> {
            setResult(200);
            finish();
        });
        viewModel.uc.toSystemAc.observe(this, integer -> {
            switch (integer){
                case 1:
                    setResult(201);
                    break;
                case 2:
                    setResult(202);
                    break;
                case 3:
                    setResult(203);
                    break;
                case 4:
                    setResult(204);
                    break;
            }
            finish();
        });
    }
}
