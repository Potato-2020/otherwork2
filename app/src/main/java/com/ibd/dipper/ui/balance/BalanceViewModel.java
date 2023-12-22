package com.ibd.dipper.ui.balance;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.bean.BeanUserinfo;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.withdraw.WithdrawActivity;
import com.ibd.dipper.ui.withdrawDetail.WithdrawDetailActivity;
import com.ibd.dipper.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class BalanceViewModel extends BaseViewModel {
    public MutableLiveData<BeanUserinfo> beanUserinfo = new MutableLiveData<>();

    public BalanceViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 提现
     */
    public View.OnClickListener withdrawClick = v -> {
        Bundle bundle = new Bundle();
        bundle.putString("balance", beanUserinfo.getValue().balance);
        startActivity(WithdrawActivity.class, bundle);
    };

    /**
     * 明细
     */
    public View.OnClickListener detailClick = v -> startActivity(WithdrawDetailActivity.class);

    /**
     * 余额查询
     */
    @SuppressLint("CheckResult")
    public void accInfoQuery() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .accInfoQuery(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanUserinfo>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanUserinfo.setValue(response.data);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
