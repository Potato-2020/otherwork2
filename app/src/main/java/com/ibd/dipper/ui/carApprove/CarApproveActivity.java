package com.ibd.dipper.ui.carApprove;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.ibd.dipper.R;
import com.ibd.dipper.bean.BeanCar;
import com.ibd.dipper.bean.BeanCarTrailer;
import com.ibd.dipper.bean.BeanEnum;
import com.ibd.dipper.bean.BeanEnums;
import com.ibd.dipper.databinding.ActivityCarApproveBinding;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.uiPopupWindow.UploadPopupWindow;
import com.ibd.dipper.utils.GlideEngine;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.NetworkUtil;
import com.ibd.dipper.utils.UIUtils;
import com.ibd.dipper.utils.Uri2FileTransformEngine;
import com.leaf.library.StatusBarUtil;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.ImageUtils;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;



public class CarApproveActivity extends BaseActivity<ActivityCarApproveBinding, CarApproveViewModel> implements  TextWatcher  {
    private OptionsPickerView energyTypeOptions;
    private OptionsPickerView licensePlateColorOptions;
    private OptionsPickerView licensePlateTypeOptions;
    private OptionsPickerView vehicleTypeOptions;

    static private LinearLayout trailerBoxLinearLayout;//对应于主布局中用来添加子布局的View
    private View trailerView;// 子Layout要以view的形式加入到主Layout


    private AutoCompleteTextView autoCompleteTextView;

//    static Handler setUrlHandle;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_car_approve;
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
        tvTitle.setText(getString(R.string.car_approve_str));
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(v -> onBackPressed());

        viewModel.otype.setValue(Integer.parseInt(getIntent().getStringExtra("otype")));
        viewModel.beTralier.setValue(false);
        viewModel.beTralierClickable.setValue(true);
        binding.tvDenialReason.setVisibility(View.GONE);
        switch (getIntent().getStringExtra("otype")) {
            case "0":
                binding.btnCommit.setVisibility(View.GONE);
                viewModel.beanCar.setValue((BeanCar) getIntent().getSerializableExtra("data"));


                new Handler().postDelayed(() -> {
                    if (viewModel.beTralier.getValue()) {
                        KLog.e(viewModel.beTralier.getValue());
                        binding.btnCommit.setVisibility(View.VISIBLE);
                        binding.btnCommit.setText("修改挂车信息");
                        viewModel.beTralierClickable.setValue(false);
                    }
                }, 200);
                break;
            case "1"://车辆认证
                binding.btnCommit.setText("提交认证");
                break;
            case "2"://车辆重新认
                viewModel.beanCar.setValue((BeanCar) getIntent().getSerializableExtra("data"));
                binding.btnCommit.setText("重新认证");
                binding.tvDenialReason.setVisibility(View.VISIBLE);
                break;
            case "3"://修改认证信息
                viewModel.beanCar.setValue((BeanCar) getIntent().getSerializableExtra("data"));
                binding.btnCommit.setText("修改认证");
                break;
        }


        viewModel.energyType();
        viewModel.licensePlateColor();
        viewModel.licensePlateType();


        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto);
        autoCompleteTextView.addTextChangedListener(this);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                String names[]=autoCompleteTextView.getText().toString().split("\\|\\|");
