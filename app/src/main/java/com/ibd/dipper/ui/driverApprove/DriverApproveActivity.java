package com.ibd.dipper.ui.driverApprove;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanEnum;
import com.ibd.dipper.bean.BeanEnums;
import com.ibd.dipper.databinding.ActivityDriverApproveBinding;
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

public class DriverApproveActivity extends BaseActivity<ActivityDriverApproveBinding, DriverApproveViewModel> {
    private OptionsPickerView carTypeOptions;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_driver_approve;
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
        tvTitle.setText(getString(R.string.driver_approve_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        viewModel.drivingType();
        binding.tvDenialReason.setVisibility(View.GONE);
        switch (getIntent().getIntExtra("carrierCertificationStatus", -2)) {
            case -2://未认证
                binding.btnDone.setText("提交认证");
                break;
            case -1://认证失败
                binding.btnDone.setText("重新认证");
                new Handler().postDelayed(() -> viewModel.authenticationInfo(), 300);
                binding.tvDenialReason.setVisibility(View.VISIBLE);
                break;
            case 0://认证中
            case 1://已认证
                binding.btnDone.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(() -> viewModel.authenticationInfo(), 300);
                break;
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.creatPhonePop.observe(this, integer -> {
            UploadPopupWindow uploadPopupWindow = new UploadPopupWindow(DriverApproveActivity.this);
            uploadPopupWindow.showPopupWindow();
        });
        viewModel.uc.phoneType.observe(this, integer -> {
            switch (integer) {
                case 0:
                    checkCamarePermission();
                    break;
                case 1:
                    PictureSelector.create(DriverApproveActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(1)
                            .isCamera(false)
                            .loadImageEngine(GlideEngine.createGlideEngine())
//                                    .setLanguage(finalLanguage)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                    break;
            }
        });
        viewModel.uc.carTypePop.observe(this, o -> {
            carTypeOptions = new OptionsPickerBuilder(DriverApproveActivity.this, (options1, options2, options3, v) -> {
                viewModel.drivingType.setValue(viewModel.beanCarType.getValue().get(options1).id + "");
                viewModel.drivingTypeStr.setValue(viewModel.beanCarType.getValue().get(options1).name);
            }).setSubmitText("确定")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleText("请选择")//标题
                    .setSubCalSize(18)//确定和取消文字大小
                    .setTitleSize(0)//标题文字大小
                    .setTitleColor(Color.BLACK)//标题文字颜色
                    .setSubmitColor(UIUtils.getColor(R.color.blue))//确定按钮文字颜色
                    .setCancelColor(Color.GRAY)//取消按钮文字颜色
                    .setTitleBgColor(getApplication().getResources().getColor(R.color.white))//标题背景颜色 Night mode
                    .setBgColor(getApplication().getResources().getColor(R.color.white))//滚轮背景颜色 Night mode
                    .setDividerColor(Color.parseColor("#EFEFEE"))
                    .setContentTextSize(18)//滚轮文字大小
                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                    .setCyclic(false, false, false)//循环与否
                    .setSelectOptions(0, 0, 0)  //设置默认选中项
                    .setOutSideCancelable(true)//点击外部dismiss default true
                    .isDialog(false)//是否显示为对话框样式
                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                    .build();
            List list = new ArrayList();
            for (BeanEnum carType : viewModel.beanCarType.getValue())
                list.add(carType.name);
            carTypeOptions.setNPicker(list, null, null);
            carTypeOptions.show();
        });

        viewModel.uc.calendarPop.observe(this, integer -> {
            DatePickerDialog.OnDateSetListener onDateSetListener =new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month+1;
                    String strMonth="";
                    String strDay="";
                    if(month<10){
                        strMonth="0"+month;
                    }else{
                        strMonth=""+month;
                    }
                    if(day<10){
                        strDay="0"+day;
                    }else{
                        strDay=""+day;
                    }
                    String str = year+"-"+strMonth+"-"+strDay;//+"日";//把日期变成字符串格式显示出来
                    if(integer==1){
                        viewModel.startDate.setValue(str);//文本框显示的内容设置成经过逻辑处理后的日期
                    }
                    if(integer==2){
                        viewModel.endDate.setValue(str);//文本框显示的内容设置成经过逻辑处理后的日期
                    }

                }
            };
            new DatePickerDialog(DriverApproveActivity.this,5,onDateSetListener,2022,5,1).show();
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
                    case 0:
                        viewModel.idCardFront.setValue(file.getPath());
//                        Glide.with(DriverApproveActivity.this).load(file.getPath()).into(binding.iv0);
                        break;
                    case 1:
                        viewModel.idCardBack.setValue(file.getPath());
//                        Glide.with(DriverApproveActivity.this).load(file.getPath()).into(binding.iv1);
                        break;
                    case 2:
                        viewModel.licenseFront.setValue(file.getPath());
//                        Glide.with(DriverApproveActivity.this).load(file.getPath()).into(binding.iv2);
                        break;
                    case 3:
                        viewModel.licenseBack.setValue(file.getPath());
//                        Glide.with(DriverApproveActivity.this).load(file.getPath()).into(binding.iv3);
                        break;
                    case 4:
                        viewModel.qualification.setValue(file.getPath());
//                        Glide.with(DriverApproveActivity.this).load(file.getPath()).into(binding.iv4);
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
