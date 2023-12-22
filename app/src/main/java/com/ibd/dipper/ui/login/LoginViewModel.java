package com.ibd.dipper.ui.login;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanLogin;
import com.ibd.dipper.bean.BeanRegist;
import com.ibd.dipper.bean.BeanShipper;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.MainActivity;
import com.ibd.dipper.ui.forget.ForgetActivity;
import com.ibd.dipper.ui.orders.OrdersItemViewModel;
import com.ibd.dipper.ui.setting.agree.AgreeActivity;
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
import me.tatarka.bindingcollectionadapter2.ItemBinding;

import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class LoginViewModel extends BaseViewModel {
    public MutableLiveData<Integer> type = new MutableLiveData<>(); //0登录1注册
    public SingleLiveEvent<String> account = new SingleLiveEvent<>();
    public SingleLiveEvent<String> code = new SingleLiveEvent<>();
    public SingleLiveEvent<String> password = new SingleLiveEvent<>();

    public SingleLiveEvent<BeanShipper> beanShipper = new SingleLiveEvent<>();
    //给RecyclerView添加items
    public final ObservableList<ShipperItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<ShipperItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_shipper);

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<String> ontickCall = new SingleLiveEvent<>();

        public SingleLiveEvent<String> onfinishCall = new SingleLiveEvent<>();

        public SingleLiveEvent toForgetActivity = new SingleLiveEvent();
    }

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 货主信誉公示
     */
    @SuppressLint("CheckResult")
    public void shipperList() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("size", "100");
        RetrofitClient.getInstance().create(ApiService.class)
                .shipperList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanShipper>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanShipper.setValue(response.data);
                        for (BeanShipper.Items items : response.data.items) {
                            ShipperItemViewModel shipperItemViewModel = new ShipperItemViewModel(this, items, response.data.items.indexOf(items));
                            observableList.add(shipperItemViewModel);
                        }
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 登入/注册按钮
     */
    public View.OnClickListener registClick = v -> {
        if (type.getValue() == 0)
            type.setValue(1);
        else
            type.setValue(0);
    };

    /**
     * 忘记密码按钮
     */
    public View.OnClickListener forgetClick = v -> uc.toForgetActivity.call();

    /**
     * 获取验证码按钮
     */
    public View.OnClickListener codeClick = v -> {
        if (!TextUtils.isEmpty(account.getValue()))
            getCode();
    };

    /**
     * 用户协议按钮
     */
    public View.OnClickListener agree0Click = v -> {
        Bundle bundle = new Bundle();
        bundle.putString("title", "用户协议");
        startActivity(AgreeActivity.class, bundle);
    };

    /**
     * 隐私条款按钮
     */
    public View.OnClickListener agree1Click = v -> {
        Bundle bundle = new Bundle();
        bundle.putString("title", "隐私条款");
        startActivity(AgreeActivity.class, bundle);
    };

    /**
     * 获取验证码接口
     */
    @SuppressLint("CheckResult")
    public void getCode() {
        Map<String, String> params = new HashMap<>();
        params.put("account", account.getValue());
        params.put("type", "1");
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
     * 登录或注册按钮
     */
    public View.OnClickListener registOrLoginClick = v -> {
        if (TextUtils.isEmpty(account.getValue())) {
            if (type.getValue() == 0)
                ToastUtils.showShort(UIUtils.getString(R.string.login_phone_str_hint));
            else
                ToastUtils.showShort(UIUtils.getString(R.string.regist_phone_str_hint));
            return;
        }
        if (TextUtils.isEmpty(code.getValue())) {
            if (type.getValue() == 0)
                ToastUtils.showShort(UIUtils.getString(R.string.login_phone_psd));
            else
                ToastUtils.showShort(UIUtils.getString(R.string.regist_phone_code));
            return;
        }
        if (type.getValue() == 1 & TextUtils.isEmpty(password.getValue())) {
            ToastUtils.showShort(UIUtils.getString(R.string.login_phone_psd));
            return;
        }
        showDialog("加载中");
        if (type.getValue() == 0)
            login();
        else
            regist();
    };

    /**
     * 注册接口
     */
    @SuppressLint("CheckResult")
    public void regist() {
        Map<String, String> params = new HashMap<>();
        params.put("account", account.getValue());
        params.put("code", code.getValue());
        params.put("password", password.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .regist(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanRegist>>) response -> {
                    dismissDialog();
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        SPUtils.getInstance(USER).put(TOKEN, response.data.token);
                        RetrofitClient.setNull();
                        startActivity(MainActivity.class);
                        finish();
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    dismissDialog();
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 登录接口
     */
    @SuppressLint("CheckResult")
    public void login() {
        Map<String, String> params = new HashMap<>();
        params.put("account", account.getValue());
        params.put("password", code.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .login(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanLogin>>) response -> {
                    dismissDialog();
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        SPUtils.getInstance(USER).put(TOKEN, response.data.token);
                        RetrofitClient.setNull();
                        startActivity(MainActivity.class);
                        finish();
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    dismissDialog();
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
}