//                KLog.e("names[0]" + names[0]);
//                KLog.e("names[1]" + names[1]);
                autoCompleteTextView.setText(names[1]);
                viewModel.vehicleTypeStr.setValue(names[1]);

                viewModel.beTralier.setValue(names[1].contains("牵引"));
            }

        });

        //设置焦点
        TextView licensePlateNumber = findViewById(R.id.license_plate_number);
        licensePlateNumber.setFocusable(true);
        licensePlateNumber.setFocusableInTouchMode(true);
        licensePlateNumber.requestFocus();




        BeanCar car=(BeanCar) getIntent().getSerializableExtra("data");
        if(car==null){
            viewModel.trailerList.setValue(new ArrayList<>());
        }else{
            viewModel.trailerList.setValue(car.trailerList);
            trailerBoxLinearLayout = (LinearLayout)findViewById(R.id.trailer_box);
            Integer index=0;
            for (BeanCarTrailer trailer:car.trailerList
            ) {
                //初始化挂车布局
                trailerView = View.inflate(this,R.layout.acitvity_car_approve_trailer,null);
                EditText id=trailerView.findViewById(R.id.id);
                ImageView trailerLicenseAuxiliaryFrontPhotoImage=trailerView.findViewById(R.id.trailerLicenseAuxiliaryFrontPhoto);
                ImageView trailerLicenseAuxiliaryBackPhotoImage=trailerView.findViewById(R.id.trailerLicenseAuxiliaryBackPhoto);
                EditText trailerLicensePlateView=trailerView.findViewById(R.id.trailerLicensePlate);
                EditText trailerLoadsView=trailerView.findViewById(R.id.trailerLoads);
                EditText trailerLengthView=trailerView.findViewById(R.id.trailerLength);
                EditText trailerWidthView=trailerView.findViewById(R.id.trailerWidth);
                EditText trailerHeightView=trailerView.findViewById(R.id.trailerHeight);
                EditText trailerTotalMassView=trailerView.findViewById(R.id.trailerTotalMass);
                //挂车布局写入值
                id.setText(trailer.id);
//                trailerFrontPhotoImage=trailerLicenseAuxiliaryFrontPhotoImage;
//                trailerBackPhotoImage=trailerLicenseAuxiliaryBackPhotoImage;

                 Handler mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Bundle data=msg.getData();
                        byte[] imgdata= (byte[]) data.get("imgdata");
                        Bitmap bitmap= BitmapFactory.decodeByteArray(imgdata,0,imgdata.length);
                        //int count=trailerBoxLinearLayout.getChildCount();
                        //for (int i = 0; i < count; i++) {
//                View view = trailerBoxLinearLayout.getChildAt(0);
//                if (view instanceof ViewGroup) {
//                    ImageView a= (ImageView) ((ViewGroup) view).getChildAt(0);
//                    a.setImageBitmap(bitmap);
//                }
                        //}

                        trailerLicenseAuxiliaryFrontPhotoImage.setImageBitmap(bitmap);
                        switch (msg.what) {

                        }
                    }
                };

                  Handler mHandler2 = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Bundle data=msg.getData();
                        byte[] imgdata= (byte[]) data.get("imgdata");
                        Bitmap bitmap= BitmapFactory.decodeByteArray(imgdata,0,imgdata.length);
                        trailerLicenseAuxiliaryBackPhotoImage.setImageBitmap(bitmap);
                        switch (msg.what) {

                        }
                    }
                };


                NetworkUtil.getImage(mHandler,trailer.trailerLicenseAuxiliaryFrontPhoto,5);
                NetworkUtil.getImage(mHandler2,trailer.trailerLicenseAuxiliaryBackPhoto,5);
                trailerLicensePlateView.setText(trailer.trailerLicensePlate);
                trailerLoadsView.setText(trailer.trailerLoads);
                trailerLengthView.setText(trailer.trailerLength);
                trailerWidthView.setText(trailer.trailerWidth);
                trailerHeightView.setText(trailer.trailerHeight);
                trailerTotalMassView.setText(trailer.trailerTotalMass);
                //添加挂车布局到主布局
                trailerBoxLinearLayout.addView(trailerView);

//                CarApproveTrailerViewModel.UIChangeObservable
                Integer finalIndex = index;
                trailerLicenseAuxiliaryFrontPhotoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getLayoutParams();
                        viewModel.listindex.setValue(finalIndex);
                        viewModel.uc.creatPhonePop.setValue(3);
                    }
                });

                trailerLicenseAuxiliaryBackPhotoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.getLayoutParams();
                        viewModel.listindex.setValue(finalIndex);
                        viewModel.uc.creatPhonePop.setValue(4);
                    }
                });

                index++;
            }
        }



        //子布局
