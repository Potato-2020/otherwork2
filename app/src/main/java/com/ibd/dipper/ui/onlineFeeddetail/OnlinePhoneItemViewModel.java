package com.ibd.dipper.ui.onlineFeeddetail;

import androidx.databinding.ObservableField;

import io.reactivex.annotations.NonNull;
import me.goldze.mvvmhabit.base.ItemViewModel;

public class OnlinePhoneItemViewModel extends ItemViewModel<OnlineFeedDetailViewModel> {
    public ObservableField<String> entity = new ObservableField<>();

    public OnlinePhoneItemViewModel(@NonNull OnlineFeedDetailViewModel viewModel, String bean) {
        super(viewModel);
        this.entity.set(bean);
    }
}
