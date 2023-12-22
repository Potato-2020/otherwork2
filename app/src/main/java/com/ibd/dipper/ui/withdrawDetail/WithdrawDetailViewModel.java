package com.ibd.dipper.ui.withdrawDetail;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanBank;
import com.ibd.dipper.bean.BeanWithdrawDetail;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.bank.BankItemViewModel;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.TimeUtils;

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

public class WithdrawDetailViewModel extends BaseViewModel {
    public MutableLiveData<Integer> page = new MutableLiveData<>();

    //给RecyclerView添加items
    public final ObservableList<WithdrawDetailItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<WithdrawDetailItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_withdraw_detail);

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadmore = new SingleLiveEvent<>();
        //没有更多
        public SingleLiveEvent finishNoMore = new SingleLiveEvent();
    }

    public WithdrawDetailViewModel(@NonNull Application application) {
        super(application);
    }

    @SuppressLint("CheckResult")
    public void setData() {
        showDialog();
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page.getValue()));
        params.put("size", "20");
//        params.put("start", "2021-01-01");
//        params.put("end", "2021-04-13");
        RetrofitClient.getInstance().create(ApiService.class)
                .fundsDetailQuery(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanWithdrawDetail>>) response -> {
                    dismissDialog();
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        if (page.getValue() == 1) {
                            Iterator it = observableList.iterator();
                            while (it.hasNext()) {
                                it.next();
                                it.remove();
                            }
                            uc.finishRefreshing.call();
                        } else {
                            if (response.data.pages < page.getValue()) {
                                page.setValue(page.getValue() - 1);
                                uc.finishNoMore.call();
                                return;
                            }
                            uc.finishLoadmore.call();
                        }

                        for (BeanWithdrawDetail.Items orders : response.data.items) {
                            WithdrawDetailItemViewModel mainItemViewModel = new WithdrawDetailItemViewModel(this, orders);
                            observableList.add(mainItemViewModel);
                        }
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
