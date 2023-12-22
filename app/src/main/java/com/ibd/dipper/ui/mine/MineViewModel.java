package com.ibd.dipper.ui.mine;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ibd.dipper.bean.BeanUpdata;
import com.ibd.dipper.bean.BeanUpload;
import com.ibd.dipper.bean.BeanUserinfo;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.carApproveList.CarApproveListActivity;
import com.ibd.dipper.ui.complaint.ComplaintActivity;
import com.ibd.dipper.ui.driverApprove.DriverApproveActivity;
import com.ibd.dipper.ui.online.OnlineActivity;
import com.ibd.dipper.ui.wallet.WalletActivity;
import com.ibd.dipper.ui.withdraw.WithdrawActivity;
import com.ibd.dipper.ui.withdrawDetail.WithdrawDetailActivity;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.FileUtils;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static com.ibd.dipper.Config.FACE;
import static com.ibd.dipper.Config.NAME;
import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class MineViewModel extends BaseViewModel {
    public MutableLiveData<BeanUserinfo> beanUserinfo = new MutableLiveData<>();
    public SingleLiveEvent<String> versionName = new SingleLiveEvent<>();
    public SingleLiveEvent<String> uploadImg = new SingleLiveEvent<>();
    public SingleLiveEvent<String> imgHead = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> isVisibleToUser = new SingleLiveEvent<>();

    //订阅者
    private Disposable mSubscription;

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent creatPhonePop = new SingleLiveEvent();

        public SingleLiveEvent carPopShow = new SingleLiveEvent();

        public SingleLiveEvent toSettingAc = new SingleLiveEvent();

        public SingleLiveEvent<String> newVersion = new SingleLiveEvent<>();

        public SingleLiveEvent<String> path = new SingleLiveEvent<>();
        //相机/相册
        public MutableLiveData<Integer> phoneType = new MutableLiveData<>();

        public SingleLiveEvent loginOut = new SingleLiveEvent();
    }

    public MineViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 头像
     */
    public View.OnClickListener phoneTypeClick = v -> {
        uc.creatPhonePop.setValue(0);
    };

    /**
     * 提现
     */
    public View.OnClickListener withdrawClick = v -> {
        Bundle bundle = new Bundle();
        bundle.putString("balance", beanUserinfo.getValue().balance);
        startActivity(WithdrawActivity.class, bundle);
    };

    /**
     * 明细
     */
    public View.OnClickListener detailClick = v -> startActivity(WithdrawDetailActivity.class);

    /**
     * 司机认证按钮
     */
    public View.OnClickListener driverApproveClick = v -> {
        Bundle bundle = new Bundle();
        bundle.putInt("carrierCertificationStatus", beanUserinfo.getValue().carrierCertificationStatus);
        startActivity(DriverApproveActivity.class, bundle);

    };

    /**
     * 车辆认证按钮
     */
    public View.OnClickListener carApproveClick = v -> {
        startActivity(CarApproveListActivity.class);
    };

    /**
     * 我的钱包按钮
     */
    public View.OnClickListener walletClick = v -> {
        Bundle bundle = new Bundle();
        bundle.putString("balance", beanUserinfo.getValue().balance);
        startActivity(WalletActivity.class, bundle);
    };

    /**
     * 投诉按钮
     */
    public View.OnClickListener complaintClick = v -> startActivity(ComplaintActivity.class);

    /**
     * 在线支持
     */
    public View.OnClickListener onlineClick = v -> startActivity(OnlineActivity.class);

    /**
     * 娱乐按钮
     */
    public View.OnClickListener yuleClick = v -> ToastUtils.showLong("敬请期待");

    /**
     * 设置
     */
    public View.OnClickListener settingClick = v -> uc.toSettingAc.call();

    /**
     * 检查更新
     */
    public View.OnClickListener updataClick = v -> version();

    /**
     * 用户信息
     */
    @SuppressLint("CheckResult")
    public void getUserInfo() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .userInfo(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanUserinfo>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        beanUserinfo.setValue(response.data);
                        imgHead.setValue(response.data.avatar);
                        SPUtils.getInstance(USER).put(NAME,response.data.name);
                        SPUtils.getInstance(USER).put(FACE,response.data.avatar);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 查询余额
     */
    @SuppressLint("CheckResult")
    public void accInfoQuery() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .accInfoQuery(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanUserinfo>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        if (beanUserinfo.getValue() != null) {
                            beanUserinfo.getValue().balance = response.data.balance;
                            beanUserinfo.setValue(beanUserinfo.getValue());
                        }
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 检查更新
     */
    @SuppressLint("CheckResult")
    public void version() {
        showDialog("加载中");
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .version(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanUpdata>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        dismissDialog();
                        if (versionName.getValue().equals(response.data.versionAndroid)) {
                            ToastUtils.showLong("已经是最新版本");
                        } else {
                            uc.newVersion.setValue(response.data.versionAndroidUrl);
                        }
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 下载apk
     */
    public void download(String url) {
        Random random=new Random();
        String loadUrl = url+"?a="+random.nextInt();
//        String loadUrl = "https://fga1.market.xiaomi.com/download/AppStore/04aaed3b6eef14c5ab8d0a419cff4456c39234936/com.shouqu.bookmarklife.apk";
        String destFileDir = UIUtils.getContext().getCacheDir().getPath();  //文件存放的路径
        String destFileName = System.currentTimeMillis() + ".apk";//文件存放的名称
        DownLoadManager.getInstance().load(loadUrl, new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
            @Override
            public void onStart() {
                //RxJava的onStart()
                showDialog("下载中");
            }

            @Override
            public void onCompleted() {
                //RxJava的onCompleted()
                uc.path.setValue(destFileDir + "/" + destFileName);
            }

            @Override
            public void onSuccess(ResponseBody responseBody) {
                //下载成功的回调
                dismissDialog();
            }

            @Override
            public void progress(final long progress, final long total) {
                //下载中的回调 progress：当前进度 ，total：文件总大小
                KLog.e("progress");
            }

            @Override
            public void onError(Throwable e) {
                //下载错误回调
                KLog.e("onError");
                ToastUtils.showLong("下载失败");
                dismissDialog();
            }
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
                        imgHead.setValue(response.data.url);
                        avatar(response.data.url);
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
    private void avatar(String url) {
        Map<String, String> params = new HashMap<>();
        params.put("avatar", url);
        RetrofitClient.getInstance().create(ApiService.class)
                .avatar(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanUserinfo>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {

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
                    if (isVisibleToUser.getValue())
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
