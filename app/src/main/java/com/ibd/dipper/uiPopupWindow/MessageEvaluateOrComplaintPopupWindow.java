package com.ibd.dipper.uiPopupWindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanEvaluateDetail;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.utils.JsonUtils;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import razerdp.basepopup.BasePopupWindow;

import java.util.HashMap;
import java.util.Map;

public class MessageEvaluateOrComplaintPopupWindow extends BasePopupWindow {
    public String id;
    public int type;
    public Boolean needUp;
    public int speed = -1;
    public int safety = -1;
    public int standard = -1;
    public int service = -1;
    public int attitude = -1;
    public String content;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rb00)
    RadioButton rb00;
    @BindView(R.id.rb01)
    RadioButton rb01;
    @BindView(R.id.rb02)
    RadioButton rb02;
    @BindView(R.id.rb03)
    RadioButton rb03;
    @BindView(R.id.rb04)
    RadioButton rb04;
    @BindView(R.id.radio_group0)
    RadioGroup radioGroup0;
    @BindView(R.id.rb10)
    RadioButton rb10;
    @BindView(R.id.rb11)
    RadioButton rb11;
    @BindView(R.id.rb12)
    RadioButton rb12;
    @BindView(R.id.rb13)
    RadioButton rb13;
    @BindView(R.id.rb14)
    RadioButton rb14;
    @BindView(R.id.radio_group1)
    RadioGroup radioGroup1;
    @BindView(R.id.rb20)
    RadioButton rb20;
    @BindView(R.id.rb21)
    RadioButton rb21;
    @BindView(R.id.rb22)
    RadioButton rb22;
    @BindView(R.id.rb23)
    RadioButton rb23;
    @BindView(R.id.rb24)
    RadioButton rb24;
    @BindView(R.id.radio_group2)
    RadioGroup radioGroup2;

    @BindView(R.id.rb30)
    RadioButton rb30;
    @BindView(R.id.rb31)
    RadioButton rb31;
    @BindView(R.id.rb32)
    RadioButton rb32;
    @BindView(R.id.rb33)
    RadioButton rb33;
    @BindView(R.id.rb34)
    RadioButton rb34;
    @BindView(R.id.radio_group3)
    RadioGroup radioGroup3;

    @BindView(R.id.rb40)
    RadioButton rb40;
    @BindView(R.id.rb41)
    RadioButton rb41;
    @BindView(R.id.rb42)
    RadioButton rb42;
    @BindView(R.id.rb43)
    RadioButton rb43;
    @BindView(R.id.rb44)
    RadioButton rb44;
    @BindView(R.id.radio_group4)
    RadioGroup radioGroup4;

    @BindView(R.id.constraintLayout5)
    ConstraintLayout constraintLayout5;
    @BindView(R.id.tv_done)
    TextView tvDone;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    public MessageEvaluateOrComplaintPopupWindow(Context context, int Type, String id, boolean needUp) {
        super(context);
        this.id = id;
        this.type = Type;
        this.needUp = needUp;

        setView();
    }

    public MessageEvaluateOrComplaintPopupWindow(Context context, int Type, String id, boolean needUp, int speed, int safety, int standard,int service,int attitude) {
        super(context);
        this.id = id;
        this.type = Type;
        this.needUp = needUp;
        this.speed = speed;
        this.safety = safety;
        this.standard = standard;
        this.service = service;
        this.attitude = attitude;

        setData();

        setView();
    }

    public void disableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(false);
        }
    }

    private void setData() {
        switch (speed) {
            case 1:
                rb00.setChecked(true);
                break;
            case 2:
                rb01.setChecked(true);
                break;
            case 3:
                rb02.setChecked(true);
                break;
            case 4:
                rb03.setChecked(true);
                break;
            case 5:
                rb04.setChecked(true);
                break;
        }
        disableRadioGroup(radioGroup0);
        switch (safety) {
            case 1:
                rb10.setChecked(true);
                break;
            case 2:
                rb11.setChecked(true);
                break;
            case 3:
                rb12.setChecked(true);
                break;
            case 4:
                rb13.setChecked(true);
                break;
            case 5:
                rb14.setChecked(true);
                break;
        }
        disableRadioGroup(radioGroup1);
        switch (standard) {
            case 1:
                rb20.setChecked(true);
                break;
            case 2:
                rb21.setChecked(true);
                break;
            case 3:
                rb22.setChecked(true);
                break;
            case 4:
                rb23.setChecked(true);
                break;
            case 5:
                rb24.setChecked(true);
                break;
        }
        disableRadioGroup(radioGroup2);

        switch (service) {
            case 1:
                rb30.setChecked(true);
                break;
            case 2:
                rb31.setChecked(true);
                break;
            case 3:
                rb32.setChecked(true);
                break;
            case 4:
                rb33.setChecked(true);
                break;
            case 5:
                rb34.setChecked(true);
                break;
        }
        disableRadioGroup(radioGroup3);

        switch (attitude) {
            case 1:
                rb40.setChecked(true);
                break;
            case 2:
                rb41.setChecked(true);
                break;
            case 3:
                rb42.setChecked(true);
                break;
            case 4:
                rb43.setChecked(true);
                break;
            case 5:
                rb44.setChecked(true);
                break;
        }
        disableRadioGroup(radioGroup4);
    }


    private void setView() {
        if (type == 0) {
            tvTitle.setText("评价");
            etContent.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setText("投诉");
            etContent.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }
        if (needUp)
            tvDone.setVisibility(View.VISIBLE);
        else
            tvDone.setVisibility(View.GONE);

        radioGroup0.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb00:
                    speed = 1;
                    break;
                case R.id.rb01:
                    speed = 2;
                    break;
                case R.id.rb02:
                    speed = 3;
                    break;
                case R.id.rb03:
                    speed = 4;
                    break;
                case R.id.rb04:
                    speed = 5;
                    break;
            }
        });
        radioGroup1.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb10:
                    safety = 1;
                    break;
                case R.id.rb11:
                    safety = 2;
                    break;
                case R.id.rb12:
                    safety = 3;
                    break;
                case R.id.rb13:
                    safety = 4;
                    break;
                case R.id.rb14:
                    safety = 5;
                    break;
            }
        });
        radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb20:
                    standard = 1;
                    break;
                case R.id.rb21:
                    standard = 2;
                    break;
                case R.id.rb22:
                    standard = 3;
                    break;
                case R.id.rb23:
                    standard = 4;
                    break;
                case R.id.rb24:
                    standard = 5;
                    break;
            }
        });

        radioGroup3.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb30:
                    service = 1;
                    break;
                case R.id.rb31:
                    service = 2;
                    break;
                case R.id.rb32:
                    service = 3;
                    break;
                case R.id.rb33:
                    service = 4;
                    break;
                case R.id.rb34:
                    service = 5;
                    break;
            }
        });

        radioGroup4.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb40:
                    attitude = 1;
                    break;
                case R.id.rb41:
                    attitude = 2;
                    break;
                case R.id.rb42:
                    attitude = 3;
                    break;
                case R.id.rb43:
                    attitude = 4;
                    break;
                case R.id.rb44:
                    attitude = 5;
                    break;
            }
        });


        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                content = s.toString();
            }
        });
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_carrier_evaluate_complaint);
    }

    @Override
    public void onViewCreated(View contentView) {
        ButterKnife.bind(this, contentView);
    }


    @OnClick(R.id.tv_done)
    public void onViewClicked() {
        if (type == 0) {
            if (speed == -1) {
                ToastUtils.showShort("请选择运输速度");
                return;
            }
            if (safety == -1) {
                ToastUtils.showShort("请选择货物安全");
                return;
            }
            if (standard == -1) {
                ToastUtils.showShort("请选择工作规范");
                return;
            }
            if (service == -1) {
                ToastUtils.showShort("请选择服务态度");
                return;
            }
            if (attitude == -1) {
                ToastUtils.showShort("请选择司机态度");
                return;
            }
            taskEvaluate();
        } else {
            taskComplaint();
        }
    }

    /**
     * 评价
     */
    @SuppressLint("CheckResult")
    public void taskEvaluate() {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("speed", speed + "");
        params.put("settlement", safety + "");
        params.put("standard", standard + "");
        params.put("service", service + "");
        params.put("attitude", attitude + "");
        RetrofitClient.getInstance().create(ApiService.class)
                .taskEvaluate(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanEvaluateDetail>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        ToastUtils.showShort("评价成功");
                        dismiss();
                    } else {
                        ToastUtils.showShort(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    /**
     * 投诉
     */
    @SuppressLint("CheckResult")
    public void taskComplaint() {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("content", content);
        RetrofitClient.getInstance().create(ApiService.class)
                .taskComplaint(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<BeanEvaluateDetail>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        ToastUtils.showShort("投诉成功");
                        dismiss();
                    } else {
                        ToastUtils.showShort(response.message);
                    }
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
