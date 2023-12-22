package com.ibd.dipper.ui.forget;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanForgetPsd;
import com.ibd.dipper.bean.BeanRegist;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.MainActivity;
import com.ibd.dipper.ui.login.LoginViewModel;
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

public class ForgetViewModel extends BaseViewModel {

    public SingleLiveEvent<String> account = new SingleLiveEvent<>();
    public SingleLiveEvent<String> code = new SingleLiveEvent<>();
    public SingleLiveEvent<String> password = new SingleLiveEvent<>();

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<String> ontickCall = new SingleLiveEvent<>();

        public SingleLiveEvent<String> onfinishCall = new SingleLiveEvent<>();

        public SingleLiveEvent loginSuccess = new SingleLiveEvent();
    }

    public ForgetViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 账号监听
     */
    public TextWatcher accountListen = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            account.setValue(s.toString());
        }
    };

    /**
     * 验证码监听
     */
    public TextWatcher codeListen = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            code.setValue(s.toString());
        }
    };

    /**
     * 密码监听
     */
    public TextWatcher passwordListen = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            password.setValue(s.toString());
        }
    };

    /**
     * 获取验证码
     */
    public View.OnClickListener codeClick = v -> {
        if (!account.getValue().isEmpty())
            getCode();
    };

    /**
     * 获取验证码接口
     */
    @SuppressLint("CheckResult")
    public void getCode() {
        Map<String, String> params = new HashMap<>();
        params.put("account", account.getValue());
        params.put("type", "2");
        RetrofitClient.getInstance().create(ApiService.class)
                .getCode(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanRegist>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000, 1000);
                        myCountDownTimer.start();
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 验证码重发计时
     */
    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            uc.ontickCall.setValue(UIUtils.getString(R.string.Reacquire_after_S, ((l / 1000) + "")));
        }

        @Override
        public void onFinish() {
            uc.onfinishCall.setValue(UIUtils.getString(R.string.Reacquire));
        }
    }

    /**
     * 确定按钮
     */
    public View.OnClickListener doneClick = v -> {
        if (account.getValue().isEmpty()) {
            ToastUtils.showShort("请输入手机号码");
            return;
        }
        if (code.getValue().isEmpty()){
            ToastUtils.showShort("请输入验证码");
            return;
        }
        if (password.getValue().isEmpty()){
            ToastUtils.showShort("请重新设置密码");
            return;
        }
        resetPassword();
    };

    /**
     * 注册接口
     */
    @SuppressLint("CheckResult")
    public void resetPassword() {
        Map<String, String> params = new HashMap<>();
        params.put("account", account.getValue());
        params.put("code", code.getValue());
        params.put("password", password.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .resetPassword(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanForgetPsd>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        SPUtils.getInstance(USER).put(TOKEN, response.data.token);
                        RetrofitClient.setNull();
                        uc.loginSuccess.call();
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
