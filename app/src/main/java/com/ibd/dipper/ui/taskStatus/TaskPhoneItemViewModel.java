package com.ibd.dipper.ui.taskStatus;

import android.view.View;

import androidx.databinding.ObservableField;

import io.reactivex.annotations.NonNull;
import me.goldze.mvvmhabit.base.ItemViewModel;

public class TaskPhoneItemViewModel extends ItemViewModel<TaskStatusViewModel> {
    public ObservableField<String> entity = new ObservableField<>();

    public TaskPhoneItemViewModel(@NonNull TaskStatusViewModel viewModel, String bean) {
        super(viewModel);
        this.entity.set(bean);
    }

    public View.OnClickListener click = v -> {
        if (entity.get().equals("+"))
            viewModel.addPhone();
    };

    public View.OnClickListener del = v -> viewModel.del(entity.get());
}
