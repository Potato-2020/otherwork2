package com.ibd.dipper.uiPopupWindow;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ibd.dipper.R;
import com.ibd.dipper.ui.orders.OrdersViewModel;
import com.ibd.dipper.ui.orders.RefuseEvent;
import com.ibd.dipper.ui.ordersDetail.RefuseDetailEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.goldze.mvvmhabit.bus.RxBus;
import razerdp.basepopup.BasePopupWindow;

public class RefusePopupWindow extends BasePopupWindow {
    private Boolean isDetail;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    public RefusePopupWindow(Context context, Boolean isDetail) {
        super(context);
        this.isDetail = isDetail;
    }

    public RefusePopupWindow(Context context) {
        super(context);
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_refuse);
    }

    @Override
    public void onViewCreated(View contentView) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick({R.id.btn_commit, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (!etContent.getText().toString().isEmpty()) {
                    if (isDetail)
                        RxBus.getDefault().post(new RefuseDetailEvent(etContent.getText().toString()));
                    else
                        RxBus.getDefault().post(new RefuseEvent(etContent.getText().toString()));
                    dismiss();
                }
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }
    }
}
