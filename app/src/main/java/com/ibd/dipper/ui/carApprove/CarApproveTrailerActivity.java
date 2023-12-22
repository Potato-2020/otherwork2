package com.ibd.dipper.ui.carApprove;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanCar;
import com.ibd.dipper.bean.BeanCarTrailer;
import com.ibd.dipper.bean.BeanEnum;
import com.ibd.dipper.bean.BeanEnums;
import com.ibd.dipper.databinding.AcitvityCarApproveTrailerBinding;
import com.ibd.dipper.databinding.ActivityCarApproveBinding;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.GlideEngine;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.ImageUtils;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarApproveTrailerActivity extends BaseActivity<AcitvityCarApproveTrailerBinding, CarApproveTrailerViewModel> implements  TextWatcher  {





    private AutoCompleteTextView autoCompleteTextView;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.acitvity_car_approve_trailer;
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

//        TextView tvTitle = findViewById(R.id.tv_title);
//        tvTitle.setText(getString(R.string.car_approve_str));
//        TextView tvBack = findViewById(R.id.tv_back);
//        tvBack.setOnClickListener(v -> onBackPressed());

//        viewModel.otype.setValue(Integer.parseInt(getIntent().getStringExtra("otype")));
//        viewModel.beTralier.setValue(false);
//        viewModel.beTralierClickable.setValue(true);
//        binding.tvDenialReason.setVisibility(View.GONE);
//        switch (getIntent().getStringExtra("otype")) {
//            case "0":
//                binding.btnCommit.setVisibility(View.GONE);
//                viewModel.beanCar.setValue((BeanCar) getIntent().getSerializableExtra("data"));
//
//
//                new Handler().postDelayed(() -> {
//                    if (viewModel.beTralier.getValue()) {
//                        KLog.e(viewModel.beTralier.getValue());
//                        binding.btnCommit.setVisibility(View.VISIBLE);
//                        binding.btnCommit.setText("修改挂车信息");
//                        viewModel.beTralierClickable.setValue(false);
//                    }
//                }, 200);
//                break;
//            case "1"://车辆认证
//                binding.btnCommit.setText("提交认证");
//                break;
//            case "2"://车辆重新认
//                viewModel.beanCar.setValue((BeanCar) getIntent().getSerializableExtra("data"));
//                binding.btnCommit.setText("重新认证");
//                binding.tvDenialReason.setVisibility(View.VISIBLE);
//                break;
//            case "3"://修改认证信息
//                viewModel.beanCar.setValue((BeanCar) getIntent().getSerializableExtra("data"));
//                binding.btnCommit.setText("修改认证");
//                break;
//        }
//
//
//        viewModel.energyType();
//        viewModel.licensePlateColor();
//        viewModel.licensePlateType();






        //子布局
//        mGridView = View.inflate(this,R.layout.acitvity_car_approve_trailer,null);
//        mLinearLayout = (LinearLayout)findViewById(R.id.trailer_box);
//        mLinearLayout.addView(mGridView);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //initAutoComplete(autoCompleteTextView.getText().toString(),autoCompleteTextView);

    }

    public void addTrailer1(View view) {
//        trailerView = View.inflate(this,R.layout.acitvity_car_approve_trailer,null);
//        trailerBoxLinearLayout = (LinearLayout)findViewById(R.id.trailer_box);
//        trailerBoxLinearLayout.addView(trailerView);

        viewModel.uc.creatPhonePop.setValue(3);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
//        viewModel.beanCar.observe(this, beanCar -> {
//            viewModel.id.setValue(beanCar.id);
//            viewModel.licenseMain.setValue(beanCar.licenseMain);
//            viewModel.licenseFront.setValue(beanCar.licenseFront);
//            viewModel.transportCertificate.setValue(beanCar.transportCertificate);
//            viewModel.energyType.setValue(beanCar.energyType);
//            viewModel.energyTypeStr.setValue(beanCar.energyTypeText);
//            viewModel.licensePlateType.setValue(beanCar.licensePlateType);
//            viewModel.licensePlateTypeStr.setValue(beanCar.licensePlateTypeText);
//            viewModel.licensePlateColor.setValue(beanCar.licensePlateColor);
//            viewModel.licensePlateColorStr.setValue(beanCar.licensePlateColorText);
//            viewModel.vehicleType.setValue(beanCar.type);
//            viewModel.vehicleTypeStr.setValue(beanCar.typeText);
//            viewModel.beTralier.setValue(beanCar.typeText.contains("牵引"));
//
//            viewModel.licensePlateNumber.setValue(beanCar.licensePlateNumber);
//            viewModel.loads.setValue(beanCar.loads);
//            viewModel.transportCertificateNumber.setValue(beanCar.transportCertificateNumber);
//            viewModel.fileNumber.setValue(beanCar.fileNumber);
//            viewModel.totalMass.setValue(beanCar.totalMass);
//            viewModel.approvedLoad.setValue(beanCar.approvedLoad);
//            viewModel.length.setValue(beanCar.length);
//            viewModel.width.setValue(beanCar.width);
//            viewModel.height.setValue(beanCar.height);
//
////            viewModel.trailerLicensePlate.setValue(beanCar.trailerLicensePlate);
////            viewModel.trailerLicenseFront.setValue(beanCar.trailerLicenseFront);
////            viewModel.trailerLicenseBack.setValue(beanCar.trailerLicenseBack);
////            viewModel.trailerLoads.setValue(beanCar.trailerLoads);
////            viewModel.trailerLength.setValue(beanCar.trailerLength);
////            viewModel.trailerWidth.setValue(beanCar.trailerWidth);
////            viewModel.trailerHeight.setValue(beanCar.trailerHeight);
////            viewModel.trailerTotalMass.setValue(beanCar.trailerTotalMass);
//
//            viewModel.strDenialReason.setValue(beanCar.certificationReason);
//
//            viewModel.trailerList.setValue(beanCar.trailerList);
//        });
//        viewModel.uc.approveSuccess.observe(this, o -> {
//            setResult(101);
//            finish();
//        });
        viewModel.uc.creatPhonePop.observe(this, integer -> {
            UploadPopupWindow uploadPopupWindow = new UploadPopupWindow(CarApproveTrailerActivity.this);
            uploadPopupWindow.showPopupWindow();
        });
        viewModel.uc.phoneType.observe(this, integer -> {
            switch (integer) {
                case 0:
                    checkCamarePermission();
                    break;
                case 1:
                    PictureSelector.create(CarApproveTrailerActivity.this)
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
            files.add(localMedia.getAndroidQToPath()==null || localMedia.getAndroidQToPath().isEmpty() ? localMedia.getPath() : localMedia.getAndroidQToPath());
        }
        ImageUtils.compressWithRx(files, new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                File file = (File) o;
                viewModel.uploadImg.setValue(file.getPath());
                switch (viewModel.uc.creatPhonePop.getValue()) {
//                    case 0:
//                        viewModel.licenseMain.setValue(file.getPath());
//                        break;
//                    case 1:
//                        viewModel.licenseFront.setValue(file.getPath());
//                        break;
//                    case 2:
//                        viewModel.transportCertificate.setValue(file.getPath());
//                        break;
                    case 3:
                        viewModel.trailerLicenseAuxiliaryFrontPhoto.setValue(file.getPath());
                        break;
                    case 4:
                        viewModel.trailerLicenseAuxiliaryBackPhoto.setValue(file.getPath());
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showLong("图片压缩失败!");
            }

            @Override
            public void onComplete() {
                viewModel.upload();
            }
        });
    }
}
