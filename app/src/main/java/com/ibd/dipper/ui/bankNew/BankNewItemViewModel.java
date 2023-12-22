package com.ibd.dipper.ui.bankNew;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanBankList;

import me.goldze.mvvmhabit.base.ItemViewModel;

public class BankNewItemViewModel extends ItemViewModel<BankNewViewModel> {
    public ObservableField<BeanBankList> entity = new ObservableField<>();

    public BankNewItemViewModel(@NonNull BankNewViewModel viewModel,BeanBankList beanBankList) {
        super(viewModel);
        this.entity.set(beanBankList);
    }

    public View.OnClickListener parentClick = v -> viewModel.ChooseBank(BankNewItemViewModel.this);
}
