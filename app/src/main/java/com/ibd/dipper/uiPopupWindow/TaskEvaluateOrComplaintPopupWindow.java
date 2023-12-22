package com.ibd.dipper.uiPopupWindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanEvaluateDetail;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import razerdp.basepopup.BasePopupWindow;

public class TaskEvaluateOrComplaintPopupWindow extends BasePopupWindow {
    public String id;
    public int type;
    public Boolean needUp;
    public int speed = -1;
    public int settlement = -1;
    public int standard = -1;
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
    @BindView(R.id.constraintLayout5)
    ConstraintLayout constraintLayout5;
    @BindView(R.id.tv_done)
    TextView tvDone;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    public TaskEvaluateOrComplaintPopupWindow(Context context, int Type, String id, boolean needUp) {
        super(context);
        this.id = id;
        this.type = Type;
        this.needUp = needUp;

        setView();
    }

    public TaskEvaluateOrComplaintPopupWindow(Context context, int Type, String id, boolean needUp, int speed, int settlement, int standard) {
        super(context);
        this.id = id;
        this.type = Type;
        this.needUp = needUp;
        this.speed = speed;
        this.settlement = settlement;
        this.standard = standard;

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
        switch (settlement) {
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
                    settlement = 1;
                    break;
                case R.id.rb11:
                    settlement = 2;
                    break;
                case R.id.rb12:
                    settlement = 3;
                    break;
                case R.id.rb13:
                    settlement = 4;
                    break;
                case R.id.rb14:
                    settlement = 5;
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
        return createPopupById(R.layout.pop_task_evaluate_complaint);
    }

    @Override
    public void onViewCreated(View contentView) {
        ButterKnife.bind(this, contentView);
    }


    @OnClick(R.id.tv_done)
    public void onViewClicked() {
        if (type == 0) {
            if (speed == -1) {
                ToastUtils.showShort("请选择回单速度");
                return;
            }
            if (settlement == -1) {
                ToastUtils.showShort("请选择结算时间");
                return;
            }
            if (standard == -1) {
                ToastUtils.showShort("请选择工作规范");
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
        params.put("settlement", settlement + "");
        params.put("standard", standard + "");
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
