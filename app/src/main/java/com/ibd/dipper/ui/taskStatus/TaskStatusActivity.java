package com.ibd.dipper.ui.taskStatus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.bean.BeanTaskDetail;
import com.ibd.dipper.databinding.ActivityTaskStatusBinding;
import com.ibd.dipper.ui.driverApprove.DriverApproveActivity;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.GlideEngine;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.LocationUtils;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ImageUtils;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class TaskStatusActivity extends BaseActivity<ActivityTaskStatusBinding, TaskStatusViewModel> {
    TextView tvTitle;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_task_status;
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
        tvTitle = findViewById(R.id.tv_title);

        binding.rcv.setLayoutManager(new GridLayoutManager(this, 3));

        Location location = LocationUtils.getLastKnownLocation(this);
        viewModel.longStr.setValue(location != null ? (location.getLongitude() + "") : "111.111111");
        viewModel.latStr.setValue(location != null ? (location.getLatitude() + "") : "111.111111");

        viewModel.maxPhone.setValue(5);
        List<String> beanPhone = new ArrayList<>();
        beanPhone.add("+");
        viewModel.beanPhone.setValue(beanPhone);

        viewModel.status.setValue(getIntent().getIntExtra("status", 0));
        viewModel.beanOrders.setValue((BeanOrdersDetail) getIntent().getSerializableExtra("data"));
        viewModel.unit.setValue(viewModel.beanOrders.getValue().dispatchQuantity.contains("车") ? "2" : "1");
        viewModel.unitStr.setValue(viewModel.beanOrders.getValue().dispatchQuantity.contains("车") ? "车" : "吨");
        viewModel.isChange.setValue(getIntent().getSerializableExtra("nodes") != null);

        if (getIntent().getSerializableExtra("nodes") != null) {
            BeanTaskDetail.Nodes nodes = (BeanTaskDetail.Nodes) getIntent().getSerializableExtra("nodes");
            viewModel.quantity.setValue(nodes.quantity + "");
            viewModel.receiptNo.setValue(nodes.receiptNo);
            for (String str : nodes.photos) {
                viewModel.setPhone(str);
            }
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.beanPhone.observe(this, strings -> viewModel.setPhoeList());
        tvTitle.setText(getIntent().getSerializableExtra("nodes") == null ? viewModel.beanOrders.getValue().statusStr : "修改订单");
        viewModel.status.observe(this, integer -> {
            switch (integer) {
                case 2:
                    binding.tvNoticeStr.setText("装车现场照片（选填）");
                    binding.tvNoticeStr2.setText("装车现场照片（示例）");
                    break;
                case 3:
                    binding.tvNoticeStr.setText("磅单照片（选填）");
                    binding.tvNoticeStr2.setText("磅单照片（示例）");
                    break;
                case 4:
                    binding.tvNoticeStr.setText("卸货现场照片（选填）");
                    binding.tvNoticeStr2.setText("卸货现场照片（示例）");
                    break;
                case 5:
                    binding.tvNoticeStr.setText("卸货磅单照片（选填）");
                    binding.tvNoticeStr2.setText("卸货磅单照片（示例）");
                    break;
            }
        });
        viewModel.uc.creatPhonePop.observe(this, integer -> {
            UploadPopupWindow uploadPopupWindow = new UploadPopupWindow(TaskStatusActivity.this);
            uploadPopupWindow.showPopupWindow();
        });
        viewModel.uc.phoneType.observe(this, integer -> {
            switch (integer) {
                case 0:
                    checkCamarePermission();
                    break;
                case 1:
                    PictureSelector.create(TaskStatusActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(1)
                            .isCamera(false)
                            .loadImageEngine(GlideEngine.createGlideEngine())
//                                    .setLanguage(finalLanguage)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                    break;
            }
        });
        viewModel.uc.doneSuccess.observe(this, o -> {
            setResult(301);
            finish();
        });
    }

    @SuppressLint("CheckResult")
    private void checkCamarePermission() {
        //请求打开相机权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        PictureSelector.create(this)
                                .openCamera(PictureMimeType.ofImage())
                                .loadImageEngine(GlideEngine.createGlideEngine()) // Please refer to the Demo GlideEngine.java
                                .forResult(PictureConfig.REQUEST_CAMERA);
                    } else {
                        ToastUtils.showShort("相机权限被拒绝，请到设置中打开！");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            compressWithRx(PictureSelector.obtainMultipleResult(data));
        }
    }

    private void compressWithRx(List<LocalMedia> selectList) {
        List<String> files = new ArrayList<>();
        for (LocalMedia localMedia : selectList) {
            files.add(localMedia.getAndroidQToPath()==null ||localMedia.getAndroidQToPath().isEmpty() ? localMedia.getPath() : localMedia.getAndroidQToPath());
        }
        ImageUtils.compressWithRx(files, new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                File file = (File) o;
                viewModel.upload(file.getPath());
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showLong("图片压缩失败!");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
