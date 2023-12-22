package com.ibd.dipper.ui.bankNew;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanBankList;
import com.ibd.dipper.bean.BeanRegist;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.bank.BankActivity;
import com.ibd.dipper.ui.forget.ForgetViewModel;
import com.ibd.dipper.ui.wallet.WalletActivity;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;

import java.util.HashMap;
import java.util.Iterator;
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
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class BankNewViewModel extends BaseViewModel {
    public MutableLiveData<Integer> type = new MutableLiveData<>();
    //给RecyclerView添加items
    public final ObservableList<BankNewItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<BankNewItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_bank_list);

    public MutableLiveData<List<BeanBankList>> beanCity = new MutableLiveData<>();

    public MutableLiveData<String> bankNo = new MutableLiveData<>();
    public MutableLiveData<String> bankAcc = new MutableLiveData<>();
    public MutableLiveData<String> cityCode = new MutableLiveData<>();
    public MutableLiveData<String> cityName = new MutableLiveData<>();
    public MutableLiveData<String> mobile = new MutableLiveData<>();
    public MutableLiveData<String> code = new MutableLiveData<>();

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //开户城市列表
        public SingleLiveEvent cityTypePop = new SingleLiveEvent();

        public SingleLiveEvent<String> ontickCall = new SingleLiveEvent<>();

        public SingleLiveEvent<String> onfinishCall = new SingleLiveEvent<>();
    }

    public BankNewViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 下一步按鈕
     */
    public View.OnClickListener nextClick = v -> {
        if (type.getValue() == 0) {
            if (TextUtils.isEmpty(bankNo.getValue())) {
                ToastUtils.showShort("请选择银行");
                return;
            }
            if (TextUtils.isEmpty(bankAcc.getValue())) {
                ToastUtils.showShort("请输入银行储蓄卡卡号");
                return;
            }
//            if (TextUtils.isEmpty(cityCode.getValue())) {
//                ToastUtils.showShort("请选择开户行");
//                return;
//            }
            if (TextUtils.isEmpty(mobile.getValue())) {
                ToastUtils.showShort("请上填写手机号");
                return;
            }
            //type.setValue(1);
            cardBind();
        } else {
            if (TextUtils.isEmpty(code.getValue()))
                return;
            cardBind();
        }
    };

    /**
     * 綁卡
     */
    @SuppressLint("CheckResult")
    public void cardBind() {
        Map<String, String> params = new HashMap<>();
        params.put("bankNo", bankNo.getValue());
        params.put("bankAcc", bankAcc.getValue());
        params.put("cityCode", cityCode.getValue());
        params.put("mobile", mobile.getValue());
        params.put("code", code.getValue());
        showDialog();
        RetrofitClient.getInstance().create(ApiService.class)
                .cardBind(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        finish();
                        ToastUtils.showLong("绑定成功！");
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                    startActivity(WalletActivity.class);
                    dismissDialog();

                }, (Consumer<ResponseThrowable>) throwable -> {
                    dismissDialog();
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 開戶地按鈕
     */
    public View.OnClickListener cityPop = v -> uc.cityTypePop.call();

    /**
     * 選擇銀行
     *
     * @param bank
     */
    public void ChooseBank(BankNewItemViewModel bank) {
        Iterator it = observableList.iterator();
        while (it.hasNext()) {
            BankNewItemViewModel bankNewItemViewModel = (BankNewItemViewModel) it.next();
            bankNewItemViewModel.entity.get().choosed = false;
            bankNewItemViewModel.entity.notifyChange();
        }
        bank.entity.get().choosed = true;
        bank.entity.notifyChange();
        bankNo.setValue(bank.entity.get().bankNo);
        cityList();
    }

    /**
     * 银行列表
     */
    @SuppressLint("CheckResult")
    public void bankList() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .bankList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanBankList>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        for (BeanBankList orders : response.data) {
                            BankNewItemViewModel mainItemViewModel = new BankNewItemViewModel(this, orders);
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
     * 开户地列表
     */
    @SuppressLint("CheckResult")
    public void cityList() {
        Map<String, String> params = new HashMap<>();
        params.put("bankNo", bankNo.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .cityList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanBankList>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanCity.setValue(response.data);
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 获取验证码按鈕
     */
    public View.OnClickListener codeClick = v -> {
        if (!TextUtils.isEmpty(mobile.getValue()))
            getCode();
    };

    /**
     * 获取验证码接口
     */
    @SuppressLint("CheckResult")
    public void getCode() {
        Map<String, String> params = new HashMap<>();
        params.put("account", mobile.getValue());
        params.put("type", "3");
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
}
