package com.ibd.dipper.ui.carApprove;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.*;
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

public class CarApproveViewModel extends BaseViewModel {
    public SingleLiveEvent<BeanCar> beanCar = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> id = new SingleLiveEvent<>();
    public SingleLiveEvent<Integer> otype = new SingleLiveEvent<>();
    public SingleLiveEvent<String> uploadImg = new SingleLiveEvent<>();
    public MutableLiveData<String> licenseMain = new MutableLiveData<>();
    public MutableLiveData<String> licenseFront = new MutableLiveData<>();
    public MutableLiveData<String> transportCertificate = new MutableLiveData<>();
    public MutableLiveData<String> licensePlateNumber = new MutableLiveData<>();
    public MutableLiveData<String> loads = new MutableLiveData<>();
    public MutableLiveData<String> transportCertificateNumber = new MutableLiveData<>();
    public MutableLiveData<String> fileNumber = new MutableLiveData<>();
    public MutableLiveData<String> totalMass = new MutableLiveData<>();
    public MutableLiveData<String> approvedLoad = new MutableLiveData<>();
    public MutableLiveData<String> length = new MutableLiveData<>();
    public MutableLiveData<String> width = new MutableLiveData<>();
    public MutableLiveData<String> height = new MutableLiveData<>();

    public MutableLiveData<Boolean> beTralier = new MutableLiveData<>();
    public MutableLiveData<Boolean> beTralierClickable = new MutableLiveData<>();
    public MutableLiveData<String> strDenialReason = new MutableLiveData<>();


    public SingleLiveEvent<Integer> listindex = new SingleLiveEvent<>();

//    public MutableLiveData<String> trailerLicensePlate = new MutableLiveData<>();
//    public MutableLiveData<String> trailerLicenseFront = new MutableLiveData<>();
//    public MutableLiveData<String> trailerLicenseBack = new MutableLiveData<>();
//    public MutableLiveData<String> trailerLoads = new MutableLiveData<>();
//    public MutableLiveData<String> trailerLength = new MutableLiveData<>();
//    public MutableLiveData<String> trailerWidth = new MutableLiveData<>();
//    public MutableLiveData<String> trailerHeight = new MutableLiveData<>();
//    public MutableLiveData<String> trailerTotalMass = new MutableLiveData<>();


    public MutableLiveData<List<BeanEnum>> energyTypeList = new MutableLiveData<>();
    public SingleLiveEvent<String> energyType = new SingleLiveEvent<>();
    public SingleLiveEvent<String> energyTypeStr = new SingleLiveEvent<>();
    public MutableLiveData<List<BeanEnums>> licensePlateColorList = new MutableLiveData<>();
    public SingleLiveEvent<String> licensePlateColor = new SingleLiveEvent<>();
    public SingleLiveEvent<String> licensePlateColorStr = new SingleLiveEvent<>();
    public MutableLiveData<List<BeanEnums>> licensePlateTypeList = new MutableLiveData<>();
    public SingleLiveEvent<String> licensePlateType = new SingleLiveEvent<>();
    public SingleLiveEvent<String> licensePlateTypeStr = new SingleLiveEvent<>();
    public MutableLiveData<BeanEnums> vehicleTypeList = new MutableLiveData<>();
    public SingleLiveEvent<String> vehicleType = new SingleLiveEvent<>();
    public SingleLiveEvent<String> vehicleTypeStr = new SingleLiveEvent<>();

    public MutableLiveData<List<BeanCarTrailer>> trailerList = new MutableLiveData<List<BeanCarTrailer>>();

