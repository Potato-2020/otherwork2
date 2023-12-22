package com.ibd.dipper.ui.onlineFeedback;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanOnline;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class OnlineItemViewModel extends ItemViewModel<OnlineFeedBackViewModel> {
    public ObservableField<BeanOnline.Items> entity = new ObservableField<>();
    public SingleLiveEvent<String> photo = new SingleLiveEvent<>();

    public OnlineItemViewModel(@NonNull OnlineFeedBackViewModel viewModel, BeanOnline.Items beanOnline) {
        super(viewModel);
        this.entity.set(beanOnline);

        if (!entity.get().photos.isEmpty())
            photo.setValue(entity.get().photos.get(0));
    }

    public View.OnClickListener parentClick = v -> viewModel.toDetail(OnlineItemViewModel.this);
}
