package com.ibd.dipper.ui.onlineFeeddetail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOnline;
import com.ibd.dipper.databinding.ActivityOnlineDetailBinding;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.SPUtils;

import static com.ibd.dipper.Config.FACE;
import static com.ibd.dipper.Config.NAME;
import static com.ibd.dipper.Config.USER;

public class OnlineFeedDetailAc extends BaseActivity<ActivityOnlineDetailBinding, OnlineFeedDetailViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_online_detail;
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
        tvTitle.setText(getString(R.string.online_feedback_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        BeanOnline.Items data = (BeanOnline.Items) getIntent().getSerializableExtra("data");
        binding.rcv.setLayoutManager(new GridLayoutManager(this, 3));

        viewModel.data.setValue(data);
        viewModel.name.setValue(SPUtils.getInstance(USER).getString(NAME));
        viewModel.face.setValue(SPUtils.getInstance(USER).getString(FACE));
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.data.observe(this, items -> {
            for (String str : items.photos) {
                OnlinePhoneItemViewModel mainItemViewModel = new OnlinePhoneItemViewModel(viewModel, str);
                viewModel.observableList1.add(mainItemViewModel);
            }
        });
    }
}
