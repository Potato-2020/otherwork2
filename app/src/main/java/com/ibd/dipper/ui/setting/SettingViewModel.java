package com.ibd.dipper.ui.setting;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.setting.about.AboutActivity;
import com.ibd.dipper.ui.setting.account.AccountActivity;
import com.ibd.dipper.ui.setting.agree.AgreeActivity;
import com.ibd.dipper.ui.setting.psd.PsdActivity;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.SPUtils;

import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class SettingViewModel extends BaseViewModel {
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent loginOut = new SingleLiveEvent();

        public SingleLiveEvent toAccountAc = new SingleLiveEvent();
    }

    public SettingViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 账号与安全
     */
    public View.OnClickListener accountClick = v -> {
        uc.toAccountAc.call();
    };

    /**
     * 修改密码
     */
    public View.OnClickListener psdClick = v -> startActivity(PsdActivity.class);

    /**
     * 退出机制
     */
    public View.OnClickListener exitClick = v -> {
        Bundle bundle = new Bundle();
        bundle.putString("title", "退出机制公示");
        startActivity(AgreeActivity.class, bundle);
    };

    /**
     * 关于我们
     */
    public View.OnClickListener aboutClick = v -> startActivity(AboutActivity.class);

    /**
     * 法律声明
     */
    public View.OnClickListener termsClick = v -> {
        Bundle bundle = new Bundle();
        bundle.putString("title", "法律申明");
        startActivity(AgreeActivity.class, bundle);
    };

    /**
     * 退出登录
     */
    public View.OnClickListener loginOut = v -> {
        uc.loginOut.call();
        SPUtils.getInstance(USER).put(TOKEN, "");
        RetrofitClient.setNull();
    };
}
