package com.ibd.dipper.ui.carApproveList;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanCar;
import com.ibd.dipper.bean.BeanCarList;
import com.ibd.dipper.bean.BeanCarTrailer;
import com.ibd.dipper.bean.BeanCarTrailerList;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.carApprove.CarApproveTrailerViewModel;
import com.ibd.dipper.utils.JsonUtils;

import java.util.*;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class CarApproveListViewModel extends BaseViewModel {
    //给RecyclerView添加items
    public final ObservableList<CarApproveItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<CarApproveItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_car_approve);

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent toCarApproveActivity = new SingleLiveEvent();

        public SingleLiveEvent<BeanCar> toCarApproveActivity2 = new SingleLiveEvent<>();
    }

    public CarApproveListViewModel(@NonNull Application application) {
        super(application);
    }

    @SuppressLint("CheckResult")
    public void getData() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .vehicleAuthenticationList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanCarList>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        Iterator it = observableList.iterator();
                        while (it.hasNext()) {
                            it.next();
                            it.remove();
                        }
                        try{
                            for (BeanCar orders : response.data.items) {
                                orders.loads = subZeroAndDot(orders.loads);
                                orders.trailerHeight = subZeroAndDot(orders.trailerHeight);
                                orders.trailerLength = subZeroAndDot(orders.trailerLength);
                                orders.trailerWidth = subZeroAndDot(orders.trailerWidth);
                                orders.trailerTotalMass = subZeroAndDot(orders.trailerTotalMass);
//                            orders.trailerList=
                                List<BeanCarTrailer> trailerListList=new ArrayList<>();
                                for (BeanCarTrailer trailer : orders.trailerList) {
                                    trailerListList.add(trailer);
                                }
                                if(trailerListList.size()>0){
                                    orders.trailerList=trailerListList;
                                }

                                CarApproveItemViewModel mainItemViewModel = new CarApproveItemViewModel(this, orders);
                                observableList.add(mainItemViewModel);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    public View.OnClickListener toAddClick = v -> uc.toCarApproveActivity.call();

    public static String subZeroAndDot(String s) {
        if (TextUtils.isEmpty(s))
            return "";
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
}
