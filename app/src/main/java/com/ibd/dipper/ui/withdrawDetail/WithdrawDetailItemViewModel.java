package com.ibd.dipper.ui.withdrawDetail;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanWithdrawDetail;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.StringUtils;

public class WithdrawDetailItemViewModel extends ItemViewModel<WithdrawDetailViewModel> {
    public ObservableField<BeanWithdrawDetail.Items> entity = new ObservableField<>();
    public SingleLiveEvent<String> money = new SingleLiveEvent<>();
    public SingleLiveEvent<String> text = new SingleLiveEvent<>();

    public WithdrawDetailItemViewModel(@NonNull WithdrawDetailViewModel viewModel, BeanWithdrawDetail.Items beanWithdrawDetail) {
        super(viewModel);
        this.entity.set(beanWithdrawDetail);

        if (entity.get().chgFlag == 0) {
            money.setValue("+￥" + entity.get().amt);
        } else if (entity.get().chgFlag == 1) {
            money.setValue("-￥" + entity.get().amt);
        } else {
            money.setValue(entity.get().amt);
        }

        if (!StringUtils.isEmpty(entity.get().BnakAccount))
            text.setValue("收款账户：" + entity.get().otherAccName + "（尾号：" + entity.get().otherAcc.substring(entity.get().otherAcc.length() - 4) + "）");
        else
            text.setValue("收款账户：" + entity.get().otherAccName);
    }
}
