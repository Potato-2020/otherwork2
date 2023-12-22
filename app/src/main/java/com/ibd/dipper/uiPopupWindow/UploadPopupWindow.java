package com.ibd.dipper.uiPopupWindow;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ibd.dipper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.goldze.mvvmhabit.bus.RxBus;
import razerdp.basepopup.BasePopupWindow;

public class UploadPopupWindow extends BasePopupWindow {
    @BindView(R.id.tv_camera)
    TextView tvCamera;
    @BindView(R.id.tv_photo)
    TextView tvPhoto;
    @BindView(R.id.tv_dismiss)
    TextView tvDismiss;

    public UploadPopupWindow(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_upload);
    }

    @Override
    public void onViewCreated(View contentView) {
        ButterKnife.bind(this, contentView);
    }

    @OnClick({R.id.tv_camera, R.id.tv_photo, R.id.tv_dismiss})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_camera:
                RxBus.getDefault().post(new ClickResult(0));
                dismiss();
                break;
            case R.id.tv_photo:
                RxBus.getDefault().post(new ClickResult(1));
                dismiss();
                break;
            case R.id.tv_dismiss:
                dismiss();
                break;
        }
    }

    public class ClickResult {
        public int type;

        public ClickResult(int type) {
            this.type = type;
        }
    }
}
