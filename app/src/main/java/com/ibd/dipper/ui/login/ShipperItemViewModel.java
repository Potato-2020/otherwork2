package com.ibd.dipper.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.bean.BeanShipper;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class ShipperItemViewModel extends ItemViewModel<LoginViewModel> {
    public MutableLiveData<BeanShipper.Items> entity = new MutableLiveData<>();
    public SingleLiveEvent<String> position = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> isDouble = new SingleLiveEvent<>();

    public ShipperItemViewModel(@NonNull LoginViewModel viewModel, BeanShipper.Items items, int options) {
        super(viewModel);
        this.entity.setValue(items);
        position.setValue((options + 1) + "");
        isDouble.setValue(((options + 1) & 1) != 0);
    }
}
