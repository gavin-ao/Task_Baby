package data.driven.cm.util;

import data.driven.cm.common.Constant;
import data.driven.cm.common.RedisFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hejinkai
 * @date 2018/7/1
 */
public class SessionUtil {

    /** 用于判断session是否能够自动延期，原因是权限设置是按天的，为了防止无限期使用的bug，即如果一直做交互session永不过期。限制为一天内失效 **/
    private static final String IS_EXPIRE_KEY = "EXPIRE_KEY_";
    private static final String IS_EXPIRE_VALUE = "TRUE";


    /**
     * 设置限制一天内session能自动延期的判断
     * @param sessionID
     */
    public static void setExpireKeyToRedis(String sessionID){
        Date now = new Date();
        Date nextDate = DateFormatUtil.addDate(now, 3, 1);
        SimpleDateFormat sdf = DateFormatUtil.getLocal();
        Date endDate = null;
        try{
            endDate = sdf.parse(sdf.format(nextDate));
        }catch (ParseException e){
            return ;
        }
        long startTime = now.getTime();
        long endTime = endDate.getTime();
        long milliseconds = endTime - startTime;
        String key = IS_EXPIRE_KEY + sessionID;
        RedisFactory.setString(key, IS_EXPIRE_VALUE, milliseconds);
    }

    /**
     * 交互时session自动延期
     * @param sessionID
     */
    public static void autoDelayExpire(String sessionID){
        if(isAutoRenewal(sessionID)){
            RedisFactory.expire(sessionID, Constant.REDIS_DEFAULT_EXPIRE_SECONDS);
        }
    }

    /**
     * 判断是否能够自动延期
     * @param sessionID
     * @return
     */
    public static boolean isAutoRenewal(String sessionID){
        String key = IS_EXPIRE_KEY + sessionID;
        String expireKey = RedisFactory.get(key);
        if(expireKey != null && IS_EXPIRE_VALUE.equals(expireKey)){
            return true;
        }
        return false;
    }

    /**
     * 根据sessionid清除清除redis中的缓存
     * @param sessionID
     */
    public static void clear(String sessionID){
        RedisFactory.remove(sessionID);
    }

}
