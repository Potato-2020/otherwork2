package com.ibd.dipper.ui.carApproveList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanCar;
import com.ibd.dipper.bean.BeanCarTrailer;
import com.ibd.dipper.databinding.ActivityCarApproveListBinding;
import com.ibd.dipper.ui.carApprove.CarApproveActivity;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class CarApproveListActivity extends BaseActivity<ActivityCarApproveListBinding, CarApproveListViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_car_approve_list;
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
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.car_approve_list_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        viewModel.getData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.toCarApproveActivity.observe(this, o -> {
            Intent intent = new Intent(CarApproveListActivity.this, CarApproveActivity.class);
            intent.putExtra("otype", "1");
//            intent.putExtra("trailerList", new ArrayList<>());
            startActivityForResult(intent, 100);
        });
        viewModel.uc.toCarApproveActivity2.observe(this, beanCar -> {
            Intent intent = new Intent(CarApproveListActivity.this, CarApproveActivity.class);
            intent.putExtra("otype", "1");
            intent.putExtra("data",beanCar);
            switch (beanCar.certificationStatus) {
                case 0:
                    intent.putExtra("otype", "3");
                    break;
                case 1:
                    intent.putExtra("otype", "0");
                    break;
                case -1:
                    intent.putExtra("otype", "2");
                    break;
            }
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 & resultCode == 101)
            viewModel.getData();
    }
}
