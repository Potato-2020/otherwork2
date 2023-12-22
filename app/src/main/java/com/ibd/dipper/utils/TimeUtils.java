package com.ibd.dipper.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    private static TimeUtils timeUtils;

    public static TimeUtils get() {
        if (timeUtils == null) {
            timeUtils = new TimeUtils();
        }
        return timeUtils;
    }

    private String format(Object time, SimpleDateFormat format) {
        if (time instanceof String) {
            String s = (String) time;
            if (TextUtils.isEmpty(s)) {
                return format.format(new Date(0));
            } else {
                return format.format(new Date(Long.parseLong(s)));
            }
        } else if (time instanceof Long) {
            long l = (Long) time;
            if (l <= 0) {
                return format.format(new Date(0));
            } else {
                return format.format(new Date(l));
            }
        } else {
            return format.format(new Date(0));
        }
    }


    private long parse(String date, SimpleDateFormat format) {
        if (TextUtils.isEmpty(date)) {
            return 0L;
        } else {
            Date date1;
            try {
                date1 = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0L;
            }
            return date1.getTime();
        }
    }

    private SimpleDateFormat yyyyMMddhhmmssFormatterC = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    public String formatCyyyyMMddhhmmss(Object time) {
        return format(time, yyyyMMddhhmmssFormatterC);
    }

    public long parseCyyyyMMddhhmmss(String date) {
        return parse(date, yyyyMMddhhmmssFormatterC);
    }

    private SimpleDateFormat yyyyMMddhhmmssFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String formatyyyyMMddhhmmss(Object time) {
        return format(time, yyyyMMddhhmmssFormatter);
    }

    public long parseyyyyMMddhhmmss(String date) {
        return parse(date, yyyyMMddhhmmssFormatter);
    }

    private SimpleDateFormat yyyyMMddhhmmFormatterC = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    public String formatCyyyyMMddhhmm(Object time) {
        return format(time, yyyyMMddhhmmFormatterC);
    }

    public long parseCyyyyMMddhhmm(String date) {
        return parse(date, yyyyMMddhhmmFormatterC);
    }

    private SimpleDateFormat yyyyMMddhhmmFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public String formatyyyyMMddhhmm(Object time) {
        return format(time, yyyyMMddhhmmFormatter);
    }

    public long parseyyyyMMddhhmm(String date) {
        return parse(date, yyyyMMddhhmmFormatter);
    }


    private SimpleDateFormat yyyyMMddFormatterC = new SimpleDateFormat("yyyy年MM月dd日");

    public String formatCyyyyMMdd(Object time) {
        return format(time, yyyyMMddFormatterC);
    }

    public long parseCyyyyMMdd(String date) {
        return parse(date, yyyyMMddFormatterC);
    }

    private SimpleDateFormat yyyyMMddFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public String formaCyyyyMMdd(Object time) {
        return format(time, yyyyMMddFormatter);
    }

    public long parseyyyyMMdd(String date) {
        return parse(date, yyyyMMddFormatter);
    }

    private SimpleDateFormat MMddFormatterC = new SimpleDateFormat("MM月dd日");

    public String formatCMMdd(Object time) {
        return format(time, MMddFormatterC);
    }

    public long parseCMMdd(String date) {
        return parse(date, MMddFormatterC);
    }

    private SimpleDateFormat MMddFormatter = new SimpleDateFormat("MM-dd");

    public String formatMMdd(Object time) {
        return format(time, MMddFormatterC);
    }

    public long parseMMdd(String date) {
        return parse(date, MMddFormatterC);
    }

    private SimpleDateFormat HHmmssFormatter = new SimpleDateFormat("HH:mm:ss");

    public String formatHHmmss(Object time) {
        return format(time, HHmmssFormatter);
    }

    public long parseHHmmss(String date) {
        return parse(date, HHmmssFormatter);
    }

    private SimpleDateFormat HHmmFormatter = new SimpleDateFormat("HH:mm");

    public String formatHHmm(Object time) {
        return format(time, HHmmFormatter);
    }

    public long parseHHmm(String date) {
        return parse(date, HHmmFormatter);
    }

    private SimpleDateFormat yyyyFormatter = new SimpleDateFormat("yyyy");

    public int formatyyyy(Object time) {
        return Integer.parseInt(format(time, yyyyFormatter));
    }

    private SimpleDateFormat MMFormatter = new SimpleDateFormat("MM");

    public int formatMM(Object time) {
        return Integer.parseInt(format(time, MMFormatter));
    }

    private SimpleDateFormat ddFormatter = new SimpleDateFormat("dd");

    public int formatdd(Object time) {
        return Integer.parseInt(format(time, ddFormatter));
    }


    /**
     * 微信聊天显示方式
     */

    public String formatWeChatTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return null;
        } else {
            long timeold = Long.parseLong(time);
            long timenew = System.currentTimeMillis();
            String dayold = ddFormatter.format(timeold);
            String daynew = ddFormatter.format(timenew);

            if ((timenew - timeold) < (24 * 60 * 60 * 1000) && Integer.parseInt(daynew) == Integer.parseInt(dayold)) {
                return HHmmFormatter.format(new Date(Long.parseLong(time)));
            } else if ((timenew - timeold) < (2 * 24 * 60 * 60 * 1000) && Integer.parseInt(daynew) == Integer.parseInt(dayold) + 1) {
                return "昨天";
            } else {
                return MMddFormatter.format(new Date(Long.parseLong(time)));
            }
        }
    }
}