//        mGridView = View.inflate(this,R.layout.acitvity_car_approve_trailer,null);
//        mLinearLayout = (LinearLayout)findViewById(R.id.trailer_box);
//        mLinearLayout.addView(mGridView);
    }

    static  ImageView trailerFrontPhotoImage;
    static  ImageView trailerBackPhotoImage;
    static Integer currentIndex;

    static Handler mHandler3 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data=msg.getData();
            byte[] imgdata= (byte[]) data.get("imgdata");
            Bitmap bitmap= BitmapFactory.decodeByteArray(imgdata,0,imgdata.length);
            //int count=trailerBoxLinearLayout.getChildCount();
            //for (int i = 0; i < count; i++) {
                View view = trailerBoxLinearLayout.getChildAt(currentIndex);
                if (view instanceof ViewGroup) {
                    ImageView a= view.findViewById(R.id.trailerLicenseAuxiliaryFrontPhoto);;
                    a.setImageBitmap(bitmap);
                }
            //}

            //trailerFrontPhotoImage.setImageBitmap(bitmap);
//            switch (msg.what) {
//
//            }
        }
    };

    static Handler mHandler4 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data=msg.getData();
            byte[] imgdata= (byte[]) data.get("imgdata");
            Bitmap bitmap= BitmapFactory.decodeByteArray(imgdata,0,imgdata.length);
            View view = trailerBoxLinearLayout.getChildAt(currentIndex);
            if (view instanceof ViewGroup) {

                ImageView a= view.findViewById(R.id.trailerLicenseAuxiliaryBackPhoto);;
                a.setImageBitmap(bitmap);
            }
        }
    };

     static Handler setUrlHandle= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data=msg.getData();
            String url= (String) data.get("url");
            currentIndex= (Integer) data.get("listindex");


            switch (msg.what) {
                case 1:
                    NetworkUtil.getImage(mHandler3,url,5);
                    break;
                case 2:
                    NetworkUtil.getImage(mHandler4,url,5);
                    break;
            }
        }
    };



    public void addTrailer(View view) {
        trailerView = View.inflate(this,R.layout.acitvity_car_approve_trailer,null);
        trailerBoxLinearLayout = (LinearLayout)findViewById(R.id.trailer_box);
        trailerBoxLinearLayout.addView(trailerView);
    }



    /**
     * 车辆认证 提交
     */
    //@SuppressLint("CheckResult")
    public void vehicleAuthentication(View view) {

        if (TextUtils.isEmpty(viewModel.licenseMain.getValue())) {
            ToastUtils.showShort("请上传行驶证（正页）图片");
            return;
        }
        if (TextUtils.isEmpty(viewModel.licenseFront.getValue())) {
            ToastUtils.showShort("请上传行驶证副页图片");
            return;
        }
        if (TextUtils.isEmpty(viewModel.transportCertificate.getValue())) {
            ToastUtils.showShort("请上传道路运输证图片");
            return;
        }
        if (TextUtils.isEmpty(viewModel.energyType.getValue())) {
            ToastUtils.showShort("请选择能源类型");
            return;
        }
        if (TextUtils.isEmpty(viewModel.licensePlateColor.getValue())) {
            ToastUtils.showShort("请选择车牌颜色");
            return;
        }
        if (TextUtils.isEmpty(viewModel.licensePlateType.getValue())) {
            ToastUtils.showShort("请选择牌照类型");
            return;
        }
        if (TextUtils.isEmpty(viewModel.licensePlateNumber.getValue())) {
            ToastUtils.showShort("请输入车牌号码");
            return;
        }
        if (TextUtils.isEmpty(viewModel.loads.getValue())) {
            ToastUtils.showShort("请输入车辆载重");
            return;
        }
        if (TextUtils.isEmpty(viewModel.transportCertificateNumber.getValue())) {
            ToastUtils.showShort("请输入道路运输证号");
            return;
        }
        if (TextUtils.isEmpty(viewModel.fileNumber.getValue())) {
            ToastUtils.showShort("请上输入档案编号");
            return;
        }
        if (TextUtils.isEmpty(viewModel.totalMass.getValue())) {
            ToastUtils.showShort("请输入总质量");
            return;
        }
        if (TextUtils.isEmpty(viewModel.approvedLoad.getValue())) {
            ToastUtils.showShort("请输入核定载重量");
            return;
        }
        if (TextUtils.isEmpty(viewModel.length.getValue())) {
            ToastUtils.showShort("请输入车长");
            return;
        }
        if (TextUtils.isEmpty(viewModel.width.getValue())) {
            ToastUtils.showShort("请输入车宽");
            return;
        }
        if (TextUtils.isEmpty(viewModel.height.getValue())) {
            ToastUtils.showShort("请输入车高");
            return;
        }
        if (TextUtils.isEmpty(viewModel.vehicleType.getValue()) && TextUtils.isEmpty(viewModel.vehicleTypeStr.getValue())) {
            ToastUtils.showShort("请选择车辆类型");
            return;
        }



        Map<String, Object> params = new HashMap<>();
        params.put("otype", viewModel.otype.getValue() + "");
        params.put("id", viewModel.id.getValue());
        params.put("licenseMain", viewModel.licenseMain.getValue());
        params.put("licenseFront", viewModel.licenseFront.getValue());
        params.put("transportCertificate", viewModel.transportCertificate.getValue());
        params.put("energyType", viewModel.energyType.getValue());
        params.put("licensePlateColor", viewModel.licensePlateColor.getValue());
        params.put("licensePlateType", viewModel.licensePlateType.getValue());
        params.put("licensePlateNumber", viewModel.licensePlateNumber.getValue());
        params.put("loads", viewModel.loads.getValue());
        params.put("transportCertificateNumber", viewModel.transportCertificateNumber.getValue());
        params.put("fileNumber", viewModel.fileNumber.getValue());
        params.put("totalMass", viewModel.totalMass.getValue());
        params.put("approvedLoad", viewModel.approvedLoad.getValue());
        params.put("width", viewModel.width.getValue());
        params.put("height", viewModel.height.getValue());
        params.put("type", viewModel.vehicleType.getValue());
        params.put("typeStr", viewModel.vehicleTypeStr.getValue());
//        params.put("trailerLicensePlate", viewModel.trailerLicensePlate.getValue());
//        params.put("trailerLicenseFront", viewModel.trailerLicenseFront.getValue());
//        params.put("trailerLicenseBack", viewModel.trailerLicenseBack.getValue());
//        params.put("trailerLoads", viewModel.trailerLoads.getValue());
//        params.put("trailerLength", viewModel.trailerLength.getValue());
//        params.put("trailerWidth", viewModel.trailerWidth.getValue());
//        params.put("trailerHeight", viewModel.trailerHeight.getValue());
//        params.put("trailerTotalMass", viewModel.trailerTotalMass.getValue());
        params.put("length", viewModel.length.getValue());


        if(trailerBoxLinearLayout!=null) {
            int childCount = trailerBoxLinearLayout.getChildCount();
            //遍历下面所有的子控件，判断是否是layout
            List<Map<String, Object>> trailerList = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                if (trailerBoxLinearLayout.getChildAt(i) instanceof LinearLayout ||
                        trailerBoxLinearLayout.getChildAt(i) instanceof RelativeLayout ||
                        trailerBoxLinearLayout.getChildAt(i) instanceof TableLayout ||
                        trailerBoxLinearLayout.getChildAt(i) instanceof AbsoluteLayout) {
                    //操作代码
                    Map<String, Object> trailer = new HashMap<>();
                    EditText id = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.id);
                    ImageView trailerLicenseAuxiliaryFrontPhoto = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerLicenseAuxiliaryFrontPhoto);
                    ImageView trailerLicenseAuxiliaryBackPhoto = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerLicenseAuxiliaryBackPhoto);
                    EditText trailerLicensePlate = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerLicensePlate);
                    EditText trailerLoads = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerLoads);
                    EditText trailerLength = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerLength);
                    EditText trailerWidth = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerWidth);
                    EditText trailerHeight = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerHeight);
                    EditText trailerTotalMass = trailerBoxLinearLayout.getChildAt(i).findViewById(R.id.trailerTotalMass);

