package com.ibd.dipper.net;


import com.ibd.dipper.bean.*;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by goldze on 2017/6/15.
 */

public interface ApiService {
    /**
     *
     */
    @POST("other/shipperList")
    Observable<BaseResponse<BeanShipper>> shipperList(@Body Map<String, String> queryParams);

    /**
     * 获取验证码接口
     */
    @POST("getCode")
    Observable<BaseResponse<BeanCode>> getCode(@Body Map<String, String> queryParams);

    /**
     * 注册接口
     */
    @POST("regist")
    Observable<BaseResponse<BeanRegist>> regist(@Body Map<String, String> queryParams);

    /**
     * 登录
     */
    @POST("login")
    Observable<BaseResponse<BeanLogin>> login(@Body Map<String, String> queryParams);

    /**
     * 忘记密码
     */
    @POST("resetPassword")
    Observable<BaseResponse<BeanForgetPsd>> resetPassword(@Body Map<String, String> queryParams);

    /**
     * 修改密码
     */
    @POST("changePassword")
    Observable<BaseResponse<BeanForgetPsd>> changePassword(@Body Map<String, String> queryParams);

    /**
     * 委托列表
     */
    @POST("order/dispatchList")
    Observable<BaseResponse<BeanOrders>> dispatchList(@Body Map<String, String> queryParams);

    /**
     * 抢单列表
     */
    @POST("order/biddingList")
    Observable<BaseResponse<BeanOrders>> biddingList(@Body Map<String, String> queryParams);

    /**
     * 已认证车辆列表
     */
    @POST("myvehicleList")
    Observable<BaseResponse<BeanCarList>> myVehicleList(@Body Map<String, String> queryParams);

    /**
     * 车辆认证信息列表
     */
    @POST("vehicleAuthenticationList")
    Observable<BaseResponse<BeanCarList>> vehicleAuthenticationList(@Body Map<String, String> queryParams);

    /**
     * 订单详情
     */
    @POST("order/detail")
    Observable<BaseResponse<BeanOrdersDetail>> detail(@Body Map<String, String> queryParams);

    /**
     * 拒绝订单
     */
    @POST("order/refuse")
    Observable<BaseResponse<String>> refuse(@Body Map<String, String> queryParams);

    /**
     * 接受订单
     */
    @POST("order/taking")
    Observable<BaseResponse<String>> taking(@Body Map<String, String> queryParams);

    /**
     * 抢单
     */
    @POST("order/bidding")
    Observable<BaseResponse<String>> bidding(@Body Map<String, String> queryParams);

    /**
     * 任务-已接受
     */
    @POST("task/taskList")
    Observable<BaseResponse<BeanOrders>> taskList(@Body Map<String, String> queryParams);

    /**
     * 任务-已接受
     */
    @POST("task/endList")
    Observable<BaseResponse<BeanOrders>> endList(@Body Map<String, String> queryParams);

    /**
     * 装车
     */
    @POST("task/loading")
    Observable<BaseResponse<String>> loading(@Body Map<String, String> queryParams);

    /**
     * 起运
     */
    @POST("task/shipment")
    Observable<BaseResponse<String>> shipment(@Body Map<String, String> queryParams);

    /**
     * 卸货
     */
    @POST("task/dischargeCargo")
    Observable<BaseResponse<String>> dischargeCargo(@Body Map<String, String> queryParams);

    /**
     * 签收
     */
    @POST("task/sign")
    Observable<BaseResponse<String>> sign(@Body Map<String, String> queryParams);

    /**
     * 重新签收
     */
    @POST("task/reSign")
    Observable<BaseResponse<String>> reSign(@Body Map<String, String> queryParams);

    /**
     * 评价内容
     */
    @POST("task/evaluateDetail")
    Observable<BaseResponse<BeanEvaluateDetail>> evaluateDetail(@Body Map<String, String> queryParams);

    /**
     * 评价内容 司机被评价
     */
    @POST("task/carrierEvaluateDetail")
    Observable<BaseResponse<BeanCarrierEvaluateDetail>> carrierEvaluateDetail(@Body Map<String, String> queryParams);

