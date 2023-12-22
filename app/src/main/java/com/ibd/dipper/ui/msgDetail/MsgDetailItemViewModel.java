package com.ibd.dipper.ui.msgDetail;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanMsg;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class MsgDetailItemViewModel extends ItemViewModel<MsgDetailViewModel> {
    public ObservableField<BeanMsg.Items> entity = new ObservableField<>();
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();
    public SingleLiveEvent<String> typeText = new SingleLiveEvent<>();

    public MsgDetailItemViewModel(@NonNull MsgDetailViewModel viewModel, BeanMsg.Items bean, int type) {
        super(viewModel);
        this.entity.set(bean);
        this.type.setValue(type);

        typeText.setValue(bean.title);
    }

    public View.OnClickListener readClick = v -> {
        if (entity.get().read == 1)
            viewModel.read(MsgDetailItemViewModel.this);
    };

    public View.OnClickListener doClick = v -> {
        if (entity.get().read == 1)
            viewModel.read(MsgDetailItemViewModel.this);
        switch (type.getValue()) {
            case 0:
                viewModel.systemTurn(MsgDetailItemViewModel.this);
                break;
            case 1:
                viewModel.evaluateDetail(MsgDetailItemViewModel.this); //弹出评价详情页
                break;
            case 2:
                viewModel.complaintDetail();//跳转到投诉我的
                break;
        }
    };
}
