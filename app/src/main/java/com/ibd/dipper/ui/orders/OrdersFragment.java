package com.ibd.dipper.ui.orders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanUpdata;
import com.ibd.dipper.databinding.FragmentOrdersBinding;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.login.LoginActivity;
import com.ibd.dipper.ui.msg.MsgActivity;
import com.ibd.dipper.ui.setting.about.AboutActivity;
import com.ibd.dipper.uiPopupWindow.CarPopupWindow;
import com.ibd.dipper.uiPopupWindow.RefusePopupWindow;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.LocationUtils;
import com.ibd.dipper.utils.UIUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import okhttp3.ResponseBody;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.ibd.dipper.utils.UIUtils.getPackageName;

public class OrdersFragment extends BaseFragment<FragmentOrdersBinding, OrdersViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_orders;
    }

    @Override
    public int initVariableId() {
        return com.ibd.dipper.BR.viewModel;
    }

    @Override
    public void initData() {
        viewModel.type.setValue(0);
        viewModel.getThisMM();

        Location location = LocationUtils.getLastKnownLocation(getActivity());
        viewModel.longStr.setValue(location != null ? (location.getLongitude() + "") : "111.111111");
        viewModel.latStr.setValue(location != null ? (location.getLatitude() + "") : "111.111111");
        viewModel.page.setValue(1);

        viewModel.setData(getContext());

        binding.refreshLayout.autoLoadMore();
        binding.refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));
        binding.refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        binding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            viewModel.page.setValue(1);
            viewModel.setData(getContext());
        });
        binding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            viewModel.page.setValue((viewModel.page.getValue() + 1));
            viewModel.setData(getContext());
        });

        viewModel.versionName.setValue(AboutActivity.getPackageInfo(getActivity()).versionName);
        viewModel.version();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.observe(this, o -> {
            //结束刷新
            binding.refreshLayout.finishRefresh();
            binding.refreshLayout.setNoMoreData(false);
        });
        //监听上拉加载完成
        viewModel.uc.finishLoadmore.observe(this, o -> {
            //结束刷新
            binding.refreshLayout.finishLoadMore();
        });
        viewModel.uc.finishNoMore.observe(this, o -> {
            //没有更多
            binding.refreshLayout.finishLoadMoreWithNoMoreData();
        });
        viewModel.uc.carListPop.observe(this, o -> {
            CarPopupWindow carPopupWindow = new CarPopupWindow(getContext(), OrdersFragment.class.getName());
            carPopupWindow.showPopupWindow();
        });
        viewModel.uc.loginOut.observe(this, o -> {
            //监听肯定按键
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("提示")//标题
                    .content("token过期，请重新登入")//内容
                    .positiveText("确定") //肯定按键
                    .cancelable(true)
                    .onPositive((dialog1, which) -> {
                        startActivity(LoginActivity.class);
                        getActivity().finish();
                    })
                    .build();
            dialog.show();
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

}
