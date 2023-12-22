package com.ibd.dipper.ui.onlineFeedback;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ibd.dipper.R;
import com.ibd.dipper.databinding.ActivityOnlineFeedbackBinding;
import com.ibd.dipper.ui.taskStatus.TaskStatusActivity;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.GlideEngine;
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
import me.goldze.mvvmhabit.utils.ToastUtils;

public class OnlineFeedBackActivity extends BaseActivity<ActivityOnlineFeedbackBinding, OnlineFeedBackViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_online_feedback;
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

        viewModel.maxPhone.setValue(5);
        binding.rcv.setLayoutManager(new GridLayoutManager(this, 3));

        viewModel.setData();

        List<String> beanPhone = new ArrayList<>();
        beanPhone.add("+");
        viewModel.beanPhone.setValue(beanPhone);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.beanPhone.observe(this, strings -> viewModel.setPhoeList());

        viewModel.uc.creatPhonePop.observe(this, integer -> {
            UploadPopupWindow uploadPopupWindow = new UploadPopupWindow(OnlineFeedBackActivity.this);
            uploadPopupWindow.showPopupWindow();
        });

        viewModel.uc.phoneType.observe(this, integer -> {
            switch (integer) {
                case 0:
                    checkCamarePermission();
                    break;
                case 1:
                    PictureSelector.create(OnlineFeedBackActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(1)
                            .isCamera(false)
                            .loadImageEngine(GlideEngine.createGlideEngine())
//                                    .setLanguage(finalLanguage)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                    break;
            }
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
            files.add(localMedia.getAndroidQToPath().isEmpty() ? localMedia.getPath() : localMedia.getAndroidQToPath());
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
