package com.ibd.dipper.ui.taskResult;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.bean.BeanTaskDetail;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.taskStatus.TaskStatusActivity;
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

public class TaskResultViewModel extends BaseViewModel {
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();//0已接受1已完结
    public MutableLiveData<BeanTaskDetail> beanTaskDetail = new MutableLiveData<>();
    public MutableLiveData<BeanOrdersDetail> beanOrders = new MutableLiveData<>();
    public SingleLiveEvent<Boolean> rcv0show = new SingleLiveEvent<>();
    public SingleLiveEvent<String> ctime0 = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> rcv1show = new SingleLiveEvent<>();
    public SingleLiveEvent<String> ctime1 = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> rcv2show = new SingleLiveEvent<>();
    public SingleLiveEvent<String> ctime2 = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> rcv3show = new SingleLiveEvent<>();
    public SingleLiveEvent<String> ctime3 = new SingleLiveEvent<>();
    public SingleLiveEvent<String> strReason = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> reasonShow = new SingleLiveEvent<>();

    public SingleLiveEvent<String> tvStr0 = new SingleLiveEvent<>();
    public SingleLiveEvent<String> tvStr1 = new SingleLiveEvent<>();
    public SingleLiveEvent<String> tvStr2 = new SingleLiveEvent<>();

    //给RecyclerView添加items
    public final ObservableList<TaskResultItemViewModel> observableList0 = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<TaskResultItemViewModel> itemBinding0 = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_phone_task);

    //给RecyclerView添加items
    public final ObservableList<TaskResultItemViewModel> observableList1 = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<TaskResultItemViewModel> itemBinding1 = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_phone_task);

    //给RecyclerView添加items
    public final ObservableList<TaskResultItemViewModel> observableList2 = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<TaskResultItemViewModel> itemBinding2 = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_phone_task);

    //给RecyclerView添加items
    public final ObservableList<TaskResultItemViewModel> observableList3 = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<TaskResultItemViewModel> itemBinding3 = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_phone_task);

    public MutableLiveData<Boolean> isresign = new MutableLiveData<>();

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //done成功
        public SingleLiveEvent doneSuccess = new SingleLiveEvent();
    }

    public TaskResultViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 已接受任务
     */
    @SuppressLint("CheckResult")
    public void taskDetail(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        RetrofitClient.getInstance().create(ApiService.class)
                .taskDetail(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanTaskDetail>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        if(response.data.tmsOrderId.isEmpty()){
                            response.data.tmsOrderId=null;
                        }
                        beanTaskDetail.setValue(response.data);
                        if (!TextUtils.isEmpty(response.data.reason)) {
                            switch (response.data.checkStatus) {
                                case 0:
                                    reasonShow.setValue(false);
                                    strReason.setValue("等待确认" + (TextUtils.isEmpty(response.data.reason) ? "" : ":") + (TextUtils.isEmpty(response.data.reason) ? "" : response.data.reason));
                                    break;
                                case 1:
                                    reasonShow.setValue(false);
                                    strReason.setValue("货主确认" + (TextUtils.isEmpty(response.data.reason) ? "" : ":") + (TextUtils.isEmpty(response.data.reason) ? "" : response.data.reason));
                                    break;
                                case 2:
                                case 4:
                                    reasonShow.setValue(true);
                                    strReason.setValue(response.data.checkStatusText + ":" + response.data.reason);
                                    break;
                                case 3:
                                    reasonShow.setValue(false);
                                    strReason.setValue("平台确认" + (TextUtils.isEmpty(response.data.reason) ? "" : ":") + (TextUtils.isEmpty(response.data.reason) ? "" : response.data.reason));
                                    break;
                            }
                        }

                        if (response.data.nodes != null) {
                            if (response.data.nodes.size() >= 1) {
                                rcv0show.setValue(response.data.nodes.get(0).photos.size() != 0);
                                ctime0.setValue(TextUtils.isEmpty(response.data.nodes.get(0).ctime) ? "" : response.data.nodes.get(0).ctime);
                            }
                            if (response.data.nodes.size() >= 2) {
                                rcv1show.setValue(response.data.nodes.get(1).photos.size() != 0);
                                ctime1.setValue(TextUtils.isEmpty(response.data.nodes.get(1).ctime) ? "" : response.data.nodes.get(1).ctime);
                                tvStr0.setValue("装车数量：" + response.data.nodes.get(1).quantity + (response.data.unit.equals("2") ? "车" : "吨"));
                            }
                            if (response.data.nodes.size() >= 3) {
                                rcv2show.setValue(response.data.nodes.get(2).photos.size() != 0);
                                ctime2.setValue(TextUtils.isEmpty(response.data.nodes.get(2).ctime) ? "" : response.data.nodes.get(2).ctime);
                            }
                            if (response.data.nodes.size() >= 4) {
                                rcv3show.setValue(response.data.nodes.get(3).photos.size() != 0);
                                ctime3.setValue(TextUtils.isEmpty(response.data.nodes.get(3).ctime) ? "" : response.data.nodes.get(3).ctime);
                                tvStr1.setValue("卸车数量：" + response.data.nodes.get(3).quantity + (response.data.unit.equals("2") ? "车" : "吨"));
                                tvStr2.setValue("签收单号：" + response.data.nodes.get(3).receiptNo);
                            }

                            if (response.data.nodes.get(0).photos.size() != 0) {
                                Iterator it = observableList0.iterator();
                                while (it.hasNext()) {
                                    it.next();
                                    it.remove();
                                }
                                for (String str : response.data.nodes.get(0).photos) {
                                    TaskResultItemViewModel mainItemViewModel = new TaskResultItemViewModel(this, str);
                                    observableList0.add(mainItemViewModel);
                                }
                            }
                            if (response.data.nodes.get(1).photos.size() != 0) {
                                Iterator it = observableList1.iterator();
                                while (it.hasNext()) {
                                    it.next();
                                    it.remove();
                                }
                                for (String str : response.data.nodes.get(1).photos) {
                                    TaskResultItemViewModel mainItemViewModel = new TaskResultItemViewModel(this, str);
                                    observableList1.add(mainItemViewModel);
                                }
                            }
                            if (response.data.nodes.get(2).photos.size() != 0) {
                                Iterator it = observableList2.iterator();
                                while (it.hasNext()) {
                                    it.next();
                                    it.remove();
                                }
                                for (String str : response.data.nodes.get(2).photos) {
                                    TaskResultItemViewModel mainItemViewModel = new TaskResultItemViewModel(this, str);
                                    observableList2.add(mainItemViewModel);
                                }
                            }
                            if (response.data.nodes.get(3).photos.size() != 0) {
                                Iterator it = observableList3.iterator();
                                while (it.hasNext()) {
                                    it.next();
                                    it.remove();
                                }
                                for (String str : response.data.nodes.get(3).photos) {
                                    TaskResultItemViewModel mainItemViewModel = new TaskResultItemViewModel(this, str);
                                    observableList3.add(mainItemViewModel);
                                }
                            }
                        }
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    public View.OnClickListener editClick = v -> {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", beanOrders.getValue());
        bundle.putInt("type", 0);
        switch (v.getId()) {
            case R.id.btn71:
                bundle.putInt("status", 2);
                bundle.putSerializable("nodes", beanTaskDetail.getValue().nodes.get(0));
                break;
            case R.id.btn72:
                bundle.putInt("status", 3);
                bundle.putSerializable("nodes", beanTaskDetail.getValue().nodes.get(1));
                break;
            case R.id.btn73:
                bundle.putInt("status", 4);
                bundle.putSerializable("nodes", beanTaskDetail.getValue().nodes.get(2));
                break;
            case R.id.btn74:
                bundle.putInt("status", 5);
                bundle.putSerializable("nodes", beanTaskDetail.getValue().nodes.get(3));
                break;
        }
        startActivity(TaskStatusActivity.class, bundle);
    };

    public View.OnClickListener isresignClick = v -> reSign(beanTaskDetail.getValue().id);

    /**
     * 重新签收
     */
    @SuppressLint("CheckResult")
    public void reSign(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        RetrofitClient.getInstance().create(ApiService.class)
                .reSign(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        uc.doneSuccess.call();
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
