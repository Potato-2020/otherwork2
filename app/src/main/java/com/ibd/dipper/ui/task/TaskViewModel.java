package com.ibd.dipper.ui.task;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanEvaluateDetail;
import com.ibd.dipper.bean.BeanOrders;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.bean.BeanTask;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.msg.MsgActivity;
import com.ibd.dipper.ui.msg.SystemTurnEvent;
import com.ibd.dipper.ui.taskResult.TaskResultActivity;
import com.ibd.dipper.ui.taskStatus.TaskStatusActivity;
import com.ibd.dipper.uiPopupWindow.TaskEvaluateOrComplaintPopupWindow;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class TaskViewModel extends BaseViewModel {
    public MutableLiveData<Integer> type = new MutableLiveData<>();//0已接受1已完结

    private Disposable mSubscription;

    public SingleLiveEvent<String> keyword = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> page = new SingleLiveEvent<>();
    public MutableLiveData<String> longStr = new MutableLiveData<>();
    public MutableLiveData<String> latStr = new MutableLiveData<>();

    public MutableLiveData<List<BeanTask>> beanConsign = new MutableLiveData<>();
    //给RecyclerView添加items
    public final ObservableList<TaskItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<TaskItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_task);

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadmore = new SingleLiveEvent<>();
        //没有更多
        public SingleLiveEvent finishNoMore = new SingleLiveEvent();
        //清除搜索
        public SingleLiveEvent delKeyword = new SingleLiveEvent();
        //任务进度页
        public SingleLiveEvent<Bundle> toTaskStatusAc = new SingleLiveEvent<>();
        //重新确认二次弹窗
        public SingleLiveEvent<TaskItemViewModel> toResignPop = new SingleLiveEvent<>();
    }

    public TaskViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData() {
        if (type.getValue() == 0) {
            taskList();
        } else {
            endList();
        }
    }

    /**
     * 清除搜索
     */
    public View.OnClickListener delClick = v -> uc.delKeyword.call();

    /**
     * 已接受任务
     */
    @SuppressLint("CheckResult")
    public void taskList() {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", keyword.getValue() == null ? "" : keyword.getValue());
        params.put("long", longStr.getValue());
        params.put("lat", latStr.getValue());
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .taskList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanOrders>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    dismissDialog();
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

                        for (BeanOrdersDetail orders : response.data.items) {
                            TaskItemViewModel mainItemViewModel = new TaskItemViewModel(this, orders, type.getValue());
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
     * 完成接受任务
     */
    @SuppressLint("CheckResult")
    public void endList() {
        Map<String, String> params = new HashMap<>();
        params.put("keyword", keyword.getValue() == null ? "" : keyword.getValue());
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .endList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanOrders>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    dismissDialog();
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

                        for (BeanOrdersDetail orders : response.data.items) {
                            TaskItemViewModel mainItemViewModel = new TaskItemViewModel(this, orders, type.getValue());
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
     * 搜索关键字监听
     */
    public TextWatcher keyWordListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            keyword.setValue(s.toString());
            if (s.toString().isEmpty())
                setData();
        }
    };

    /**
     * 搜索按钮
     */
    public TextView.OnEditorActionListener keyActionListener = (v, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            showDialog();
            setData();
        }
        return false;
    };

    /**
     * 消息按钮
     */
    public View.OnClickListener msgClick = v -> startActivity(MsgActivity.class);

    /**
     * 已接受按钮
     */
    public BindingCommand acceptClick = new BindingCommand(() -> {
        type.setValue(0);
        page.setValue(1);
        showDialog();
        setData();
    });

    /**
     * 已完结按钮
     */
    public BindingCommand endClick = new BindingCommand(() -> {
        type.setValue(1);
        page.setValue(1);
        showDialog();
        setData();
    });

    /**
     * 任务进度按钮
     */
    public void taskDetail(TaskItemViewModel taskItemViewModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", taskItemViewModel.entity.get());
        bundle.putInt("type", 0);
        bundle.putInt("status", taskItemViewModel.entity.get().status);
        uc.toTaskStatusAc.setValue(bundle);
    }

    /**
     * 运单详情
     */
    public void waybillDetail(TaskItemViewModel taskItemViewModel) {
        Bundle bundle = new Bundle();
        bundle.putString("id", taskItemViewModel.entity.get().id);
        bundle.putInt("type", type.getValue());
        bundle.putSerializable("data", taskItemViewModel.entity.get());
        startActivity(TaskResultActivity.class, bundle);
    }

    /**
     * 未完成订单
     * 重新签收
     */
    public void reSign(TaskItemViewModel taskItemViewModel) {
        uc.toResignPop.setValue(taskItemViewModel);
    }

    /**
     * 订单结果
     * 评价判断是否有
     */
    @SuppressLint("CheckResult")
    public void evaluate(TaskItemViewModel taskItemViewModel) {
        Map<String, String> params = new HashMap<>();
        params.put("id", taskItemViewModel.entity.get().id);
        RetrofitClient.getInstance().create(ApiService.class)
                .evaluateDetail(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanEvaluateDetail>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        TaskEvaluateOrComplaintPopupWindow taskEvaluateOrComplaintPopupWindow = new TaskEvaluateOrComplaintPopupWindow(UIUtils.getContext(), 0, taskItemViewModel.entity.get().id, false, response.data.speed, response.data.settlement, response.data.standard);
                        taskEvaluateOrComplaintPopupWindow.showPopupWindow();
                    } else {
                        TaskEvaluateOrComplaintPopupWindow taskEvaluateOrComplaintPopupWindow = new TaskEvaluateOrComplaintPopupWindow(UIUtils.getContext(), 0, taskItemViewModel.entity.get().id, true);
                        taskEvaluateOrComplaintPopupWindow.showPopupWindow();
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 订单结果
     * 投诉
     */
    public void complaint(TaskItemViewModel taskItemViewModel) {
        TaskEvaluateOrComplaintPopupWindow taskEvaluateOrComplaintPopupWindow = new TaskEvaluateOrComplaintPopupWindow(UIUtils.getContext(), 1, taskItemViewModel.entity.get().id, true);
        taskEvaluateOrComplaintPopupWindow.showPopupWindow();
    }

    //注册RxBus
    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(SystemTurnEvent.class).subscribe(s -> {
            type.setValue(1);
            setData();
        });
        RxSubscriptions.add(mSubscription);
    }

    //移除RxBus
    @Override
    public void removeRxBus() {
        super.removeRxBus();
        //将订阅者从管理站中移除
        RxSubscriptions.remove(mSubscription);
    }
}
