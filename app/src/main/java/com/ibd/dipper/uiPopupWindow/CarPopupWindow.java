package com.ibd.dipper.uiPopupWindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanCar;
import com.ibd.dipper.bean.BeanCarList;
//import com.ibd.dipper.helper.XLinearLayoutManager;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.carApprove.CarApproveActivity;
import com.ibd.dipper.ui.mine.MineFragment;
import com.ibd.dipper.ui.mine.MineViewModel;
import com.ibd.dipper.ui.orders.OrdersFragment;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import razerdp.basepopup.BasePopupWindow;

public class CarPopupWindow extends BasePopupWindow {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.rcv)
    RecyclerView rcv;
    @BindView(R.id.tv_ok)
    Button tvOk;

    private CarAdapter carAdapter;
    private String type;

    public CarPopupWindow(Context context, String enterType) {
        super(context);
        this.type = enterType;

        setView();
    }

    private void setView() {
        if (type.equals(MineFragment.class.getName())) {
            tvTitle.setText("认证车辆");
        } else {
            tvTitle.setText("选择车辆");
        }
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
//        rcv.setLayoutManager(new XLinearLayoutManager(getContext()));
        getData();
    }

    @SuppressLint("CheckResult")
    private void getData() {
        Map<String, String> params = new HashMap<>();
        RetrofitClient.getInstance().create(ApiService.class)
                .myVehicleList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanCarList>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        for (BeanCar beanCar : response.data.items)
                            beanCar.Choose = false;
                        setBtnText(response);

                        carAdapter = new CarAdapter(R.layout.item_car, response.data.items, getContext(), type, this);
                        rcv.setAdapter(carAdapter);
                    } else {
                        ToastUtils.showLong(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    private void setBtnText(BaseResponse<BeanCarList> data) {
        if (type.equals(MineFragment.class.getName())) {
            tvOk.setText("前往认证");
        } else {
            if (data.data.items.isEmpty())
                tvOk.setText("暂无车辆，前往认证");
            else
                tvOk.setText("抢单");
        }
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_car);
    }

    @Override
    public void onViewCreated(View contentView) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick({R.id.iv_close, R.id.tv_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_ok:
                if (type.equals(MineFragment.class.getName())) {
                    getContext().startActivity(new Intent(getContext(), CarApproveActivity.class));
                } else {
                    if (carAdapter.getChoose() == -1)
                        ToastUtils.showLong("请先选择接单车辆");
                    else {
                        if (type.equals(OrdersFragment.class.getName()))
                            RxBus.getDefault().post(new BiddinOrdersgEvent(carAdapter.getChoose()));
                        else
                            RxBus.getDefault().post(new BiddinOrdersDetailgEvent(carAdapter.getChoose()));
                    }
                }
                dismiss();
                break;
        }
    }

    public class CarAdapter extends BaseQuickAdapter<BeanCar, BaseViewHolder> {
        CarPopupWindow carPopupWindow;
        private Context context;
        private String type;

        public CarAdapter(@LayoutRes int layoutResId, @Nullable List<BeanCar> data, Context context, String enterType, CarPopupWindow carPopupWindow) {
            super(layoutResId, data);
            this.context = context;
            this.type = enterType;
            this.carPopupWindow = carPopupWindow;
        }

        @Override
        protected void convert(BaseViewHolder viewHolder, final BeanCar item) {
            viewHolder.setText(R.id.num, "车牌号码：" + item.licensePlateNumber);
            viewHolder.setText(R.id.unit, "载重：" + item.loads);
            viewHolder.setText(R.id.type, "车辆类型：" + item.typeText);

            if (type.equals(MineFragment.class.getName()))
                viewHolder.setVisible(R.id.iv_choose, false);
            else {
                viewHolder.setVisible(R.id.iv_choose, true);
                if (item.Choose)
                    viewHolder.setImageDrawable(R.id.iv_choose, UIUtils.getRoundedDrawable(R.mipmap.xuanze, 0));
                else
                    viewHolder.setImageDrawable(R.id.iv_choose, UIUtils.getRoundedDrawable(R.mipmap.xuanze2, 0));
            }

            viewHolder.getView(R.id.parent).setOnClickListener(v -> {
                if (type.equals(MineFragment.class.getName())) {
                    RxBus.getDefault().post(new CarDetailEvent(item));
                    carPopupWindow.dismiss();
                } else {
                    for (BeanCar beanCar : mData)
                        beanCar.Choose = false;
                    item.Choose = true;
                    notifyDataSetChanged();
                }
            });
        }

        public int getChoose() {
            int chooseId = -1;
            for (BeanCar beanCar : mData)
                if (beanCar.Choose)
                    chooseId = beanCar.id;
            return chooseId;
        }
    }


    public class BiddinOrdersgEvent {
        public BiddinOrdersgEvent(int vehicleId) {
            this.vehicleId = vehicleId;
        }

        public int vehicleId;
    }

    public class BiddinOrdersDetailgEvent {
        public BiddinOrdersDetailgEvent(int vehicleId) {
            this.vehicleId = vehicleId;
        }

        public int vehicleId;
    }

    public class CarDetailEvent {
        public BeanCar beanCar;

        public CarDetailEvent(BeanCar beanCar) {
            this.beanCar = beanCar;
        }
    }
}
