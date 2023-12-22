package com.ibd.dipper.ui.bill;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanBill;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.billUnpaid.BillUnpaidActivity;
import com.ibd.dipper.ui.login.LoginActivity;
import com.ibd.dipper.ui.msg.MsgActivity;
import com.ibd.dipper.ui.taskResult.TaskResultActivity;
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

public class BillViewModel extends BaseViewModel {
    public SingleLiveEvent<Integer> page = new SingleLiveEvent<>();
    public MutableLiveData<BeanBill> beanBill = new MutableLiveData<>();

    //给RecyclerView添加items
    public final ObservableList<BillItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<BillItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_bill);

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

    public BillViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 账单
     */
    @SuppressLint("CheckResult")
    public void billList() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .billList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanBill>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanBill.setValue(response.data);
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

                        for (BeanBill.Items orders : response.data.items) {
                            BillItemViewModel mainItemViewModel = new BillItemViewModel(this, orders);
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
     * 消息按钮
     */
    public View.OnClickListener msgClick = v -> startActivity(MsgActivity.class);

    /**
     * 未付订单详情
     */
    public View.OnClickListener unpaidBillClick = v -> startActivity(BillUnpaidActivity.class);

    /**
     * 运单详情
     */
    public void waybillDetail(BillItemViewModel billItemViewModel) {
        Bundle bundle = new Bundle();
        bundle.putString("id", billItemViewModel.entity.get().id);
        bundle.putString("orderId", billItemViewModel.entity.get().id);
        bundle.putInt("type", 0);
        startActivity(TaskResultActivity.class, bundle);
    }

}
