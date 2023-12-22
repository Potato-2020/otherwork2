package com.ibd.dipper.ui.bill;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.FragmentBillBinding;
import com.ibd.dipper.ui.orders.OrdersFragment;
import com.ibd.dipper.uiPopupWindow.CarPopupWindow;
import com.ibd.dipper.utils.UIUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import me.goldze.mvvmhabit.base.BaseFragment;

public class BillFragment extends BaseFragment<FragmentBillBinding, BillViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_bill;
    }

    @Override
    public int initVariableId() {
        return com.ibd.dipper.BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        viewModel.page.setValue(1);

        viewModel.billList();

        binding.refreshLayout.autoLoadMore();
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            viewModel.page.setValue(1);
            viewModel.billList();
        });
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            viewModel.page.setValue((viewModel.page.getValue() + 1));
            viewModel.billList();
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
    }
}
