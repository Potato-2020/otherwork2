package com.ibd.dipper.ui.orders;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanCarList;
import com.ibd.dipper.bean.BeanOrders;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.bean.BeanUpdata;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.login.LoginActivity;
import com.ibd.dipper.ui.msg.MsgActivity;
import com.ibd.dipper.ui.ordersDetail.OrdersDetailActivity;
import com.ibd.dipper.ui.ordersDetail.RefuseSuccessEvent;
import com.ibd.dipper.ui.setting.about.AboutActivity;
import com.ibd.dipper.uiPopupWindow.CarPopupWindow;
import com.ibd.dipper.uiPopupWindow.RefusePopupWindow;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
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
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import okhttp3.ResponseBody;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;

public class OrdersViewModel extends BaseViewModel {
    private Context context;
    public MutableLiveData<Integer> type = new MutableLiveData<>();
    public SingleLiveEvent<String> versionName = new SingleLiveEvent<>();



    //给RecyclerView添加items
    public final ObservableList<OrdersItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public final ItemBinding<OrdersItemViewModel> itemBinding = ItemBinding.of(com.ibd.dipper.BR.viewModel, R.layout.item_orders);

    //订阅者
    private Disposable mSubscription;
    private Disposable mSubscriptionRefuse;
    private Disposable mSubscriptionBidding;
    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadmore = new SingleLiveEvent<>();
        //没有更多
        public SingleLiveEvent finishNoMore = new SingleLiveEvent();
        //认证车辆pop
        public SingleLiveEvent carListPop = new SingleLiveEvent();
        //loginout的弹窗
        public SingleLiveEvent loginOut = new SingleLiveEvent();

