package com.ibd.dipper.ui.setting.about;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;

import com.ibd.dipper.bean.BeanAboutUs;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class AboutViewModel extends BaseViewModel {
    public SingleLiveEvent<BeanAboutUs> beanAboutUs = new SingleLiveEvent<>();

    public AboutViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 关于我们
     */
    @SuppressLint("CheckResult")
    public void aboutUs() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .aboutUs(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanAboutUs>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanAboutUs.setValue(response.data);
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
