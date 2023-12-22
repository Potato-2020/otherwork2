package com.ibd.dipper.ui.ordersDetail;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.orders.OrdersItemViewModel;
import com.ibd.dipper.ui.orders.RefuseEvent;
import com.ibd.dipper.uiPopupWindow.CarPopupWindow;
import com.ibd.dipper.uiPopupWindow.RefusePopupWindow;
import com.ibd.dipper.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class OrdersDetailViewModel extends BaseViewModel {
    public Context context;
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();
    public SingleLiveEvent<BeanOrdersDetail> beanOrdersDetail = new SingleLiveEvent<>();

    public MutableLiveData<Integer> status = new MutableLiveData<>();
    public MutableLiveData<String> statusStr = new MutableLiveData<>();
    public MutableLiveData<Integer> biddingStatus = new MutableLiveData<>();
    public MutableLiveData<String> biddingStatusStr = new MutableLiveData<>();

    public MutableLiveData<String> longStr = new MutableLiveData<>();
    public MutableLiveData<String> latStr = new MutableLiveData<>();

    //订阅者
    private Disposable mSubscription;
    private Disposable mSubscriptionBidding;
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //认证车辆pop
        public SingleLiveEvent carListPop = new SingleLiveEvent();
    }

    public OrdersDetailViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 订单详情
     */
    @SuppressLint("CheckResult")
    public void getDetail(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        RetrofitClient.getInstance().create(ApiService.class)
                .detail(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanOrdersDetail>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanOrdersDetail.setValue(response.data);
                        setData();
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 设置数据
     */
    public void setData() {
        if (type.getValue() == 0) {
            status.setValue(beanOrdersDetail.getValue().status);
            status.observe((LifecycleOwner) context, integer -> {
                switch (integer) {
                    case 0:
                        statusStr.setValue("接单");
                        break;
                    case 1:
                        statusStr.setValue("已拒绝");
                        break;
                }
            });
        } else {
            this.biddingStatus.setValue(beanOrdersDetail.getValue().biddingStatus);
            biddingStatus.observe((LifecycleOwner) context, integer -> {
                switch (integer) {
                    case 1:
                        biddingStatusStr.setValue("抢单");
                        break;
                    case 2:
                        biddingStatusStr.setValue("抢单中");
                        break;
                }
            });
        }
    }

    /**
     * 拒绝
     */
    public View.OnClickListener refuseClick = v -> {
        RefusePopupWindow refusePopupWindow = new RefusePopupWindow(context, true);
        refusePopupWindow.showPopupWindow();
    };

    /**
     * 接受
     */
    public View.OnClickListener ordersClick = v -> {
        if (beanOrdersDetail.getValue().status == 0)
            taking();
    };

    /**
     * 抢单
     */
    public View.OnClickListener grabOrdersClick = v -> {
        if (beanOrdersDetail.getValue().biddingStatus == 1)
            uc.carListPop.call();
    };

    /**
     * 提交拒绝理由接口
     */
    @SuppressLint("CheckResult")
    public void refuseStrSubmit(String str) {
        Map<String, String> params = new HashMap<>();
        params.put("str", str);
        params.put("id", beanOrdersDetail.getValue().id);
        RetrofitClient.getInstance().create(ApiService.class)
                .refuse(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        status.setValue(1);
                        statusStr.setValue("已拒绝");
                        RxBus.getDefault().post(new RefuseSuccessEvent(false, beanOrdersDetail.getValue().id, 1, 0));
                    }
                    ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 接单接口
     */
    @SuppressLint("CheckResult")
    public void taking() {
        Map<String, String> params = new HashMap<>();
        params.put("long", longStr.getValue());
        params.put("lat", latStr.getValue());
        params.put("id", beanOrdersDetail.getValue().id);
        RetrofitClient.getInstance().create(ApiService.class)
                .taking(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        status.setValue(2);
                        statusStr.setValue("待装车");
                        RxBus.getDefault().post(new RefuseSuccessEvent(true, beanOrdersDetail.getValue().id, 2, 0));
                    }
                    ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 抢单接口
     */
    @SuppressLint("CheckResult")
    public void bidding(int vehicleId) {
        Map<String, String> params = new HashMap<>();
        params.put("long", longStr.getValue());
        params.put("lat", latStr.getValue());
        params.put("vehicleId", vehicleId + "");
        params.put("id", beanOrdersDetail.getValue().id);
        RetrofitClient.getInstance().create(ApiService.class)
                .bidding(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        biddingStatusStr.setValue("抢单中");
                        RxBus.getDefault().post(new RefuseSuccessEvent(false, beanOrdersDetail.getValue().id, 0, 2));
                    }
                    ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    //注册RxBus
    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(RefuseDetailEvent.class).subscribe(s -> {
            refuseStrSubmit(s.str);
        });
        mSubscriptionBidding = RxBus.getDefault().toObservable(CarPopupWindow.BiddinOrdersDetailgEvent.class).subscribe(s -> {
            bidding(s.vehicleId);
        });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
        RxSubscriptions.add(mSubscriptionBidding);
    }

    //移除RxBus
    @Override
    public void removeRxBus() {
        super.removeRxBus();
        //将订阅者从管理站中移除
        RxSubscriptions.remove(mSubscription);
        RxSubscriptions.remove(mSubscriptionBidding);
    }
}
