package com.ibd.dipper.ui.onlineFeedback;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOnline;
import com.ibd.dipper.bean.BeanUpload;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.onlineFeeddetail.OnlineFeedDetailAc;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.FileUtils;
import com.ibd.dipper.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class OnlineFeedBackViewModel extends BaseViewModel {
    public MutableLiveData<String> content = new SingleLiveEvent<>();
    //给RecyclerView添加items
    public final ObservableList<OnlineItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<OnlineItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_online);

    public SingleLiveEvent<Integer> maxPhone = new SingleLiveEvent<>();
    public MutableLiveData<List<String>> beanPhone = new MutableLiveData<>();
    //给RecyclerView添加items
    public final ObservableList<OnlinePhoneItemViewModel> observableList1 = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<OnlinePhoneItemViewModel> itemBinding1 = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_phone_online);

    //订阅者
    private Disposable mSubscription;
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //相机选择弹窗
        public SingleLiveEvent creatPhonePop = new SingleLiveEvent();
        //相机/相册
        public MutableLiveData<Integer> phoneType = new MutableLiveData<>();
    }

    public OnlineFeedBackViewModel(@NonNull Application application) {
        super(application);
    }

    @SuppressLint("CheckResult")
    public void setData() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("size", "1000");
        RetrofitClient.getInstance().create(ApiService.class)
                .feedbackList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanOnline>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        for (BeanOnline.Items bean : response.data.items) {
                            OnlineItemViewModel mainItemViewModel = new OnlineItemViewModel(this, bean);
                            observableList.add(mainItemViewModel);
                        }
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 提交按钮
     */
    @SuppressLint("CheckResult")
    public View.OnClickListener commitClick = v -> {
        if (!TextUtils.isEmpty(content.getValue())) {
            Map<String, String> params = new HashMap<>();
            params.put("content", content.getValue());
            List<String> photos = new ArrayList<>();
            for (String str : beanPhone.getValue()) {
                if (!str.equals("+"))
                    photos.add(str);
            }
            params.put("photos", JsonUtils.toJson(photos));
            RetrofitClient.getInstance().create(ApiService.class)
                    .feedback(params)
                    .compose(RxUtils.schedulersTransformer())  // 线程调度
                    .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                    .subscribe((Consumer<BaseResponse<String>>) response -> {
                        KLog.e(JsonUtils.toJson(response));
                        if (response.isOk()) {
                            finish();
                        }
                        ToastUtils.showLong(response.message);
                    }, (Consumer<ResponseThrowable>) throwable -> {
                        ToastUtils.showLong(throwable.message);
                    });
        }
    };

    public void toDetail(OnlineItemViewModel onlineItemViewModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", onlineItemViewModel.entity.get());
        startActivity(OnlineFeedDetailAc.class, bundle);
    }

    /**
     * 设置图片
     */
    public void setPhoeList() {
        Iterator it = observableList1.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        for (String str : beanPhone.getValue()) {
            OnlinePhoneItemViewModel mainItemViewModel = new OnlinePhoneItemViewModel(this, str);
            observableList1.add(mainItemViewModel);
        }
    }

    /**
     * 添加图片-选择图片方式
     */
    public void addPhone() {
        uc.creatPhonePop.call();
    }

    /**
     * 删除图片
     */
    public void del(String str) {
        Iterator it = beanPhone.getValue().iterator();
        while (it.hasNext()) {
            String photo = (String) it.next();
            if (photo.equals(str))
                it.remove();
        }
        beanPhone.setValue(beanPhone.getValue());
    }

    /**
     * 添加图片
     */
    public void setPhone(String path) {
        if (beanPhone.getValue().size() == maxPhone.getValue()) {
            if (beanPhone.getValue().contains("+")) {
                beanPhone.getValue().remove(beanPhone.getValue().size() - 1);
                beanPhone.getValue().add(0, path);
            }
        } else {
            beanPhone.getValue().add(0, path);
        }
        beanPhone.setValue(beanPhone.getValue());
    }

    /**
     * 上传图片
     */
    @SuppressLint("CheckResult")
    public void upload(String str) {
        byte[] path = FileUtils.readFile(str);
        //1.创建MultipartBody.Builder对象
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型

        //2.获取图片，创建请求体
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), path);//表单类型

        //3.调用MultipartBody.Builder的addFormDataPart()方法添加表单数据
        builder.addFormDataPart(TOKEN, SPUtils.getInstance(USER).getString(TOKEN));//传入服务器需要的key，和相应value值
//        builder.addFormDataPart("is_new", "1"); //添加图片数据，body创建的请求体
        builder.addFormDataPart("filetype", "image"); //添加图片数据，body创建的请求体
        builder.addFormDataPart("file", FileUtils.getFileNameFromPath(str), body); //添加图片数据，body创建的请求体


        //4.创建List<MultipartBody.Part> 集合，
        //  调用MultipartBody.Builder的build()方法会返回一个新创建的MultipartBody
        //  再调用MultipartBody的parts()方法返回MultipartBody.Part集合
        List<MultipartBody.Part> parts = builder.build().parts();

        RetrofitClient.getInstance().create(ApiService.class)
                .create(parts)
                .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) // 请求与View周期同步
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanUpload>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        setPhone(response.data.url);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    //注册RxBus
    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(UploadPopupWindow.ClickResult.class)
                .subscribe(s -> {
                    uc.phoneType.setValue(s.type);
                });
        //将订阅者加入管理站
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
