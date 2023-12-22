package com.ibd.dipper.ui.bankNew;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanBankList;
import com.ibd.dipper.bean.BeanEnum;
import com.ibd.dipper.databinding.ActivityBankNewBinding;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.UIUtils;
import com.leaf.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class BankNewActivity extends BaseActivity<ActivityBankNewBinding, BankNewViewModel> {
    private OptionsPickerView cityOptions;
    private AutoCompleteTextView bankCompleteTextView;
    private AutoCompleteTextView autoCompleteTextView;

    private List<Map<String,String>> currentBankList;
    private List<Map<String,String>> currentAutoList;
    private boolean bankCompleteTextSelected;
    private boolean autoCompleteTextSelected;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_bank_new;
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
        tvTitle.setText(getString(R.string.bank_new));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        viewModel.type.setValue(0);
//        binding.rcv.setLayoutManager(new GridLayoutManager(this, 2));

//        viewModel.bankList();




        bankCompleteTextSelected=false;
        autoCompleteTextSelected=false;

        //银行
        bankCompleteTextView = (AutoCompleteTextView) findViewById(R.id.bank);
        bankCompleteTextView.setFocusable(true);
        bankCompleteTextView.addTextChangedListener(bank_TextChange);
        bankCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                String names=bankCompleteTextView.getText().toString();
                for (Map<String,String> item:currentBankList
                     ) {
                    if(item.get("bankName").equals(names)) {
                        viewModel.bankNo.setValue(item.get("bankNo"));
                        break;
                    }
                }

                bankCompleteTextSelected=true;
            }

        });
        //开户行
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto);
        autoCompleteTextView.addTextChangedListener(auto_TextChange);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
//                String names[]=autoCompleteTextView.getText().toString().split("\\|\\|");
//                autoCompleteTextView.setText(names[1]);
//                viewModel.cityCode.setValue(names[0]);

                String names=autoCompleteTextView.getText().toString();
                for (Map<String,String> item:currentAutoList
                ) {
                    if(item.get("name").equals(names)) {
                        viewModel.cityCode.setValue(item.get("cityCode"));
                        break;
                    }
                }

                autoCompleteTextSelected=true;
            }

        });


    }

    private TextWatcher bank_TextChange=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(bankCompleteTextSelected){
                bankCompleteTextSelected=false;
            }
            initBankComplete(s.toString(), bankCompleteTextView);
        }
    };

    private TextWatcher auto_TextChange=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(autoCompleteTextSelected){
                autoCompleteTextSelected=false;
            }
            initAutoComplete(s.toString(),autoCompleteTextView);
        }
    };

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.cityTypePop.observe(this, o -> {
            cityOptions = new OptionsPickerBuilder(BankNewActivity.this, (options1, options2, options3, v) -> {
                viewModel.cityCode.setValue(viewModel.beanCity.getValue().get(options1).cityCode);
                viewModel.cityName.setValue(viewModel.beanCity.getValue().get(options1).name);
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
            if(viewModel.beanCity.getValue()!=null){
                for (BeanBankList carType : viewModel.beanCity.getValue())
                    list.add(carType.name);
                cityOptions.setNPicker(list, null, null);
                cityOptions.show();
            }

        });
        viewModel.uc.ontickCall.observe(this, s -> {
            binding.tvCode.setClickable(false);
            binding.tvCode.setText(s);
        });
        viewModel.uc.onfinishCall.observe(this, s -> {
            binding.tvCode.setClickable(true);
            binding.tvCode.setText(s);
        });
    }

    @Override
    public void onBackPressed() {
        if (viewModel.type.getValue() == 1)
            viewModel.type.setValue(0);
        else
            finish();
    }

    /**
     * 初始化AutoCompleteTextView，使 AutoCompleteTextView自动提示
     * @param keyword 搜索字符串
     * @param autoCompleteTextView 要操作的AutoCompleteTextView
     */
    @SuppressLint("CheckResult")
    private void initBankComplete(String keyword,
                                  AutoCompleteTextView autoCompleteTextView) {
        Map<String, String> params = new HashMap<>();
        //params.put("bankNo", viewModel.bankNo.getValue());
        params.put("keyword", keyword);
        KLog.e("initAutoComplete keyword" + keyword);
        RetrofitClient.getInstance().create(ApiService.class)
                .bankList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanBankList>>>) response -> {
                    KLog.e("initAutoComplete field2" + keyword);
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        String his = "";
                        List<BeanBankList> list = response.data;
                        currentBankList=new ArrayList<>();
                        for (BeanBankList item : list
                        ) {
                            //his += item.bankNo +"||"+item.bankName + ",";

                            his += item.bankName + ",";

                            Map<String,String> map=new HashMap<>();
                            map.put("bankNo",item.bankNo);
                            map.put("bankName",item.bankName);
                            currentBankList.add(map);
                        }



                        KLog.e("his" + his);
                        String[] histories = his.split(",");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_dropdown_item_1line, histories);
//                        // 只保留最近的50条的记录
//                        if (histories.length > 50) {
//                            String[] newHistories = new String[50];
//                            System.arraycopy(histories, 0, newHistories, 0, 50);
//                            adapter = new ArrayAdapter<String>(this,
//                                    android.R.layout.simple_dropdown_item_1line, newHistories);
//                        }
                        autoCompleteTextView.setAdapter(adapter);
                        if(!bankCompleteTextSelected){
                            autoCompleteTextView.showDropDown();
                        }

                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }


    /**
     * 初始化AutoCompleteTextView，使 AutoCompleteTextView自动提示
     * @param keyword 搜索字符串
     * @param autoCompleteTextView 要操作的AutoCompleteTextView
     */
    @SuppressLint("CheckResult")
    private void initAutoComplete(String keyword,
                                  AutoCompleteTextView autoCompleteTextView) {
        Map<String, String> params = new HashMap<>();
        params.put("bankNo", viewModel.bankNo.getValue());
        params.put("keyword", keyword);
        KLog.e("initAutoComplete keyword" + keyword);
        RetrofitClient.getInstance().create(ApiService.class)
                .cityList(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanBankList>>>) response -> {
                    KLog.e("initAutoComplete field2" + keyword);
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        String his = "";
                        List<BeanBankList> list = response.data;
                        currentAutoList=new ArrayList<>();
                        for (BeanBankList item : list
                        ) {
                            //his += item.cityCode +"||"+item.name + ",";
                            his += item.name + ",";

                            Map<String,String> map=new HashMap<>();
                            map.put("cityCode",item.cityCode);
                            map.put("name",item.name);
                            currentAutoList.add(map);
                        }
                        KLog.e("his" + his);
                        String[] histories = his.split(",");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_dropdown_item_1line, histories);
//                        // 只保留最近的50条的记录
//                        if (histories.length > 50) {
//                            String[] newHistories = new String[50];
//                            System.arraycopy(histories, 0, newHistories, 0, 50);
//                            adapter = new ArrayAdapter<String>(this,
//                                    android.R.layout.simple_dropdown_item_1line, newHistories);
//                        }
                        autoCompleteTextView.setAdapter(adapter);
                        if(!autoCompleteTextSelected){
                            autoCompleteTextView.showDropDown();
                        }

                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }
}