        public SingleLiveEvent<String> newVersion = new SingleLiveEvent<>();
        public SingleLiveEvent<String> path = new SingleLiveEvent<>();
    }

    public OrdersItemViewModel consignItemViewModelSingleLiveEvent;

    public MutableLiveData<Integer> page = new MutableLiveData<>();
    public MutableLiveData<String> longStr = new MutableLiveData<>();
    public MutableLiveData<String> latStr = new MutableLiveData<>();
    public MutableLiveData<String> startTime = new MutableLiveData<>();
    public MutableLiveData<String> endTime = new MutableLiveData<>();

    public OrdersViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData(Context context) {
        if (type.getValue() == 0)
            getDispatchList(context);
        else
            biddingList(context);
    }

    /**
     * 委托列表
     */
    @SuppressLint("CheckResult")
    public void getDispatchList(Context context) {
        this.context = context;
        Map<String, String> params = new HashMap<>();
        params.put("keyword", "");
        params.put("long", longStr.getValue());
        params.put("lat", latStr.getValue());
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .dispatchList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanOrders>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    dismissDialog();
                    if (response.isOk()) {
                        if (page.getValue() == 1) {
                            Iterator it = observableList.iterator();
                            while (it.hasNext()) {
                                it.next();
                                it.remove();
                            }
                            uc.finishRefreshing.call();
                        } else {
                            if (response.data.pages < page.getValue()) {
                                page.setValue(page.getValue() - 1);
                                uc.finishNoMore.call();
                                return;
                            }
                            uc.finishLoadmore.call();
                        }

                        for (BeanOrdersDetail orders : response.data.items) {
                            OrdersItemViewModel mainItemViewModel = new OrdersItemViewModel(this, orders, type.getValue());
                            observableList.add(mainItemViewModel);
                        }
                    } else {
                        ToastUtils.showLong(response.message);
                        if (response.code == 0 | response.message.contains("token")) {
                            SPUtils.getInstance(USER).put(TOKEN, "");
                            RetrofitClient.setNull();
                            uc.loginOut.call();
                        }
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 抢单列表
     */
    @SuppressLint("CheckResult")
    public void biddingList(Context context) {
        this.context = context;
        Map<String, String> params = new HashMap<>();
        params.put("keyword", "");
        params.put("long", longStr.getValue());
        params.put("lat", latStr.getValue());
        params.put("startTime", startTime.getValue());
        params.put("endTime", endTime.getValue());
        params.put("page", page.getValue() + "");
        params.put("size", "10");
        RetrofitClient.getInstance().create(ApiService.class)
                .biddingList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanOrders>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    dismissDialog();
                    if (response.isOk()) {
                        if (page.getValue() == 1) {
                            Iterator it = observableList.iterator();
                            while (it.hasNext()) {
                                it.next();
                                it.remove();
                            }
                            uc.finishRefreshing.call();
                        } else {
                            if (response.data.pages < page.getValue()) {
                                page.setValue(page.getValue() - 1);
                                uc.finishNoMore.call();
                                return;
                            }
                            uc.finishLoadmore.call();
                        }

                        for (BeanOrdersDetail orders : response.data.items) {
                            OrdersItemViewModel mainItemViewModel = new OrdersItemViewModel(this, orders, type.getValue());
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
     * 获取这个月初与月末
     */
    public void getThisMM() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        Date date = calendar.getTime();

        startTime.setValue(getTime(date).substring(0, getTime(date).length() - 2) + "01");

        calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.roll(Calendar.DATE, -1);
        date = calendar.getTime();
        endTime.setValue(getTime(date));
    }

    public String getTime(Date date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 娱乐按钮
     */
    public View.OnClickListener yuleClick = v -> ToastUtils.showLong("敬请期待");

    /**
     * 消息按钮
     */
    public View.OnClickListener msgClick = v -> startActivity(MsgActivity.class);

    /**
     * 接单tab按钮
     */
    public BindingCommand type0Click = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            page.setValue(1);
            type.setValue(0);
            showDialog();
            setData(context);
        }
    });

    /**
     * 抢单tab按钮
     */
    public BindingCommand type1Click = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            page.setValue(1);
            type.setValue(1);
            showDialog();
            setData(context);
        }
    });

    /**
     * 拒接按钮
     */
    public void refuse(OrdersItemViewModel consignItemViewModel) {
        this.consignItemViewModelSingleLiveEvent = consignItemViewModel;
        RefusePopupWindow refusePopupWindow = new RefusePopupWindow(context, false);
        refusePopupWindow.showPopupWindow();
    }

    /**
     * 接单按钮
     */
    public void orders(OrdersItemViewModel consignItemViewModel) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title("确认接受订单")
                .positiveText("确定")
                .negativeText("取消")
                .onAny((dialog, which) -> {
                    switch (which) {
                        case NEGATIVE:
                            dismissDialog();
                            break;
                        case POSITIVE:
                            taking(consignItemViewModel);
                            break;
                    }
                });
        builder.show();
    }

    /**
     * 抢单
     */
    public void grabOrder(OrdersItemViewModel consignItemViewModel) {
        this.consignItemViewModelSingleLiveEvent = consignItemViewModel;
        uc.carListPop.call();
    }

    /**
     * 提交拒绝理由接口
     */
    @SuppressLint("CheckResult")
    public void refuseStrSubmit(String str) {
        Map<String, String> params = new HashMap<>();
        params.put("str", str);
        params.put("id", consignItemViewModelSingleLiveEvent.entity.get().id);
        RetrofitClient.getInstance().create(ApiService.class)
                .refuse(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        consignItemViewModelSingleLiveEvent.setRefuseStatus();
                    }
                    ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 接单接口
     */
    @SuppressLint("CheckResult")
    public void taking(OrdersItemViewModel consignItemViewModel) {
        Map<String, String> params = new HashMap<>();
        params.put("long", longStr.getValue());
        params.put("lat", latStr.getValue());
        params.put("id", Objects.requireNonNull(consignItemViewModel.entity.get()).id);
        RetrofitClient.getInstance().create(ApiService.class)
                .taking(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        observableList.remove(consignItemViewModel);
                    }
                    ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 抢单接口
     */
    @SuppressLint("CheckResult")
    public void bidding(int vehicleId) {
        Map<String, String> params = new HashMap<>();
        params.put("long", longStr.getValue());
        params.put("lat", latStr.getValue());
        params.put("vehicleId", vehicleId +"");
        params.put("id", Objects.requireNonNull(consignItemViewModelSingleLiveEvent.entity.get()).id);
        RetrofitClient.getInstance().create(ApiService.class)
                .bidding(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<String>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        consignItemViewModelSingleLiveEvent.setGrabOrderStatus();
                    }
                    ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 订单详情
     */
    public void ordersDetail(OrdersItemViewModel consignItemViewModel) {
        this.consignItemViewModelSingleLiveEvent = consignItemViewModel;
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", consignItemViewModel.entity.get());
        bundle.putInt("type", type.getValue());
        startActivity(OrdersDetailActivity.class, bundle);
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
                            //ToastUtils.showLong("已经是最新版本");
                        } else {
                            //ToastUtils.showLong("发现新版本版本号"+response.data.versionAndroid);
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

    //注册RxBus
    @Override
    public void registerRxBus() {
        super.registerRxBus();
        mSubscription = RxBus.getDefault().toObservable(RefuseEvent.class).subscribe(s -> {
            refuseStrSubmit(s.str);
        });
        mSubscriptionRefuse = RxBus.getDefault().toObservable(RefuseSuccessEvent.class).subscribe(s -> {
            Iterator it = observableList.iterator();
            while (it.hasNext()) {
                OrdersItemViewModel ordersItemViewModel = (OrdersItemViewModel) it.next();
                if (s.isOrders & s.status == 1 & ordersItemViewModel.entity.get().id.equals(s.id)) {//详情页拒绝返回
                    ordersItemViewModel.setRefuseStatus();
                }
                if (s.isOrders & s.status == 2 & ordersItemViewModel.entity.get().id.equals(s.id)) {//详情页接受返回
                    it.remove();
                }
                if (!s.isOrders & s.biddingStatus == 2 & ordersItemViewModel.entity.get().id.equals(s.id)) { //详情页抢单返回
                    ordersItemViewModel.setGrabOrderStatus();
                }
            }
        });
        mSubscriptionBidding = RxBus.getDefault().toObservable(CarPopupWindow.BiddinOrdersgEvent.class).subscribe(s -> {
            new Handler().postDelayed(() -> {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                        .title("确认接受订单")
                        .positiveText("确定")
                        .negativeText("取消")
                        .onAny((dialog, which) -> {
                            switch (which) {
                                case NEGATIVE:
                                    dismissDialog();
                                    break;
                                case POSITIVE:
                                    bidding(s.vehicleId);
                                    break;
                            }
                        });
                builder.show();
            },200);
        });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
        RxSubscriptions.add(mSubscriptionRefuse);
        RxSubscriptions.add(mSubscriptionBidding);
    }

    //移除RxBus
    @Override
    public void removeRxBus() {
        super.removeRxBus();
        //将订阅者从管理站中移除
        RxSubscriptions.remove(mSubscription);
        RxSubscriptions.remove(mSubscriptionRefuse);
        RxSubscriptions.remove(mSubscriptionBidding);
    }
}
