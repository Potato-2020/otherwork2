package com.ibd.dipper.ui.taskResult;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import me.goldze.mvvmhabit.base.ItemViewModel;

public class TaskResultItemViewModel extends ItemViewModel<TaskResultViewModel> {
    public ObservableField<String> entity = new ObservableField<>();

    public TaskResultItemViewModel(@NonNull TaskResultViewModel viewModel, String str) {
        super(viewModel);
        this.entity.set(str);
    }
}
