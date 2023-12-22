package com.ibd.dipper.ui.bank;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.view.View;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanBank;
import com.ibd.dipper.bean.BeanBill;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.bankNew.BankNewActivity;
import com.ibd.dipper.ui.bill.BillItemViewModel;
import com.ibd.dipper.ui.setting.account.AccountViewModel;
import com.ibd.dipper.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

public class BankViewModel extends BaseViewModel {
    public MutableLiveData<BeanBank> beanBank = new MutableLiveData<>();
    //给RecyclerView添加items
    public final ObservableList<BankItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<BankItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_bank);

    public BankViewModel(@NonNull Application application) {
        super(application);
    }

    public UIChangeObservable uc = new UIChangeObservable();
    public class UIChangeObservable {
        public SingleLiveEvent<String> unbindCard = new SingleLiveEvent<>();
        public SingleLiveEvent<BankItemViewModel> bankItemViewModel = new SingleLiveEvent<>();
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
                        observableList.clear();
                        for (BeanBank.Items orders : response.data.items) {
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
     * 添加银行卡
     */
    public View.OnClickListener bankNewClick = v -> startActivity(BankNewActivity.class);

    /**
     * 解绑银行卡
     */
    @SuppressLint("CheckResult")
    public void unbindCard(BankItemViewModel bankItemViewModel) {
        uc.bankItemViewModel.setValue(bankItemViewModel);
        uc.unbindCard.call();

    }

    public void unBind() {
        Map<String, String> params = new HashMap<>();
        params.put("bankAcc", uc.bankItemViewModel.getValue().entity.get().bankAcc);
        RetrofitClient.getInstance().create(ApiService.class)
                .unbindCard(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        observableList.remove(uc.bankItemViewModel.getValue());
                        ToastUtils.showLong("解绑成功");
                        this.setData();
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