    //订阅者
    private Disposable mSubscription;
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //能源类型
        public SingleLiveEvent energyTypePop = new SingleLiveEvent();
        //牌照颜色
        public SingleLiveEvent licensePlateColorPop = new SingleLiveEvent();
        //牌照类型
        public SingleLiveEvent licensePlateTypePop = new SingleLiveEvent();
        //能源类型
        public SingleLiveEvent vehicleTypePop = new SingleLiveEvent();
        //相机选择弹窗
        public MutableLiveData<Integer> creatPhonePop = new MutableLiveData<>();
        //相机/相册
        public MutableLiveData<Integer> phoneType = new MutableLiveData<>();
        //添加认证成功
        public SingleLiveEvent approveSuccess = new SingleLiveEvent();
    }

    public CarApproveViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 驾驶证正面点击 phoneType 0
     */
    public View.OnClickListener phoneTypeClick0 = v -> {
        if (beTralierClickable.getValue())
            uc.creatPhonePop.setValue(0);
    };
    /**
     * 驾驶证副页点击 phoneType 1
     */
    public View.OnClickListener phoneTypeClick1 = v -> {
        if (beTralierClickable.getValue())
            uc.creatPhonePop.setValue(1);
    };
    /**
     * 道路运输从业证点击 phoneType 2
     */
    public View.OnClickListener phoneTypeClick2 = v -> {
        if (beTralierClickable.getValue())
            uc.creatPhonePop.setValue(2);
    };
    /**
     * 挂车正面
     */
    public View.OnClickListener phoneTypeClick3 = v -> {
        uc.creatPhonePop.setValue(3);
    };
    /**
     * 挂车反面
     */
    public View.OnClickListener phoneTypeClick4 = v -> uc.creatPhonePop.setValue(4);

    /**
     * 能源类型
     */
    @SuppressLint("CheckResult")
    public void energyType() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .energyType(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnum>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        energyTypeList.setValue(response.data);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message+"能源类型");
                });
    }

    public View.OnClickListener energyTypeClick = v -> {
        if (beTralierClickable.getValue())
            uc.energyTypePop.call();
    };

    /**
     * 牌照颜色
     */
    @SuppressLint("CheckResult")
    public void licensePlateColor() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .licensePlateColor(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnums>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        licensePlateColorList.setValue(response.data);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    public View.OnClickListener licensePlateColorClick = v -> {
        if (beTralierClickable.getValue())
            uc.licensePlateColorPop.call();
    };

    /**
     * 牌照类型
     */
    @SuppressLint("CheckResult")
    public void licensePlateType() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .licensePlateType(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnums>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        licensePlateTypeList.setValue(response.data);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    public View.OnClickListener licensePlateTypeClick = v -> {
        if (beTralierClickable.getValue())
            uc.licensePlateTypePop.call();
    };

    /**
     * 车辆类型
     */
    @SuppressLint("CheckResult")
    public void vehicleType() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .vehicleType(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanEnums>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        vehicleTypeList.setValue(response.data);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    public View.OnClickListener vehicleTypeClick = v -> {
        if (beTralierClickable.getValue())
            uc.vehicleTypePop.call();
    };

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
                                licenseMain.setValue(response.data.url);
                                break;
                            case 1:
                                licenseFront.setValue(response.data.url);
                                break;
                            case 2:
                                transportCertificate.setValue(response.data.url);
                                break;
                            case 3:
                                List<BeanCarTrailer> trailers=trailerList.getValue();
                                trailers.get(listindex.getValue()).trailerLicenseAuxiliaryFrontPhoto=response.data.url;
                                trailerList.postValue(trailers);

                                Message msg = Message.obtain();
                                msg.what = 1;
                                Bundle data = new Bundle();
                                data.putString("url", response.data.url);
                                data.putInt("listindex",listindex.getValue());
                                msg.setData(data);
                                CarApproveActivity.setUrlHandle.sendMessage(msg);

//                                trailerLicenseFront.setValue(response.data.url);
                                break;
                            case 4:
                                List<BeanCarTrailer> trailers2=trailerList.getValue();
                                trailers2.get(listindex.getValue()).trailerLicenseAuxiliaryBackPhoto=response.data.url;
                                trailerList.postValue(trailers2);

                                //CarApproveActivity.
                                Message msg2 = Message.obtain();
                                msg2.what = 2;
                                Bundle data2 = new Bundle();
                                data2.putString("url", response.data.url);
                                data2.putInt("listindex",listindex.getValue());
                                msg2.setData(data2);
                                CarApproveActivity.setUrlHandle.sendMessage(msg2);

                                //ImageView trailerLicenseAuxiliaryBackPhotoImage=trailerView.findViewById(R.id.trailerLicenseAuxiliaryBackPhoto);
                                //trailerLicenseBack.setValue(response.data.url);
                                break;
                        }
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 认证点击
     */
    public View.OnClickListener doneClick = v -> {
        if (TextUtils.isEmpty(licenseMain.getValue())) {
            ToastUtils.showShort("请上传行驶证（正页）图片");
            return;
        }
        if (TextUtils.isEmpty(licenseFront.getValue())) {
            ToastUtils.showShort("请上传行驶证副页图片");
            return;
        }
        if (TextUtils.isEmpty(transportCertificate.getValue())) {
            ToastUtils.showShort("请上传道路运输证图片");
            return;
        }
        if (TextUtils.isEmpty(energyType.getValue())) {
            ToastUtils.showShort("请选择能源类型");
            return;
        }
        if (TextUtils.isEmpty(licensePlateColor.getValue())) {
            ToastUtils.showShort("请选择车牌颜色");
            return;
        }
        if (TextUtils.isEmpty(licensePlateType.getValue())) {
            ToastUtils.showShort("请选择牌照类型");
            return;
        }
        if (TextUtils.isEmpty(licensePlateNumber.getValue())) {
            ToastUtils.showShort("请输入车牌号码");
            return;
        }
        if (TextUtils.isEmpty(loads.getValue())) {
            ToastUtils.showShort("请输入车辆载重");
            return;
        }
        if (TextUtils.isEmpty(transportCertificateNumber.getValue())) {
            ToastUtils.showShort("请输入道路运输证号");
            return;
        }
        if (TextUtils.isEmpty(fileNumber.getValue())) {
            ToastUtils.showShort("请上输入档案编号");
            return;
        }
        if (TextUtils.isEmpty(totalMass.getValue())) {
            ToastUtils.showShort("请输入总质量");
            return;
        }
        if (TextUtils.isEmpty(approvedLoad.getValue())) {
            ToastUtils.showShort("请输入核定载重量");
            return;
        }
        if (TextUtils.isEmpty(length.getValue())) {
            ToastUtils.showShort("请输入车长");
            return;
        }
        if (TextUtils.isEmpty(width.getValue())) {
            ToastUtils.showShort("请输入车宽");
            return;
        }
        if (TextUtils.isEmpty(height.getValue())) {
            ToastUtils.showShort("请输入车高");
            return;
        }
        if (TextUtils.isEmpty(vehicleType.getValue()) && TextUtils.isEmpty(vehicleTypeStr.getValue())) {
            ToastUtils.showShort("请选择车辆类型");
            return;
        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerLicensePlate.getValue())) {
//            ToastUtils.showShort("请输入挂车牌照");
//            return;
//        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerLicenseFront.getValue())) {
//            ToastUtils.showShort("请上传挂车行驶证正页图片");
//            return;
//        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerLicenseBack.getValue())) {
//            ToastUtils.showShort("请上传挂车行驶证副页图片");
//            return;
//        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerLoads.getValue())) {
//            ToastUtils.showShort("请输入挂车载重量");
//            return;
//        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerLength.getValue())) {
//            ToastUtils.showShort("请输入挂车长度");
//            return;
//        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerWidth.getValue())) {
//            ToastUtils.showShort("请输入挂车宽度");
//            return;
//        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerHeight.getValue())) {
//            ToastUtils.showShort("请输入挂车高度");
//            return;
//        }
//        if (beTralier.getValue() & TextUtils.isEmpty(trailerTotalMass.getValue())) {
//            ToastUtils.showShort("请输入挂车总质量");
//            return;
//        }
        if (beTralierClickable.getValue())
            vehicleAuthentication();
        else {
            trailerAuthentication();
        }
    };


    /**
     * 车辆认证 提交
     */
    @SuppressLint("CheckResult")
    public void vehicleAuthentication() {
        Map<String, Object> params = new HashMap<>();
        params.put("otype", otype.getValue() + "");
        params.put("id", TextUtils.isEmpty(id.getValue() + "") ? "" : id.getValue() + "");
        params.put("licenseMain", licenseMain.getValue());
        params.put("licenseFront", licenseFront.getValue());
        params.put("transportCertificate", transportCertificate.getValue());
        params.put("energyType", energyType.getValue());
        params.put("licensePlateColor", licensePlateColor.getValue());
        params.put("licensePlateType", licensePlateType.getValue());
        params.put("licensePlateNumber", licensePlateNumber.getValue());
        params.put("loads", loads.getValue());
        params.put("transportCertificateNumber", transportCertificateNumber.getValue());
        params.put("fileNumber", fileNumber.getValue());
        params.put("totalMass", totalMass.getValue());
        params.put("approvedLoad", approvedLoad.getValue());
        params.put("width", width.getValue());
        params.put("height", height.getValue());
        params.put("type", vehicleType.getValue());
        params.put("typeStr", vehicleTypeStr.getValue());
//        params.put("trailerLicensePlate", trailerLicensePlate.getValue());
//        params.put("trailerLicenseFront", trailerLicenseFront.getValue());
//        params.put("trailerLicenseBack", trailerLicenseBack.getValue());
//        params.put("trailerLoads", trailerLoads.getValue());
//        params.put("trailerLength", trailerLength.getValue());
//        params.put("trailerWidth", trailerWidth.getValue());
//        params.put("trailerHeight", trailerHeight.getValue());
//        params.put("trailerTotalMass", trailerTotalMass.getValue());
        params.put("length", length.getValue());

//        LinearLayout mLinearLayout = (LinearLayout)this.(R.id.trailer_box);
//        int childCount = mLinearLayout.getChildCount();
//        //遍历下面所有的子控件，判断是否是layout
//        for(int i = 0; i < childCount; i++){
//            if(l.getChildAt(i) instanceof LinearLayout || l.getChildAt(i) instanceof RelativeLayout || l.getChildAt(i) instanceof TableLayout || l.getChildAt(i) instanceof AbsoluteLayout){
//                //操作代码
//            }
//        }
//        mGridView = View.inflate(this,R.layout.acitvity_car_approve_trailer,null);

        params.put("trailerList",trailerList.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .vehicleAuthentication(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnums>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        uc.approveSuccess.call();
                        ToastUtils.showLong(response.message);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 修改挂车信息
     */
    @SuppressLint("CheckResult")
    public void trailerAuthentication() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", TextUtils.isEmpty(id.getValue() + "") ? "" : id.getValue() + "");
//        params.put("trailerLicensePlate", trailerLicensePlate.getValue());
//        params.put("trailerLicenseFront", trailerLicenseFront.getValue());
//        params.put("trailerLicenseBack", trailerLicenseBack.getValue());
//        params.put("trailerLoads", trailerLoads.getValue());
//        params.put("trailerLength", trailerLength.getValue());
//        params.put("trailerWidth", trailerWidth.getValue());
//        params.put("trailerHeight", trailerHeight.getValue());
//        params.put("trailerTotalMass", trailerTotalMass.getValue());

        params.put("trailerList",trailerList.getValue());
        RetrofitClient.getInstance().create(ApiService.class)
                .trailerAuthentication(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnums>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        uc.approveSuccess.call();
                        ToastUtils.showLong(response.message);
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
