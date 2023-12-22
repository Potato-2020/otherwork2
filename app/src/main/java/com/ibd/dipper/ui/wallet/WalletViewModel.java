package com.ibd.dipper.ui.wallet;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.ui.balance.BalanceActivity;
import com.ibd.dipper.ui.bank.BankActivity;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class WalletViewModel extends BaseViewModel {
    public MutableLiveData<String> balance = new MutableLiveData<>();

    public WalletViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 余额按钮
     */
    public View.OnClickListener balanceClick = v -> startActivity(BalanceActivity.class);

    /**
     * 银行卡按钮
     */
    public View.OnClickListener bankClick = v -> startActivity(BankActivity.class);
}
