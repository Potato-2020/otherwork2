package com.ibd.dipper.ui.setting.psd;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.ibd.dipper.bean.BeanForgetPsd;
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
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class PsdViewModel extends BaseViewModel {
    public SingleLiveEvent<String> oldPassword = new SingleLiveEvent<>();
    public SingleLiveEvent<String> password = new SingleLiveEvent<>();
    public SingleLiveEvent<String> rePassword = new SingleLiveEvent<>();

    public PsdViewModel(@NonNull Application application) {
        super(application);
    }

    public View.OnClickListener doneClick = v -> {
        if (TextUtils.isEmpty(oldPassword.getValue())) {
            ToastUtils.showShort("请输入原密码");
            return;
        }
        if (TextUtils.isEmpty(password.getValue())) {
            ToastUtils.showShort("请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(rePassword.getValue())) {
            ToastUtils.showShort("请再次输入新密码");
            return;
        }
        if (!password.getValue().equals(rePassword.getValue())) {
            ToastUtils.showLong("两次密码不一致");
            return;
        }
        rePsd();
    };

    /**
     * 修改密码
     */
    @SuppressLint("CheckResult")
    public void rePsd() {
        Map<String, String> params = new HashMap<>();
        params.put("oldPassword", oldPassword.getValue());
        params.put("password", password.getValue());
        params.put("rePassword", rePassword.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .changePassword(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanForgetPsd>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        SPUtils.getInstance(USER).put(TOKEN, response.data.token);
                        RetrofitClient.setNull();
                        ToastUtils.showLong("修改成功");
                        finish();
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
