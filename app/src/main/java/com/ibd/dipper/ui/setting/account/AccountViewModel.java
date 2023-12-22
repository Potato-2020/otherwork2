package com.ibd.dipper.ui.setting.account;

import android.annotation.SuppressLint;
import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.SplashActivity;
import com.ibd.dipper.ui.login.LoginActivity;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;

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

public class AccountViewModel extends BaseViewModel {
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //loginout的弹窗
        public SingleLiveEvent safePop = new SingleLiveEvent();
    }
    public AccountViewModel(@NonNull Application application) {
        super(application);
    }

    public View.OnClickListener safeClick = v -> {
        uc.safePop.call();
    };

    @SuppressLint("CheckResult")
    public void disableAccount() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .disableAccount(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        SPUtils.getInstance(USER).put(TOKEN, "");
                        RetrofitClient.setNull();
                        startActivity(SplashActivity.class);
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