    /**
     * 评价
     */
    @POST("task/evaluate")
    Observable<BaseResponse<String>> taskEvaluate(@Body Map<String, String> queryParams);

    /**
     * 投诉
     */
    @POST("task/complaint")
    Observable<BaseResponse<String>> taskComplaint(@Body Map<String, String> queryParams);

    /**
     * 修改订单
     */
    @POST("task/edit")
    Observable<BaseResponse<String>> edit(@Body Map<String, String> queryParams);

    /**
     * 运单详情
     */
    @POST("task/detail")
    Observable<BaseResponse<BeanTaskDetail>> taskDetail(@Body Map<String, String> queryParams);

    /**
     * 账单列表
     */
    @POST("bill/list")
    Observable<BaseResponse<BeanBill>> billList(@Body Map<String, String> queryParams);

    /**
     * 账单列表
     */
    @POST("bill/notPaymentList")
    Observable<BaseResponse<BeanBill>> notPaymentList(@Body Map<String, String> queryParams);

    /**
     * 用户信息
     */
    @POST("userInfo")
    Observable<BaseResponse<BeanUserinfo>> userInfo(@Body Map<String, String> queryParams);

    /**
     * 上传图片
     */
    @Multipart
    @POST("storage/create")
    Observable<BaseResponse<BeanUpload>> create(@Part List<MultipartBody.Part> partLis);

    /**
     * 司机已认证信息
     */
    @POST("authenticationInfo")
    Observable<BaseResponse<BeanDriver>> authenticationInfo(@Body Map<String, String> queryParams);

    /**
     * 司机认证
     */
    @POST("authentication")
    Observable<BaseResponse<String>> authentication(@Body Map<String, String> queryParams);

    /**
     * 准驾车型列表
     */
    @POST("dic/drivingType")
    Observable<BaseResponse<List<BeanEnum>>> drivingType(@Body Map<String, String> queryParams);

    /**
     * 能源类型
     */
    @POST("dic/energyType")
    Observable<BaseResponse<List<BeanEnum>>> energyType(@Body Map<String, String> queryParams);

    /**
     * 牌照颜色
     */
    @POST("dic/licensePlateColor")
    Observable<BaseResponse<List<BeanEnums>>> licensePlateColor(@Body Map<String, String> queryParams);

    /**
     * 牌照类型
     */
    @POST("dic/licensePlateType")
    Observable<BaseResponse<List<BeanEnums>>> licensePlateType(@Body Map<String, String> queryParams);

    /**
     * 车辆类型
     */
    @POST("dic/vehicleType")
    Observable<BaseResponse<List<BeanEnum>>> vehicleType(@Body Map<String, String> queryParams);

    /**
     * 车辆认证
     */
    @POST("vehicleAuthentication")
    Observable<BaseResponse<String>> vehicleAuthentication(@Body Map<String, Object> queryParams);

    /**
     * 车辆认证
     */
    @POST("trailerAuthentication")
    Observable<BaseResponse<String>> trailerAuthentication(@Body Map<String, Object> queryParams);

    /**
     * 用户协议
     */
    @POST("other/userAgreement")
    Observable<BaseResponse<String>> userAgreement(@Body Map<String, String> queryParams);

    /**
     * 隐私条款
     */
    @POST("other/privacyClause")
    Observable<BaseResponse<String>> privacyClause(@Body Map<String, String> queryParams);

    /**
     * 法律申明
     */
    @POST("other/privacyClause")
    Observable<BaseResponse<String>> legalStatement(@Body Map<String, String> queryParams);

    /**
     * 退出机制公示
     */
    @POST("other/exitMechanism")
    Observable<BaseResponse<String>> exitMechanism(@Body Map<String, String> queryParams);

    /**
     * 关于我们
     */
    @POST("other/aboutUs")
    Observable<BaseResponse<BeanAboutUs>> aboutUs(@Body Map<String, String> queryParams);

