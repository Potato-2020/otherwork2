package com.ibd.dipper.uiPopupWindow;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ibd.dipper.R;
import com.ibd.dipper.ui.complaint.ComplaintViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import razerdp.basepopup.BasePopupWindow;

public class ComplaintPopupWindow extends BasePopupWindow {
    public ComplaintViewModel complaintViewModel;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    public ComplaintPopupWindow(Context context, ComplaintViewModel complaintViewModel) {
        super(context);
        this.complaintViewModel = complaintViewModel;
    }

    public ComplaintPopupWindow(Context context) {
        super(context);
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_complaint);
    }

    @Override
    public void onViewCreated(View contentView) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick({R.id.btn_commit, R.id.iv_close, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (!etContent.getText().toString().isEmpty()) {
                    complaintViewModel.appealCommit(etContent.getText().toString());
                    dismiss();
                }
                break;
            case R.id.iv_close:
            case R.id.btn_close:
                dismiss();
                break;
        }
    }
}
