package com.ibd.dipper.ui.driverApprove;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.view.View;

import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.bean.BeanEnum;
import com.ibd.dipper.bean.BeanEnums;
import com.ibd.dipper.bean.BeanDriver;
import com.ibd.dipper.bean.BeanUpload;
import com.ibd.dipper.bean.BeanUserinfo;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.FileUtils;
import com.ibd.dipper.utils.JsonUtils;

import java.util.HashMap;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class DriverApproveViewModel extends BaseViewModel {
    public SingleLiveEvent<List<BeanEnum>> beanCarType = new SingleLiveEvent<>();
    public SingleLiveEvent<String> uploadImg = new SingleLiveEvent<>();
    public SingleLiveEvent<String> idCardFront = new SingleLiveEvent<>();
    public SingleLiveEvent<String> idCardBack = new SingleLiveEvent<>();
    public SingleLiveEvent<String> licenseFront = new SingleLiveEvent<>();
    public SingleLiveEvent<String> licenseBack = new SingleLiveEvent<>();
    public SingleLiveEvent<String> qualification = new SingleLiveEvent<>();
    public SingleLiveEvent<String> name = new SingleLiveEvent<>();
    public SingleLiveEvent<String> idNumber = new SingleLiveEvent<>();
    public SingleLiveEvent<String> qualificationNumber = new SingleLiveEvent<>();
    public SingleLiveEvent<String> licenseNumber = new SingleLiveEvent<>();
    public SingleLiveEvent<String> drivingType = new SingleLiveEvent<>();
    public SingleLiveEvent<String> drivingTypeStr = new SingleLiveEvent<>();

    public SingleLiveEvent<String> endDate = new SingleLiveEvent<>();
    public SingleLiveEvent<String> startDate = new SingleLiveEvent<>();

    public SingleLiveEvent<String> strDenialReason = new SingleLiveEvent<>();

    //订阅者
    private Disposable mSubscription;
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //相机选择弹窗
        public MutableLiveData<Integer> creatPhonePop = new MutableLiveData<>();
        //相机/相册
        public MutableLiveData<Integer> phoneType = new MutableLiveData<>();
        //车型弹窗
        public SingleLiveEvent carTypePop = new SingleLiveEvent();

        //日历弹窗
        public MutableLiveData<Integer> calendarPop = new MutableLiveData<>();
    }

    public DriverApproveViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 车辆类型
     */
    @SuppressLint("CheckResult")
    public void drivingType() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .drivingType(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnum>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanCarType.setValue(response.data);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 已认证信息
     */
    @SuppressLint("CheckResult")
    public void authenticationInfo() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .authenticationInfo(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanDriver>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        if (response.data == null)
                            return;
                        idCardFront.setValue(response.data.idCardFront);
                        idCardBack.setValue(response.data.idCardBack);
                        licenseFront.setValue(response.data.licenseFront);
                        licenseBack.setValue(response.data.licenseBack);
                        qualification.setValue(response.data.qualification);
                        name.setValue(response.data.name);
                        idNumber.setValue(response.data.IdCardNumber);
                        qualificationNumber.setValue(response.data.qualificationNumber);
                        licenseNumber.setValue(response.data.licenseNumber);
                        drivingType.setValue(response.data.drivingType);
                        strDenialReason.setValue(response.data.certificationReason);
                        startDate.setValue(response.data.idCardValidityStart);
                        endDate.setValue(response.data.idCardValidity);
                        for (BeanEnum beanCarType : beanCarType.getValue())
                            if (String.valueOf(beanCarType.id).equals(response.data.drivingType))
                                drivingTypeStr.setValue(beanCarType.name);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 身份证正面点击 phoneType 0
     */
    public View.OnClickListener phoneTypeClick0 = v -> uc.creatPhonePop.setValue(0);
    /**
     * 身份证反面点击 phoneType 1
     */
    public View.OnClickListener phoneTypeClick1 = v -> uc.creatPhonePop.setValue(1);
    /**
     * 驾驶证正面点击 phoneType 2
     */
    public View.OnClickListener phoneTypeClick2 = v -> uc.creatPhonePop.setValue(2);
    /**
     * 驾驶证反面点击 phoneType 3
     */
    public View.OnClickListener phoneTypeClick3 = v -> uc.creatPhonePop.setValue(3);
    /**
     * 道路运输证点击 phoneType 4
     */
    public View.OnClickListener phoneTypeClick4 = v -> uc.creatPhonePop.setValue(4);

    //准驾车型
    public View.OnClickListener drivingTypeClick = v -> uc.carTypePop.call();

    /**
     * 点击身份证开始时间
     */
    public View.OnClickListener startDateClick = v -> uc.calendarPop.setValue(1);

    /**
     * 点击身份证截止时间
     */
    public View.OnClickListener endDateClick = v -> uc.calendarPop.setValue(2);

    public View.OnClickListener doneClick = v -> {
        if (TextUtils.isEmpty(idCardFront.getValue())) {
            ToastUtils.showShort("请上传身份证正面照片");
            return;
        }
        if (TextUtils.isEmpty(idCardBack.getValue())){
            ToastUtils.showShort("请上传身份证反面照片");
            return;
        }
        if (TextUtils.isEmpty(licenseFront.getValue())){
            ToastUtils.showShort("请上传驾驶证正页照片");
            return;
        }
        if (TextUtils.isEmpty(licenseBack.getValue())){
            ToastUtils.showShort("请上传驾驶证副页照片");
            return;
        }
        if (TextUtils.isEmpty(qualification.getValue())){
            ToastUtils.showShort("请上传从业资格证照片");
            return;
        }
        if (TextUtils.isEmpty(name.getValue())){
            ToastUtils.showShort("请输入姓名");
            return;
        }
        if (TextUtils.isEmpty(idNumber.getValue())){
            ToastUtils.showShort("请输入身份证号");
            return;
        }
        if(idNumber.getValue().length()!=18){
            ToastUtils.showShort("身份证号码不正确");
            return;
        }
        if (TextUtils.isEmpty(startDate.getValue())){
            ToastUtils.showShort("请输入身份证有效期开始时间");
            return;
        }
        if (TextUtils.isEmpty(endDate.getValue())){
            ToastUtils.showShort("请输入身份证有效期结束时间");
            return;
        }
        if (TextUtils.isEmpty(qualificationNumber.getValue())){
            ToastUtils.showShort("请输入从业资格证号");
            return;
        }
        if (TextUtils.isEmpty(licenseNumber.getValue())){
            ToastUtils.showShort("请输入驾驶证号");
            return;
        }
        if (TextUtils.isEmpty(drivingType.getValue())){
            ToastUtils.showShort("请输入准驾车型");
            return;
        }

        authentication();
    };

    /**
     * 司机认证
     */
    @SuppressLint("CheckResult")
    private void authentication() {
        Map<String, String> params = new HashMap<>();
        params.put("idCardFront", idCardFront.getValue());
        params.put("idCardBack", idCardBack.getValue());
        params.put("licenseFront", licenseFront.getValue());
        params.put("licenseBack", licenseBack.getValue());
        params.put("qualification", qualification.getValue());
        params.put("name", name.getValue());
        params.put("idNumber", idNumber.getValue());
        params.put("qualificationNumber", qualificationNumber.getValue());
        params.put("licenseNumber", licenseNumber.getValue());
        params.put("drivingType", drivingType.getValue());
        params.put("idCardValidityStart", startDate.getValue());
        params.put("idCardValidity", endDate.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .authentication(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanUserinfo>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        finish();
                        ToastUtils.showLong(response.message);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 上传图片
     */
    @SuppressLint("CheckResult")
    public void upload() {
        byte[] path = FileUtils.readFile(uploadImg.getValue());
        //1.创建MultipartBody.Builder对象
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);//表单类型

        //2.获取图片，创建请求体
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), path);//表单类型

        //3.调用MultipartBody.Builder的addFormDataPart()方法添加表单数据
        builder.addFormDataPart(TOKEN, SPUtils.getInstance(USER).getString(TOKEN));//传入服务器需要的key，和相应value值
//        builder.addFormDataPart("is_new", "1"); //添加图片数据，body创建的请求体
        builder.addFormDataPart("filetype", "image"); //添加图片数据，body创建的请求体
        builder.addFormDataPart("file", FileUtils.getFileNameFromPath(uploadImg.getValue()), body); //添加图片数据，body创建的请求体


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
                        switch (uc.creatPhonePop.getValue()) {
                            case 0:
                                idCardFront.setValue(response.data.url);
                                break;
                            case 1:
                                idCardBack.setValue(response.data.url);
                                break;
                            case 2:
                                licenseFront.setValue(response.data.url);
                                break;
                            case 3:
                                licenseBack.setValue(response.data.url);
                                break;
                            case 4:
                                qualification.setValue(response.data.url);
                                break;
                        }
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