    /**
     * 客服电话
     */
    @POST("support/serviceNumber")
    Observable<BaseResponse<BeanAboutUs>> serviceNumber(@Body Map<String, String> queryParams);

    /**
     * 提交反馈
     */
    @POST("support/feedback")
    Observable<BaseResponse<String>> feedback(@Body Map<String, String> queryParams);

    /**
     * 反馈列表
     */
    @POST("support/feedbackList")
    Observable<BaseResponse<BeanOnline>> feedbackList(@Body Map<String, String> queryParams);

    /**
     * 已绑定银行卡列表
     */
    @POST("wallet/cardBindList")
    Observable<BaseResponse<BeanBank>> cardBindList(@Body Map<String, String> queryParams);

    /**
     *  银行列表
     */
    @POST("wallet/bankList")
    Observable<BaseResponse<List<BeanBankList>>> bankList(@Body Map<String, String> queryParams);

    /**
     *  开卡城市列表
     */
    @POST("wallet/cityList")
    Observable<BaseResponse<List<BeanBankList>>> cityList(@Body Map<String, String> queryParams);

    /**
     *  绑卡
     */
    @POST("wallet/cardBind")
    Observable<BaseResponse<String>> cardBind(@Body Map<String, String> queryParams);

    /**
     *  解绑银行卡
     */
    @POST("wallet/unbindCard")
    Observable<BaseResponse<String>> unbindCard(@Body Map<String, String> queryParams);

    /**
     *  明细
     */
    @POST("wallet/fundsDetailQuery")
    Observable<BaseResponse<BeanWithdrawDetail>> fundsDetailQuery(@Body Map<String, String> queryParams);

    /**
     *  查询余额
     */
    @POST("wallet/accInfoQuery")
    Observable<BaseResponse<BeanUserinfo>> accInfoQuery(@Body Map<String, String> queryParams);

    /**
     *  提现
     */
    @POST("wallet/cashout")
    Observable<BaseResponse<String>> cashout(@Body Map<String, String> queryParams);

    /**
     *  我的投诉列表
     */
    @POST("complaint/complaintList")
    Observable<BaseResponse<BeanComplaint>> complaintList(@Body Map<String, String> queryParams);

    /**
     *  投诉我的列表
     */
    @POST("complaint/beingComplainedList")
    Observable<BaseResponse<BeanComplaint>> beingComplainedList(@Body Map<String, String> queryParams);

    /**
     *  申诉
     */
    @POST("complaint/complained")
    Observable<BaseResponse<String>> complained(@Body Map<String, String> queryParams);

    /**
     * 消息
     */
    @POST("message/info")
    Observable<BaseResponse<BeanMsg>> info(@Body Map<String, String> queryParams);

    /**
     * 系统通知列表
     */
    @POST("message/notice")
    Observable<BaseResponse<BeanMsg>> notice(@Body Map<String, String> queryParams);

    /**
     * 货主评价列表
     */
    @POST("message/evaluate")
    Observable<BaseResponse<BeanMsg>> evaluate(@Body Map<String, String> queryParams);

    /**
     * 投诉通知列表
     */
    @POST("message/complaint")
    Observable<BaseResponse<BeanMsg>> complaint(@Body Map<String, String> queryParams);

    /**
     * 读消息
     */
    @POST("message/read")
    Observable<BaseResponse<String>> read(@Body Map<String, String> queryParams);

    /**
     * 禁用账号
     */
    @POST("security/disableAccount")
    Observable<BaseResponse<String>> disableAccount(@Body Map<String, String> queryParams);

    /**
     * 版本更新
     */
    @POST("other/version")
    Observable<BaseResponse<BeanUpdata>> version(@Body Map<String, String> queryParams);

    /**
     * 修改用户头像
     */
    @POST("avatar")
    Observable<BaseResponse<BeanUserinfo>> avatar(@Body Map<String, String> queryParams);


    /**
     * 运输轨迹
     */
    @POST("task/track")
    Observable<BaseResponse<String>> track(@Body Map<String, String> queryParams);
}

