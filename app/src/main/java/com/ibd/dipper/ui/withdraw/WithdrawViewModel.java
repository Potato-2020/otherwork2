package com.ibd.dipper.ui.withdraw;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanBank;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.utils.JsonUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class WithdrawViewModel extends BaseViewModel {
    public SingleLiveEvent<String> balance = new SingleLiveEvent<>();

    public MutableLiveData<String> amount = new MutableLiveData<>();
    public MutableLiveData<String> bankAcc = new MutableLiveData<>();

    public MutableLiveData<BeanBank> beanBank = new MutableLiveData<>();
    //给RecyclerView添加items
    public final ObservableList<BankItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<BankItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_withdraw_bank);

    public WithdrawViewModel(@NonNull Application application) {
        super(application);
    }

    @SuppressLint("CheckResult")
    public void setData() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .cardBindList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanBank>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanBank.setValue(response.data);
                        for (BeanBank.Items orders : response.data.items) {
                            orders.choosed = false;
                            BankItemViewModel mainItemViewModel = new BankItemViewModel(this, orders);
                            observableList.add(mainItemViewModel);
                        }
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 选择银行卡
     * @param bankItemViewModel
     */
    public void bankChoose(BankItemViewModel bankItemViewModel) {
        Iterator it = observableList.iterator();
        while (it.hasNext()) {
            BankItemViewModel bankItemViewModels = (BankItemViewModel) it.next();
            bankItemViewModels.entity.get().choosed = false;
            bankItemViewModels.entity.set(bankItemViewModels.entity.get());
            bankItemViewModels.entity.notifyChange();
        }
        bankItemViewModel.entity.get().choosed = true;
        bankItemViewModel.entity.set(bankItemViewModel.entity.get());
        bankItemViewModel.entity.notifyChange();
        bankAcc.setValue(bankItemViewModel.entity.get().bankAcc);
    }

    /**
     * 提现按钮
     */
    public View.OnClickListener cashoutClick = v -> {
        if (TextUtils.isEmpty(amount.getValue()))
            return;
        if (TextUtils.isEmpty(bankAcc.getValue()))
            return;
        showDialog();
        cashout();
    };

    /**
     * 提现接口s
     */
    @SuppressLint("CheckResult")
    public void cashout() {
        Map<String, String> params = new HashMap<>();
        params.put("amount",amount.getValue());
        params.put("bankAcc",bankAcc.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .cashout(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanBank>>) response -> {
                    dismissDialog();
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        finish();
                        ToastUtils.showLong("提现成功");
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
