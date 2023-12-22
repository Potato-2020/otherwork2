package com.ibd.dipper.ui.task;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanOrdersDetail;
import com.ibd.dipper.bean.BeanTask;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class TaskItemViewModel extends ItemViewModel<TaskViewModel> {
    public ObservableField<BeanOrdersDetail> entity = new ObservableField<>();
    public SingleLiveEvent<Integer> type = new SingleLiveEvent<>();

    public TaskItemViewModel(@NonNull TaskViewModel viewModel, BeanOrdersDetail bean, int type) {
        super(viewModel);
        this.entity.set(bean);
        this.type.setValue(type);

        if (type == 0) {
            switch (bean.status) {
                case 2:
                    entity.get().statusStr = "装车";
                    break;
                case 3:
                    entity.get().statusStr = "起运";
                    break;
                case 4:
                    entity.get().statusStr = "卸货";
                    break;
                case 5:
                    entity.get().statusStr = "签收";
                    break;
                case 6:
                    switch (entity.get().checkStatus) {
                        case 0:
                            entity.get().statusStr = "等待确认";
                            entity.get().statusText = "等待确认";
                            break;
                        case 1:
                            //不应出现在未完成列表
                            entity.get().statusStr = "货主确认";
                            entity.get().statusText = "货主确认";
                            break;
                        case 2:
                            entity.get().statusStr = "重新签收";
                            entity.get().statusText = "货主驳回";
                            break;
                        case 3:
                            //不应出现在未完成列表
                            entity.get().statusStr = "平台确认";
                            entity.get().statusText = "平台确认";
                            break;
                        case 4:
                            entity.get().statusStr = "重新签收";
                            entity.get().statusText = "平台驳回";
                            break;
                    }
                    break;
                case 10:
                    entity.get().statusStr = "已撤销";
                    break;
            }
            entity.set(entity.get());
        } else {
            entity.get().statusText = entity.get().checkStatusText;
            entity.set(entity.get());
        }
    }

    /**
     * 详情页
     */
    public View.OnClickListener waybillDetailClick = v -> viewModel.waybillDetail(this);

    /**
     * 未完成点击
     */
    public View.OnClickListener taskDetailClick = v -> {
        if (entity.get().status < 6)
            viewModel.taskDetail(this);
        else {
            switch (entity.get().checkStatus) {
                case 2:
                case 4:
                    viewModel.reSign(this);
                    break;
            }
        }

    };

    /**
     * 已完成点击
     */
    public View.OnClickListener waybillResultDetailClick = v -> {
        switch (v.getId()){
            case R.id.tv_complaint:
                viewModel.complaint(this);
                break;
            case R.id.tv_evaluate:
                viewModel.evaluate(this);
                break;
        }

    };
}
