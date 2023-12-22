package com.ibd.dipper.uiPopupWindow;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ibd.dipper.R;
import com.ibd.dipper.ui.online.OnlineViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import razerdp.basepopup.BasePopupWindow;

public class PhonePopupWindow extends BasePopupWindow {
    public OnlineViewModel onlineViewModel;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_phone)
    Button btnPhone;
    private String Phone;

    public PhonePopupWindow(Context context, OnlineViewModel onlineViewModel, String phone) {
        super(context);
        this.onlineViewModel = onlineViewModel;
        this.Phone = phone;

        tvPhone.setText(phone);
    }

    public PhonePopupWindow(Context context) {
        super(context);
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_phone);
    }

    @Override
    public void onViewCreated(View contentView) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick({R.id.btn_close, R.id.btn_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_phone:
                onlineViewModel.phonePermissions(tvPhone.getText().toString());
                dismiss();
                break;
        }
    }
}
