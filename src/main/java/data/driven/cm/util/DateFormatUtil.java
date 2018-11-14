package data.driven.cm.util;

import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期格式化工具类
 * Created by player on 2017/9/16.
 */
public class DateFormatUtil {
    public static final String yearName = "year";
    public static final String monthName = "month";
    public static final String startTimeName = "startTime";
    public static final String endTimeName = "endTime";

    public static final String normalPattern = "yyyy-MM-dd";

    public static final ThreadLocal<Map<String,SimpleDateFormat>> local = new ThreadLocal<Map<String,SimpleDateFormat>>();

    /**
     * 移除线程本地变量
     */
    public static void removeLocal(){
        local.remove();
    }

    /**
     * 如果本地线程没有获取到日期格式化类，那么就新建一个并且放入到本地线程
     * @param pattern
     * @return
     */
    public static SimpleDateFormat getLocal(String pattern){
        Map<String, SimpleDateFormat> map = local.get();
        if(map==null){
            map = new HashMap<String, SimpleDateFormat>();
            local.set(map);
        }
        SimpleDateFormat sdf = map.get(pattern);
        if(sdf==null){
            if(pattern == null){
                pattern = normalPattern;
            }
            sdf = new SimpleDateFormat(pattern);
            map.put(pattern, sdf);
        }
        return sdf;
    }

    /**
     * 如果本地线程没有获取到日期格式化类，那么就新建一个并且放入到本地线程
     *   默认yyyy-MM-dd
     * @return
     */
    public static SimpleDateFormat getLocal(){
        return getLocal(normalPattern);
    }


    /**
     * 获取年，月，日的数字
     * @param dateStr
     * @return
     */
    public static JSONObject getNumTime(String dateStr){
        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat sdf = getLocal();
        try{
            Date date = sdf.parse(dateStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            long startTime = c.getTimeInMillis();
            jsonObject.put(startTimeName,startTime);
            jsonObject.put(yearName,c.get(Calendar.YEAR));
            jsonObject.put(monthName,c.get(Calendar.MONTH) + 1);

            c.add(Calendar.MONTH,1);
            long endTime = c.getTimeInMillis();
            jsonObject.put(endTimeName,endTime);
        }catch (Exception e){

        }
        return jsonObject;
    }

    /**
     * 根据字符串返回日期
     * @param timeformat    日期格式字符串
     * @param dateStr
     * @return
     */
    public static Date getTime(String timeformat,String dateStr){
        SimpleDateFormat sdf = getLocal(timeformat);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字符串返回日期
     * @param dateStr
     * @return
     */
    public static Date getTime(String dateStr){
        return getTime(normalPattern, dateStr);
    }

    /**
     * 根据传入的格式，处理后返回日期，可以用于清除时分秒等
     * @param timeformat    日期格式字符串
     * @param date  日期
     * @return
     */
    public static Date convertDate(String timeformat, Date date){
        SimpleDateFormat sdf = getLocal(timeformat);
        return getTime(normalPattern, sdf.format(date));
    }

    /**
     * 根据传入的格式，处理后返回日期，可以用于清除时分秒等
     * @param date
     * @return
     */
    public static Date convertDate(Date date){
        return convertDate(normalPattern, date);
    }

    /**
     * 返回时间戳
     * @param timeformat
     * @param dataStr
     * @return
     */
    public static Long getTimeMillis(String timeformat, String dataStr){
        Date date = getTime(timeformat,dataStr);
        if(date!=null){
            return date.getTime();
        }
        return null;
    }

    /**
     * 日期加法
     * @param date 日期原值
     * @param type 添加单位 ， 1 - 年， 2 - 月， 3 - 天
     * @param amount 添加的值
     * @return
     */
    public static Date addDate(Date date, int type, int amount){
        if(date == null){
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int filed = 0;
        if(type == 1){
            filed = Calendar.YEAR;
        }else if(type == 2){
            filed = Calendar.MONTH;
        }else if(type == 3){
            filed = Calendar.DAY_OF_YEAR;
        }else{
            return null;
        }
        calendar.add(filed, amount);
        return calendar.getTime();
    }

    /**
     * 针对于yyyy-MM-dd格式的字符串，拼接23:59:59返回日期
     * @param endDate
     * @return
     */
    public static Date toEndDate(String endDate){
        endDate += " 23:59:59";
        SimpleDateFormat sdf = getLocal("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
