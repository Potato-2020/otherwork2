package com.ibd.dipper.ui.bill;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanBill;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class BillItemViewModel extends ItemViewModel<BillViewModel> {
    public ObservableField<BeanBill.Items> entity = new ObservableField<>();

    public BillItemViewModel(@NonNull BillViewModel viewModel, BeanBill.Items bean) {
        super(viewModel);
        this.entity.set(bean);
    }

    public View.OnClickListener waybillDetailClick = v -> viewModel.waybillDetail(BillItemViewModel.this);
}
