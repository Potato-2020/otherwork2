package com.ibd.dipper.ui.withdraw;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanBank;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class BankItemViewModel extends ItemViewModel<WithdrawViewModel> {
    public ObservableField<BeanBank.Items> entity = new ObservableField<>();
    public SingleLiveEvent<String> text = new SingleLiveEvent<>();

    public BankItemViewModel(@NonNull WithdrawViewModel viewModel, BeanBank.Items bean) {
        super(viewModel);
        this.entity.set(bean);

        text.setValue("收款账户：" + entity.get().bankName + "（尾号：" + entity.get().bankAcc.substring(entity.get().bankAcc.length() - 4) + "）");
    }

    public View.OnClickListener bankClick = v -> viewModel.bankChoose(BankItemViewModel.this);
}
