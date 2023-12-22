package com.ibd.dipper.ui.billUnpaid;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanBill;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class BillUnpaidItemViewModel extends ItemViewModel<BillUnpaidViewModel> {
    public ObservableField<BeanBill.Items> entity = new ObservableField<>();
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();

    public BillUnpaidItemViewModel(@NonNull BillUnpaidViewModel viewModel, BeanBill.Items bean, int type) {
        super(viewModel);
        this.entity.set(bean);
        this.type.setValue(type);
    }

}
