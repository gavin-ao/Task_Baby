package data.driven.cm.common;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.util.CookieUtil;
import data.driven.cm.util.SessionUtil;
import data.driven.cm.util.UUIDUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author 何晋凯
 * @date 2018/07/01
 */
public class ApplicationSession {

    /** 用于判断session是否能够自动延期，原因是权限设置是按天的，为了防止无限期使用的bug，即如果一直做交互session永不过期。限制为一天内失效 **/
    private static final String IS_EXPIRE_KEY = "EXPIRE_KEY_";
    private static final String IS_EXPIRE_VALUE = "TRUE";
    private String sessionID;
    private ApplicationSessionBean sessionBean;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String cacheType;

    public ApplicationSession(HttpServletRequest request, HttpServletResponse response, String cacheType){
        this(request,response,getSessionID(request,response),cacheType);
    }

    public ApplicationSession(HttpServletRequest request, HttpServletResponse response, String sessionID, String cacheType) {
        this.request = request;
        this.response = response;
        this.sessionID = sessionID;
        this.cacheType = cacheType;

        this.sessionBean = RedisFactory.get(this.sessionID,ApplicationSessionBean.class);
        if(this.sessionBean == null){
            RedisFactory.set(this.sessionID, new ApplicationSessionBean());
            SessionUtil.setExpireKeyToRedis(sessionID);
            this.sessionBean = RedisFactory.get(this.sessionID,ApplicationSessionBean.class);
        }else{
            if(SessionUtil.isAutoRenewal(sessionID)){
                RedisFactory.expire(this.sessionID, Constant.REDIS_DEFAULT_EXPIRE_SECONDS);
            }
        }

    }

    public static String getSessionID(HttpServletRequest request, HttpServletResponse response) {
        String sessionID = CookieUtil.getCookie(request, Constant.SESSIONID_COOKIE_NAME);
        if (sessionID == null) {
            sessionID = UUIDUtil.getUUID();
            CookieUtil.addCookie(Constant.SESSIONID_COOKIE_NAME, sessionID, -1, request,response);
        }
        return sessionID;
    }

    public String getString(String key){
        Object value = get(key);
        if(value == null){
            return null;
        }
        if(value instanceof String){
            return String.valueOf(value);
        }
        throw new RuntimeException("数据类型不匹配，'"+value+"'不是String类型");
    }

    public Object get(String key){
        return this.sessionBean.getSessionMap().get(key);
    }

    public <T> T get(String key,Class<T> tClass){
        if(this.sessionBean != null && this.sessionBean.getSessionMap() != null && !this.sessionBean.getSessionMap().isEmpty()){
            Object value = this.sessionBean.getSessionMap().get(key);
            if(value != null){
                if(value instanceof JSONObject){
                    JSONObject userJson = (JSONObject) value;
                    return userJson.toJavaObject(tClass);
                }else {
                    return (T)value;
                }
            }
        }
        return null;
    }

    public void set(String key,Object value){
        this.sessionBean.getSessionMap().put(key,value);
        RedisFactory.set(this.sessionID,this.sessionBean);
    }

    public void set(String key,Object value,int seconds){
        this.sessionBean.getSessionMap().put(key,value);
        RedisFactory.set(this.sessionID,this.sessionBean, seconds);
    }

    public void clear(){
        RedisFactory.remove(this.sessionID);
    }
    public void clear(String sessionID){
        SessionUtil.clear(sessionID);
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}



