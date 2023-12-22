package com.ibd.dipper.ui;

import android.app.Application;

import androidx.annotation.NonNull;

import com.ibd.dipper.ui.msg.SystemTurnEvent;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class MainViewModel extends BaseViewModel {
    private Disposable mSubscription;

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent<Integer> turnSystem = new SingleLiveEvent<>();
    }
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    //注册RxBus
    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(SystemTurnEvent.class).subscribe(s -> {
            uc.turnSystem.setValue(s.tab0);
        });
        RxSubscriptions.add(mSubscription);
    }

    //移除RxBus
    @Override
    public void removeRxBus() {
        super.removeRxBus();
        //将订阅者从管理站中移除
        RxSubscriptions.remove(mSubscription);
    }
}
