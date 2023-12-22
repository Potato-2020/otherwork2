package com.ibd.dipper.ui.bank;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanBank;

import me.goldze.mvvmhabit.base.ItemViewModel;

public class BankItemViewModel extends ItemViewModel<BankViewModel> {
    public ObservableField<BeanBank.Items> entity = new ObservableField<>();

    public BankItemViewModel(@NonNull BankViewModel viewModel, BeanBank.Items beanBank) {
        super(viewModel);
        this.entity.set(beanBank);
    }

    public View.OnClickListener unbindCardClick = v -> viewModel.unbindCard(BankItemViewModel.this);
}
