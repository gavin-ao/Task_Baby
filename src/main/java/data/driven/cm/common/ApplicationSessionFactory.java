package data.driven.cm.common;

import data.driven.cm.entity.user.UserInfoEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 所有的缓存信息，如果需要缓存信息，请通过在此类中定义方法来调用，即保证所有的key都是在这个类中维护
 * @author hejinkai
 * @date 2018/7/1
 */
public class ApplicationSessionFactory {

    public static final String CACHE_TYPE_REDIS = "redis";

    public static final String SESSION_KEY_USER = "user";
    public static final String SESSION_KEY_VALIDATE_CODE_PREFIX = "validateCode_";


    /**
     * 根据sessionID构造缓存容器
     */
    private static ApplicationSession getSession(HttpServletRequest request, HttpServletResponse response,String sessionID) {
        return new ApplicationSession(request,response,sessionID,CACHE_TYPE_REDIS);
    }

    /**
     * 构造缓存容器
     */
    private static ApplicationSession getSession(HttpServletRequest request, HttpServletResponse response) {
        return new ApplicationSession(request,response,CACHE_TYPE_REDIS);
    }

    /**
     * 清除redis中的session信息
     */
    public static void clearSession(HttpServletRequest request, HttpServletResponse response){
        getSession(request,response).clear();
    }

    /**
     * 根据sessionID清除缓存信息
     */
    public static void clearSession(HttpServletRequest request, HttpServletResponse response,String sessionID){
        getSession(request,response).clear(sessionID);
    }

    /**
     * 获取登录的用户信息
     */
    public static UserInfoEntity getUser(HttpServletRequest request, HttpServletResponse response){
        return getSession(request,response).get(SESSION_KEY_USER, UserInfoEntity.class);
    }

    /**
     * 根据sessionID获取用户信息
     */
    public static UserInfoEntity getUser(HttpServletRequest request, HttpServletResponse response,String sessionID){
        return getSession(request,response,sessionID).get(SESSION_KEY_USER, UserInfoEntity.class);
    }

    /**
     * 设置用户信息
     */
    public static void setUser(HttpServletRequest request, HttpServletResponse response, UserInfoEntity user){
        getSession(request, response).set(SESSION_KEY_USER, user);

    }

    public static void init(HttpServletRequest request, HttpServletResponse response){
        getSession(request, response);
    }

    public static String getSessionId(HttpServletRequest request, HttpServletResponse response){
        return getSession(request,response).getSessionID();
    }
}
