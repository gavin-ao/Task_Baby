package data.driven.cm.common;

import data.driven.cm.util.SessionUtil;
import data.driven.cm.util.UUIDUtil;

/**
 * 微信缓存
 * @author hejinkai
 * @date 2018/6/26
 */
public class WechatApiSession {

    /**
     * 获取缓存对象
     * @param sessionID
     * @return
     */
    public static WechatApiSessionBean getSessionBean(String sessionID){
        WechatApiSessionBean wechatApiSessionBean = RedisFactory.get(sessionID, WechatApiSessionBean.class);
        //交互时自动延期
        SessionUtil.autoDelayExpire(sessionID);
        return wechatApiSessionBean;
    }

    /**
     * 更新缓存对象
     * @param sessionID
     * @param wechatApiSessionBean
     * @return
     */
    public static void setSessionBean(String sessionID, WechatApiSessionBean wechatApiSessionBean){
        RedisFactory.set(sessionID, wechatApiSessionBean, Constant.REDIS_DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 登录时存入缓存,返回sessionID
     */
    public static String setSessionBean(WechatApiSessionBean wechatApiSessionBean){
        String sessionID = UUIDUtil.getUUID();
        //设置缓存
        setSessionBean(sessionID, wechatApiSessionBean);
        //设置自动延期时效
        SessionUtil.setExpireKeyToRedis(sessionID);
        return sessionID;
    }

}
