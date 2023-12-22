package com.ibd.dipper.ui.msg;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanMsg;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.msgDetail.MsgDetailActivity;
import com.ibd.dipper.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
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

public class MsgViewModel extends BaseViewModel {
    public MutableLiveData<BeanMsg> beanMsg = new MutableLiveData<>();

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent<Bundle> toMsgDetail = new SingleLiveEvent<>();
    }

    public MsgViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 消息接口
     */
    @SuppressLint("CheckResult")
    public void info() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .info(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanMsg>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanMsg.setValue(response.data);
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    public View.OnClickListener click = v -> {
        switch (v.getId()) {
            case R.id.cl0:
                toDetailActivity(0);
                break;
            case R.id.cl1:
                toDetailActivity(1);
                break;
            case R.id.cl2:
                toDetailActivity(2);
                break;
        }
    };

    /**
     * 跳转详情页
     */
    public void toDetailActivity(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        uc.toMsgDetail.setValue(bundle);
    }
}
