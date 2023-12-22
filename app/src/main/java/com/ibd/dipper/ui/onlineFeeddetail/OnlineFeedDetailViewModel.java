package com.ibd.dipper.ui.onlineFeeddetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOnline;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class OnlineFeedDetailViewModel extends BaseViewModel {
    public MutableLiveData<BeanOnline.Items> data = new MutableLiveData<>();
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> face = new MutableLiveData<>();

    //给RecyclerView添加items
    public final ObservableList<OnlinePhoneItemViewModel> observableList1 = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<OnlinePhoneItemViewModel> itemBinding1 = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_phone_online_detail);

    public OnlineFeedDetailViewModel(@NonNull Application application) {
        super(application);
    }
}
