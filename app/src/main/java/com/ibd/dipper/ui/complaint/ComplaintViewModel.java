package com.ibd.dipper.ui.complaint;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanComplaint;
import com.ibd.dipper.bean.BeanOrders;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.orders.OrdersItemViewModel;
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

public class ComplaintViewModel extends BaseViewModel {
    public SingleLiveEvent<Integer> page = new SingleLiveEvent<>();
    public List<String> list = new ArrayList<>();
    public MutableLiveData<List<BeanComplaint>> beanMsg = new MutableLiveData<>();
    //给RecyclerView添加items
    public final ObservableList<ComplaintItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<ComplaintItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_complaint);

    public MutableLiveData<ComplaintItemViewModel> complaintItemViewModel = new MutableLiveData<>();

    public MutableLiveData<Integer> type = new MutableLiveData<>();

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

    public ComplaintViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData() {
        if (type.getValue() == 0)
            complaintList();
        else
            beingComplainedList();
    }

    /**
     * 我的投诉列表
     */
    @SuppressLint("CheckResult")
    public void complaintList() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .complaintList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanComplaint>>) response -> {
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

                        for (BeanComplaint.Items orders : response.data.items) {
                            ComplaintItemViewModel mainItemViewModel = new ComplaintItemViewModel(this, orders, type.getValue());
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
     * 投诉我的列表
     */
    @SuppressLint("CheckResult")
    public void beingComplainedList() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .beingComplainedList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanComplaint>>) response -> {
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

                        for (BeanComplaint.Items orders : response.data.items) {
                            ComplaintItemViewModel mainItemViewModel = new ComplaintItemViewModel(this, orders, type.getValue());
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
     * 我的投诉按钮
     */
    public View.OnClickListener myComplaintClick = v -> {
        type.setValue(0);
        page.setValue(1);
        setData();
    };

    /**
     * 投诉我的按钮
     */
    public View.OnClickListener complaintMyClick = v -> {
        type.setValue(1);
        page.setValue(1);
        setData();
    };

    /**
     * 申诉按钮
     */
    public void appealPop(ComplaintItemViewModel c) {
        this.complaintItemViewModel.setValue(c);
    }

    /**
     * 提交申诉
     */
    @SuppressLint("CheckResult")
    public void appealCommit(String str) {
        Map<String, String> params = new HashMap<>();
        params.put("id", complaintItemViewModel.getValue().entity.get().id + "");
        params.put("content", str);
        RetrofitClient.getInstance().create(ApiService.class)
                .complained(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanComplaint>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        page.setValue(1);
                        setData();
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
