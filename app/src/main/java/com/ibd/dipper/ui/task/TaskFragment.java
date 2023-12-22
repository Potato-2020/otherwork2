package com.ibd.dipper.ui.task;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.databinding.FragmentTaskBinding;
import com.ibd.dipper.ui.taskResult.TaskResultActivity;
import com.ibd.dipper.ui.taskStatus.TaskStatusActivity;
import com.ibd.dipper.utils.LocationUtils;
import com.ibd.dipper.utils.UIUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.KLog;

public class TaskFragment extends BaseFragment<FragmentTaskBinding, TaskViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_task;
    }

    @Override
    public int initVariableId() {
        return com.ibd.dipper.BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        Location location = LocationUtils.getLastKnownLocation(getActivity());
        viewModel.longStr.setValue(location != null ? (location.getLongitude() + "") : "111.111111");
        viewModel.latStr.setValue(location != null ? (location.getLatitude() + "") : "111.111111");
        viewModel.page.setValue(1);
        viewModel.type.setValue(0);

        binding.etKey.addTextChangedListener(viewModel.keyWordListener);
        binding.etKey.setOnEditorActionListener(viewModel.keyActionListener);
        viewModel.setData();

        binding.refreshLayout.autoLoadMore();
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
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
        viewModel.uc.delKeyword.observe(this, o -> binding.etKey.setText(""));
        viewModel.uc.toTaskStatusAc.observe(this, bundle -> {
            Intent intent = new Intent(getActivity(), TaskStatusActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 300);
        });
        viewModel.uc.toResignPop.observe(this, taskItemViewModel -> {
            Bundle bundle = new Bundle();
            bundle.putString("id", taskItemViewModel.entity.get().id);
            bundle.putInt("type", viewModel.type.getValue());
            bundle.putSerializable("data", taskItemViewModel.entity.get());
            bundle.putBoolean("isresign", true);
            Intent intent = new Intent(getActivity(), TaskResultActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, 300);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300 & resultCode == 301)
            viewModel.setData();
    }
}
