package com.ibd.dipper.ui.mine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import androidx.lifecycle.ViewModel;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.databinding.FragmentMineBinding;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.carApprove.CarApproveActivity;
import com.ibd.dipper.ui.setting.SettingActivity;
import com.ibd.dipper.ui.setting.about.AboutActivity;
import com.ibd.dipper.uiPopupWindow.CarPopupWindow;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.GlideEngine;
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
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.ImageUtils;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static android.app.Activity.RESULT_OK;
import static com.ibd.dipper.utils.UIUtils.getPackageName;

import static com.ibd.dipper.Config.TOKEN;
import static com.ibd.dipper.Config.USER;
public class MineFragment extends BaseFragment<FragmentMineBinding, MineViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine;
    }

    @Override
    public int initVariableId() {
        return com.ibd.dipper.BR.viewModel;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            viewModel.getUserInfo();
            viewModel.accInfoQuery();
        }, 500);
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.getUserInfo();

        viewModel.versionName.setValue(AboutActivity.getPackageInfo(getActivity()).versionName);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        new Handler().postDelayed(() -> viewModel.isVisibleToUser.setValue(isVisibleToUser),200);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("CheckResult")
    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.creatPhonePop.observe(this, integer -> {
            UploadPopupWindow uploadPopupWindow = new UploadPopupWindow(getActivity());
            uploadPopupWindow.showPopupWindow();
        });
        viewModel.uc.phoneType.observe(this, integer -> {
            switch (integer) {
                case 0:
                    checkCamarePermission();
                    break;
                case 1:
                    PictureSelector.create(MineFragment.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(1)
                            .isCamera(false)
                            .loadImageEngine(GlideEngine.createGlideEngine())
//                                    .setLanguage(finalLanguage)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                    break;
            }
        });
        viewModel.uc.carPopShow.observe(this, o -> {
            CarPopupWindow carPopupWindow = new CarPopupWindow(getContext(), MineFragment.class.getName());
            carPopupWindow.showPopupWindow();
        });
        viewModel.uc.toSettingAc.observe(this, o -> {
            Intent intent = new Intent(MineFragment.this.getContext(), SettingActivity.class);
            if(viewModel.beanUserinfo.getValue()!=null) {
                intent.putExtra("mobile", viewModel.beanUserinfo.getValue().mobile);
                intent.putExtra("name", viewModel.beanUserinfo.getValue().name);
                startActivityForResult(intent, 200);
            }else{
                viewModel.uc.loginOut.call();
                SPUtils.getInstance(USER).put(TOKEN, "");
                RetrofitClient.setNull();
            }
        });
        viewModel.uc.newVersion.observe(this, s -> {
            //监听肯定按键
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("提示")//标题
                    .content("有新版本，是否更新")//内容
                    .positiveText("更新") //肯定按键
                    .negativeText("取消") //否定按键
                    .cancelable(true)
                    .onPositive((dialog1, which) -> {
                        viewModel.download(s);
                    })
                    .onNegative((dialog12, which) -> {
                        dismissDialog();
                    })
                    .build();
            dialog.show();
        });
        viewModel.uc.path.observe(this, s -> {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                start7Install(getActivity(), s);
            } else {
                startInstall(getActivity(), s);
            }
        });
    }

    /**
     * 安装应用
     */
    public void startInstall(Context context, String path) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    /**
     * 7.0安装应用
     *
     * @param context
     */
    public void start7Install(Context context, String path) {
        //在AndroidManifest中的android:authorities值
        Uri apkUri = FileProvider.getUriForFile(context, getPackageName() + ".provider", new File(path));

        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");

        startActivity(install);
    }

    @SuppressLint("CheckResult")
    private void checkCamarePermission() {
        //请求打开相机权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        PictureSelector.create(MineFragment.this)
                                .openCamera(PictureMimeType.ofImage())
                                .loadImageEngine(GlideEngine.createGlideEngine()) // Please refer to the Demo GlideEngine.java
                                .forResult(PictureConfig.REQUEST_CAMERA);
                    } else {
                        ToastUtils.showShort("相机权限被拒绝，请到设置中打开！");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.e(resultCode + "??");

        if (requestCode == 200 & resultCode == 201)
            getActivity().finish();
        if (resultCode == RESULT_OK & viewModel.isVisibleToUser.getValue()) {
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
                viewModel.uploadImg.setValue(file.getPath());
                viewModel.imgHead.setValue(file.getPath());
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