//                if (TextUtils.isEmpty(trailerLicenseAuxiliaryFrontPhoto.imageu()[0].getURL())) {
//                    ToastUtils.showShort("请上传挂车行驶证正页图片");
//                    return;
//                }
//                if (TextUtils.isEmpty(trailerLicenseAuxiliaryBackPhoto.get.get.getValue())) {
//                    ToastUtils.showShort("请上传挂车行驶证副页图片");
//                    return;
//                }
                    if (TextUtils.isEmpty(trailerLicensePlate.getText())) {
                        ToastUtils.showShort("请输入挂车牌照");
                        return;
                    }
                    if (TextUtils.isEmpty(trailerLoads.getText())) {
                        ToastUtils.showShort("请输入挂车载重量");
                        return;
                    }
                    if (TextUtils.isEmpty(trailerLength.getText())) {
                        ToastUtils.showShort("请输入挂车长度");
                        return;
                    }
                    if (TextUtils.isEmpty(trailerWidth.getText())) {
                        ToastUtils.showShort("请输入挂车宽度");
                        return;
                    }
                    if (TextUtils.isEmpty(trailerHeight.getText())) {
                        ToastUtils.showShort("请输入挂车高度");
                        return;
                    }
                    if (TextUtils.isEmpty(trailerTotalMass.getText())) {
                        ToastUtils.showShort("请输入挂车总质量");
                        return;
                    }

                    List<BeanCarTrailer> trailers = viewModel.trailerList.getValue();

                    trailer.put("id", id.getText().toString());
                    trailer.put("trailerLicenseAuxiliaryFrontPhoto", trailers.get(i).trailerLicenseAuxiliaryFrontPhoto);
                    trailer.put("trailerLicenseAuxiliaryBackPhoto", trailers.get(i).trailerLicenseAuxiliaryBackPhoto);
                    trailer.put("trailerLicensePlate", trailerLicensePlate.getText().toString());
                    trailer.put("trailerLoads", trailerLoads.getText().toString());
                    trailer.put("trailerLength", trailerLength.getText().toString());
                    trailer.put("trailerWidth", trailerWidth.getText().toString());
                    trailer.put("trailerHeight", trailerHeight.getText().toString());
                    trailer.put("trailerTotalMass", trailerTotalMass.getText().toString());

                    trailerList.add(trailer);
                }
            }
            params.put("trailerList", trailerList);
        }

        RetrofitClient.getInstance().create(ApiService.class)
                .vehicleAuthentication(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnums>>>) response -> {
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        viewModel.uc.approveSuccess.call();
                        ToastUtils.showLong(response.message);
                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        initAutoComplete(autoCompleteTextView.getText().toString(),autoCompleteTextView);

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
        params.put("keyword", keyword);
        KLog.e("initAutoComplete keyword" + keyword);
        RetrofitClient.getInstance().create(ApiService.class)
                .vehicleType(params)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .subscribe((Consumer<BaseResponse<List<BeanEnum>>>) response -> {
                    KLog.e("initAutoComplete field2" + keyword);
                    KLog.e(JsonUtils.toJson(response));
                    if (response.isOk()) {
                        String his = "";
                        List<BeanEnum> list = response.data;
                        for (BeanEnum item : list
                        ) {
                            his += item.id +"||"+item.name + ",";
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
                        if(list.size()>1){
                            autoCompleteTextView.showDropDown();
                        }

                    } else
                        ToastUtils.showLong(response.message);
                }, (Consumer<ResponseThrowable>) throwable -> {
                    ToastUtils.showLong(throwable.message);
                });
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.beanCar.observe(this, beanCar -> {
            viewModel.id.setValue(beanCar.id);
            viewModel.licenseMain.setValue(beanCar.licenseMain);
            viewModel.licenseFront.setValue(beanCar.licenseFront);
            viewModel.transportCertificate.setValue(beanCar.transportCertificate);
            viewModel.energyType.setValue(beanCar.energyType);
            viewModel.energyTypeStr.setValue(beanCar.energyTypeText);
            viewModel.licensePlateType.setValue(beanCar.licensePlateType);
            viewModel.licensePlateTypeStr.setValue(beanCar.licensePlateTypeText);
            viewModel.licensePlateColor.setValue(beanCar.licensePlateColor);
            viewModel.licensePlateColorStr.setValue(beanCar.licensePlateColorText);
            viewModel.vehicleType.setValue(beanCar.type);
            viewModel.vehicleTypeStr.setValue(beanCar.typeText);
            viewModel.beTralier.setValue(beanCar.typeText.contains("牵引"));

            viewModel.licensePlateNumber.setValue(beanCar.licensePlateNumber);
            viewModel.loads.setValue(beanCar.loads);
            viewModel.transportCertificateNumber.setValue(beanCar.transportCertificateNumber);
            viewModel.fileNumber.setValue(beanCar.fileNumber);
            viewModel.totalMass.setValue(beanCar.totalMass);
            viewModel.approvedLoad.setValue(beanCar.approvedLoad);
            viewModel.length.setValue(beanCar.length);
            viewModel.width.setValue(beanCar.width);
            viewModel.height.setValue(beanCar.height);

//            viewModel.trailerLicensePlate.setValue(beanCar.trailerLicensePlate);
//            viewModel.trailerLicenseFront.setValue(beanCar.trailerLicenseFront);
//            viewModel.trailerLicenseBack.setValue(beanCar.trailerLicenseBack);
//            viewModel.trailerLoads.setValue(beanCar.trailerLoads);
//            viewModel.trailerLength.setValue(beanCar.trailerLength);
//            viewModel.trailerWidth.setValue(beanCar.trailerWidth);
//            viewModel.trailerHeight.setValue(beanCar.trailerHeight);
//            viewModel.trailerTotalMass.setValue(beanCar.trailerTotalMass);

            viewModel.strDenialReason.setValue(beanCar.certificationReason);

            viewModel.trailerList.setValue(beanCar.trailerList);
        });
        viewModel.uc.approveSuccess.observe(this, o -> {
            setResult(101);
            finish();
        });
        viewModel.uc.creatPhonePop.observe(this, integer -> {
            UploadPopupWindow uploadPopupWindow = new UploadPopupWindow(CarApproveActivity.this);
            uploadPopupWindow.showPopupWindow();
        });
        viewModel.uc.phoneType.observe(this, integer -> {
            switch (integer) {
                case 0:
                    checkCamarePermission();
                    break;
                case 1:
                    PictureSelector.create(CarApproveActivity.this)
                            .openGallery(SelectMimeType.ofImage())
                            .setMaxSelectNum(1)
                            .isDisplayCamera(false)
                            .setImageEngine(GlideEngine.createGlideEngine())
                            .setSandboxFileEngine(new Uri2FileTransformEngine())
                            .forResult(new OnResultCallbackListener<>() {
                                @Override
                                public void onResult(ArrayList<LocalMedia> result) {
                                    compressWithRx(result);
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                    break;
            }
        });
        viewModel.uc.energyTypePop.observe(this, o -> {
            energyTypeOptions = new OptionsPickerBuilder(CarApproveActivity.this, (options1, options2, options3, v) -> {
                viewModel.energyType.setValue(viewModel.energyTypeList.getValue().get(options1).id + "");
                viewModel.energyTypeStr.setValue(viewModel.energyTypeList.getValue().get(options1).name);
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
            for (BeanEnum carType : viewModel.energyTypeList.getValue())
                list.add(carType.name);
            energyTypeOptions.setNPicker(list, null, null);
            energyTypeOptions.show();
        });

        viewModel.uc.licensePlateColorPop.observe(this, o -> {
            licensePlateColorOptions = new OptionsPickerBuilder(CarApproveActivity.this, (options1, options2, options3, v) -> {
                viewModel.licensePlateColor.setValue(viewModel.licensePlateColorList.getValue().get(options1).id + "");
                viewModel.licensePlateColorStr.setValue(viewModel.licensePlateColorList.getValue().get(options1).name);
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
            for (BeanEnums carType : viewModel.licensePlateColorList.getValue())
                list.add(carType.name);
            licensePlateColorOptions.setNPicker(list, null, null);
            licensePlateColorOptions.show();
        });

        viewModel.uc.licensePlateTypePop.observe(this, o -> {
            licensePlateTypeOptions = new OptionsPickerBuilder(CarApproveActivity.this, (options1, options2, options3, v) -> {
                viewModel.licensePlateType.setValue(viewModel.licensePlateTypeList.getValue().get(options1).id + "");
                viewModel.licensePlateTypeStr.setValue(viewModel.licensePlateTypeList.getValue().get(options1).name);
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
            for (BeanEnums carType : viewModel.licensePlateTypeList.getValue())
                list.add(carType.name);
            licensePlateTypeOptions.setNPicker(list, null, null);
            licensePlateTypeOptions.show();
        });

//        viewModel.uc.vehicleTypePop.observe(this, o -> {
//            vehicleTypeOptions = new OptionsPickerBuilder(CarApproveActivity.this, (options1, options2, options3, v) -> {
//                viewModel.vehicleType.setValue(viewModel.vehicleTypeList.getValue().items.get(options1).children.get(options2).value + "");
//                viewModel.vehicleTypeStr.setValue(viewModel.vehicleTypeList.getValue().items.get(options1).children.get(options2).label);
//                viewModel.beTralier.setValue(viewModel.vehicleTypeList.getValue().items.get(options1).children.get(options2).label.contains("挂"));
//            }).setSubmitText("确定")//确定按钮文字
//                    .setCancelText("取消")//取消按钮文字
//                    .setTitleText("城市选择")//标题
//                    .setSubCalSize(18)//确定和取消文字大小
//                    .setTitleSize(0)//标题文字大小
//                    .setTitleColor(Color.BLACK)//标题文字颜色
//                    .setSubmitColor(Color.parseColor("#FFB244"))//确定按钮文字颜色
//                    .setCancelColor(Color.GRAY)//取消按钮文字颜色
//                    .setTitleBgColor(getApplication().getResources().getColor(R.color.white))//标题背景颜色 Night mode
//                    .setBgColor(getApplication().getResources().getColor(R.color.white))//滚轮背景颜色 Night mode
//                    .setDividerColor(Color.parseColor("#EFEFEE"))
//                    .setContentTextSize(18)//滚轮文字大小
//                    .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                    .setCyclic(false, false, false)//循环与否
//                    .setSelectOptions(0, 0, 0)  //设置默认选中项
//                    .setOutSideCancelable(true)//点击外部dismiss default true
//                    .isDialog(false)//是否显示为对话框样式
//                    .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
//                    .build();
//
//            ArrayList<String> list = new ArrayList<>();
//            ArrayList<List<String>> cityList = new ArrayList<>();
//            for (int i = 0; i < viewModel.vehicleTypeList.getValue().items.size(); i++) {
//                list.add(viewModel.vehicleTypeList.getValue().items.get(i).label);
//                ArrayList<String> list1 = new ArrayList<>();
//                for (int j = 0; j < viewModel.vehicleTypeList.getValue().items.get(i).children.size(); j++) {
//                    list1.add(viewModel.vehicleTypeList.getValue().items.get(i).children.get(j).label);
//                }
//                cityList.add(list1);
//            }
//
//            vehicleTypeOptions.setPicker(list, cityList, null);
//            vehicleTypeOptions.show();
//        });
    }

    @SuppressLint("CheckResult")
    private void checkCamarePermission() {
        //请求打开相机权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        PictureSelector.create(this)
                                .openCamera(SelectMimeType.ofImage())
                                .setSandboxFileEngine(new Uri2FileTransformEngine())
                                .forResult(new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(ArrayList<LocalMedia> result) {
                                        compressWithRx(result);
                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });
                    } else {
                        ToastUtils.showShort("相机权限被拒绝，请到设置中打开！");
                    }
                });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            compressWithRx(PictureSelector.obtainMultipleResult(data));
//        }
//    }

    private void compressWithRx(List<LocalMedia> selectList) {
        List<String> files = new ArrayList<>();
        for (LocalMedia localMedia : selectList) {
            files.add(localMedia.getAvailablePath());
        }
        ImageUtils.compressWithRx(files, new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                File file = (File) o;
                viewModel.uploadImg.setValue(file.getPath());
                switch (viewModel.uc.creatPhonePop.getValue()) {
                    case 0:
                        viewModel.licenseMain.setValue(file.getPath());
                        break;
                    case 1:
                        viewModel.licenseFront.setValue(file.getPath());
                        break;
                    case 2:
                        viewModel.transportCertificate.setValue(file.getPath());
                        break;
//                    case 3:
//                        viewModel.trailerLicenseFront.setValue(file.getPath());
//                        break;
//                    case 4:
//                        viewModel.trailerLicenseBack.setValue(file.getPath());
//                        break;
                }
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
