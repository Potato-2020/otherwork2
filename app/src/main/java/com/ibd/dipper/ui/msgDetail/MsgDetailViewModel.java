package com.ibd.dipper.ui.msgDetail;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.*;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.orders.OrdersItemViewModel;
import com.ibd.dipper.uiPopupWindow.MessageEvaluateOrComplaintPopupWindow;
import com.ibd.dipper.uiPopupWindow.TaskEvaluateOrComplaintPopupWindow;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;

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

public class MsgDetailViewModel extends BaseViewModel {
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> page = new SingleLiveEvent<>();

    //给RecyclerView添加items
    public final ObservableList<MsgDetailItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<MsgDetailItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_msg);

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadmore = new SingleLiveEvent<>();
        //没有更多
        public SingleLiveEvent finishNoMore = new SingleLiveEvent();

        public SingleLiveEvent toComplaintAc = new SingleLiveEvent();

        public SingleLiveEvent<Integer> toSystemAc = new SingleLiveEvent<>();
    }

    public MsgDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData() {
        switch (type.getValue()) {
            case 0:
                notice();
                break;
            case 1:
                evaluate();
                break;
            case 2:
                complaint();
                break;
        }
    }

    /**
     * 系统通知列表
     */
    @SuppressLint("CheckResult")
    public void notice() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .notice(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanMsg>>) response -> {
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

                        for (BeanMsg.Items orders : response.data.items) {
                            MsgDetailItemViewModel mainItemViewModel = new MsgDetailItemViewModel(this, orders, type.getValue());
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
     * 货主评价列表
     */
    @SuppressLint("CheckResult")
    public void evaluate() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .evaluate(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanMsg>>) response -> {
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

                        for (BeanMsg.Items orders : response.data.items) {
                            MsgDetailItemViewModel mainItemViewModel = new MsgDetailItemViewModel(this, orders, type.getValue());
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
     * 投诉通知列表
     */
    @SuppressLint("CheckResult")
    public void complaint() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .complaint(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanMsg>>) response -> {
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

                        for (BeanMsg.Items orders : response.data.items) {
                            MsgDetailItemViewModel mainItemViewModel = new MsgDetailItemViewModel(this, orders, type.getValue());
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
     * 读取消息
     */
    @SuppressLint("CheckResult")
    public void read(MsgDetailItemViewModel msgDetailItemViewModel) {
        Map<String, String> params = new HashMap<>();
        params.put("id", msgDetailItemViewModel.entity.get().id + "");
        RetrofitClient.getInstance().create(ApiService.class)
                .read(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanMsg>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        msgDetailItemViewModel.entity.get().read = 2;
                        msgDetailItemViewModel.entity.set(msgDetailItemViewModel.entity.get());
                        msgDetailItemViewModel.entity.notifyChange();
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 评价详情
     */
    @SuppressLint("CheckResult")
    public void evaluateDetail(MsgDetailItemViewModel msgDetailItemViewModel) {
        Map<String, String> params = new HashMap<>();
        params.put("id", msgDetailItemViewModel.entity.get().id + "");
        RetrofitClient.getInstance().create(ApiService.class)
                .carrierEvaluateDetail(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanCarrierEvaluateDetail>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        MessageEvaluateOrComplaintPopupWindow taskEvaluateOrComplaintPopupWindow = new MessageEvaluateOrComplaintPopupWindow(UIUtils.getContext(), 0, msgDetailItemViewModel.entity.get().id + "", false, response.data.speed, response.data.safety, response.data.standard, response.data.service, response.data.attitude);
                        taskEvaluateOrComplaintPopupWindow.showPopupWindow();
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 投诉详情
     */
    public void complaintDetail() {
        uc.toComplaintAc.call();
    }

    /**
     * 系统通知
     */
    public void systemTurn(MsgDetailItemViewModel msgDetailItemViewModel) {
        uc.toSystemAc.setValue(msgDetailItemViewModel.entity.get().state);
    }
}
