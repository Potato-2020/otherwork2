package com.ibd.dipper.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class GPSServiceListener implements LocationListener {

    private static final String tag = "GPSServiceListener";

    private static final float minAccuracyMeters = 35;

    private static final String hostUrl = "http://doandroid.info/gpsservice/position.php?";

    private static final String user = "huzhangyou";

    private static final String pass = "123456";

    private static final int duration = 10;

    private final DateFormat timestampFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public int GPSCurrentStatus;

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (location != null) {
            if (location.hasAccuracy() && location.getAccuracy() <= minAccuracyMeters) {
                // 获取时间参数,将时间一并Post到服务器端
                GregorianCalendar greg = new GregorianCalendar();
                TimeZone tz = greg.getTimeZone();
                int offset = tz.getOffset(System.currentTimeMillis());
                greg.add(Calendar.SECOND, (offset / 1000) * -1);
                StringBuffer strBuffer = new StringBuffer();
                strBuffer.append(hostUrl);
                strBuffer.append("user=");

                strBuffer.append(user);
                strBuffer.append("&pass=");
                strBuffer.append(pass);
                strBuffer.append("&Latitude=");
                strBuffer.append(location.getLatitude());
                strBuffer.append("&Longitude=");
                strBuffer.append(location.getLongitude());
                strBuffer.append("&Time=");
                strBuffer.append(timestampFormat.format(greg.getTime()));
                strBuffer.append("&Speed=");
                strBuffer.append(location.hasSpeed());
                doGet(strBuffer.toString());
                Log.v(tag, strBuffer.toString());
            }
        }
    }

    // 将数据通过get的方式发送到服务器,服务器可以根据这个数据进行跟踪用户的行走状态
    private void doGet(String string) {
        // TODO Auto-generated method stub
        //
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        GPSCurrentStatus = status;
    }

}
