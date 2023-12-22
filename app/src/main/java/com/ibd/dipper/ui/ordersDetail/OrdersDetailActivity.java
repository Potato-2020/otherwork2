package com.ibd.dipper.ui.ordersDetail;

import android.app.ActionBar;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOrders;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.databinding.ActivityOrdersDetailBinding;
import com.ibd.dipper.uiPopupWindow.CarPopupWindow;
import com.ibd.dipper.utils.LocationUtils;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

import java.sql.Wrapper;

public class OrdersDetailActivity extends BaseActivity<ActivityOrdersDetailBinding, OrdersDetailViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_orders_detail;
    }

    @Override
    public int initVariableId() {
        return com.ibd.dipper.BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setColor(this, UIUtils.getColor(R.color.blue));

        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> finish());

        viewModel.context = this;
        viewModel.type.setValue(getIntent().getIntExtra("type", 0));
        Location location = LocationUtils.getLastKnownLocation(this);
        viewModel.longStr.setValue(location != null ? (location.getLongitude() + "") : "111.111111");
        viewModel.latStr.setValue(location != null ? (location.getLatitude() + "") : "111.111111");

        BeanOrdersDetail items = (BeanOrdersDetail) getIntent().getSerializableExtra("data");
        TextView textView26 = (TextView)findViewById(R.id.textView26);
        TextView textView266 = (TextView)findViewById(R.id.textView266);
        if(items.tmsOrderId.isEmpty() || items.tmsOrderId==""){
            textView26.setVisibility(View.GONE);
            textView266.setVisibility(View.GONE);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
//            //lp.setMargins(0, 0, 0, 0);
//            textView26.setLayoutParams(lp);
//            textView26.setHeight(0);
//            textView266.setHeight(0);
        }else{
            textView26.setVisibility(View.VISIBLE);
            textView266.setVisibility(View.VISIBLE);

//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            //lp.setMargins(0, 8, 0, 0);
//            lp.
//            textView26.setLayoutParams(lp);
//            textView26.setHeight(WrapperCon);
//            textView266.setHeight(0);
        }
        if (viewModel.type.getValue() == 0)
            viewModel.getDetail(items.id);
        else {
            viewModel.beanOrdersDetail.setValue(items);
            viewModel.setData();
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.carListPop.observe(this, o -> {
            CarPopupWindow carPopupWindow = new CarPopupWindow(this, OrdersDetailActivity.class.getName());
            carPopupWindow.showPopupWindow();
        });
    }
}
