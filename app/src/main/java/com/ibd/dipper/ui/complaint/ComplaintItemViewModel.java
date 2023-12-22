package com.ibd.dipper.ui.complaint;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.bean.BeanComplaint;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class ComplaintItemViewModel extends ItemViewModel<ComplaintViewModel> {
    public ObservableField<BeanComplaint.Items> entity = new ObservableField<>();
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();

    public ComplaintItemViewModel(@NonNull ComplaintViewModel viewModel, BeanComplaint.Items beanComplaint, int type) {
        super(viewModel);
        this.entity.set(beanComplaint);
        this.type.setValue(type);
    }

    public View.OnClickListener appealClick = v -> viewModel.appealPop(this);
}
