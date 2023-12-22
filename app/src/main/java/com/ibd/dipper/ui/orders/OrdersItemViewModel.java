package com.ibd.dipper.ui.orders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.ibd.dipper.bean.BeanOrders;
import com.ibd.dipper.bean.BeanOrdersDetail;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.KLog;

public class OrdersItemViewModel extends ItemViewModel<OrdersViewModel> {
    public ObservableField<BeanOrdersDetail> entity = new ObservableField<>();
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();

    public OrdersItemViewModel(@NonNull OrdersViewModel viewModel, BeanOrdersDetail bean, Integer type) {
        super(viewModel);
        this.entity.set(bean);
        this.type.setValue(type);
        setText();
    }

    private void setText() {
        if (type.getValue() == 0) {
            switch (entity.get().status) {
                case 0:
                    entity.get().statusStr = "接单";
                    break;
                case 1:
                    entity.get().statusStr = "已拒绝";
                    break;
            }
        } else {
            switch (entity.get().biddingStatus) {
                case 1:
                    entity.get().biddingStatusStr = "抢单";
                    break;
                case 2:
                    entity.get().biddingStatusStr = "抢单中";
                    break;
            }
        }
    }

    /**
     * 拒绝
     */
    public View.OnClickListener refuseClick = v -> {
        viewModel.refuse(OrdersItemViewModel.this);
    };

    /**
     * 接受
     */
    public View.OnClickListener ordersClick = v -> {
        if (entity.get().status == 0)
            viewModel.orders(OrdersItemViewModel.this);
    };

    /**
     * 抢单
     */
    public View.OnClickListener grabOrdersClick = v -> {
        if (entity.get().biddingStatus == 1)
            viewModel.grabOrder(OrdersItemViewModel.this);
    };

    /**
     * 详情
     */
    public View.OnClickListener detailClick = v -> viewModel.ordersDetail(OrdersItemViewModel.this);

    /**
     * 拒绝变化
     */
    public void setRefuseStatus() {
        entity.get().status = 1;
        setText();
        entity.notifyChange();
    }

    /**
     * 抢单变化
     */
    public void setGrabOrderStatus() {
        entity.get().biddingStatus = 2;
        setText();
        entity.notifyChange();
    }
}
