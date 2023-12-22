package com.ibd.dipper.ui.carApproveList;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanCar;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class CarApproveItemViewModel extends ItemViewModel<CarApproveListViewModel> {
    public ObservableField<BeanCar> entity = new ObservableField<>();
    public SingleLiveEvent<String> certificationStatusText = new SingleLiveEvent<>();

    public CarApproveItemViewModel(@NonNull CarApproveListViewModel viewModel, BeanCar beanCar) {
        super(viewModel);
        this.entity.set(beanCar);

        switch (beanCar.certificationStatus) {
            case 0:
                certificationStatusText.setValue("认证中");
                break;
            case 1:
                certificationStatusText.setValue("已认证");
                break;
            case -1:
                certificationStatusText.setValue("已拒绝");
                break;
        }
    }

    public View.OnClickListener toDetail = v -> viewModel.uc.toCarApproveActivity2.setValue(entity.get());
}
