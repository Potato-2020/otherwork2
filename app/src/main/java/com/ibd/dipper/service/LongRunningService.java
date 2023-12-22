package com.ibd.dipper.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import com.ibd.dipper.bean.BeanBill;
import com.ibd.dipper.net.ApiService;
import com.ibd.dipper.net.RetrofitClient;
import com.ibd.dipper.ui.bill.BillItemViewModel;
import com.ibd.dipper.utils.JsonUtils;
import com.ibd.dipper.utils.LocationUtils;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LongRunningService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LongRunningService", "executed at " + new Date().toString());

                Location location = LocationUtils.getLastKnownLocation(getApplicationContext());
                Log.d("Latitude=", String.valueOf(location.getLatitude()));
                Log.d("Longitude=", String.valueOf(location.getLongitude()));

                Map<String, String> params = new HashMap<>();
                params.put("id", Data.getOrderID());
                params.put("lat", location.getLatitude() + "");
                params.put("long", location.getLongitude()+ "");
                RetrofitClient.getInstance().create(ApiService.class)
                        .track(params)
                        .compose(RxUtils.schedulersTransformer())  // 线程调度
                        .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                        .subscribe((Consumer<BaseResponse<String>>) response -> {
                            KLog.e(JsonUtils.toJson(response));
                            if (response.isOk()) {
                                Log.d("track接口","提交成功");
                            } else {
                                ToastUtils.showLong(response.message);
                            }
                        }, (Consumer<ResponseThrowable>) throwable -> {
                            ToastUtils.showLong(throwable.message);
                        });
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //int anHour = 60 * 60 * 1000; // 这是一小时的毫秒数
        int anHour = 10 * 1000; // 这是一小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
//        super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
    }

}
