package com.ibd.dipper.ui.online;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.ibd.dipper.bean.BeanAboutUs;
import com.ibd.dipper.bean.BeanOrders;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.onlineFeedback.OnlineFeedBackActivity;
import com.ibd.dipper.ui.orders.OrdersItemViewModel;
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

public class OnlineViewModel extends BaseViewModel {
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //客服电话弹窗
        public SingleLiveEvent<String> phonePop = new SingleLiveEvent<>();
        //电话权限检测
        public SingleLiveEvent<String> phonePermissions = new SingleLiveEvent<>();
    }


    public OnlineViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 客服电话
     */
    public View.OnClickListener phoneClick = v -> serviceNumber();

    /**
     * 拨打电话权限
     */
    public void phonePermissions(String str) {
        uc.phonePermissions.setValue(str);
    }

    /**
     * 在线反馈按钮
     */
    public View.OnClickListener onlineFeedback = v -> startActivity(OnlineFeedBackActivity.class);

    /**
     * 获取客服电话
     */
    @SuppressLint("CheckResult")
    public void serviceNumber() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .serviceNumber(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanAboutUs>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        uc.phonePop.setValue(response.data.number);
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
