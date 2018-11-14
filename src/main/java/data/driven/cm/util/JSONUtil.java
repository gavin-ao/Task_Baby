package data.driven.cm.util;

import com.alibaba.fastjson.JSONObject;

/**
 * json工具类
 * @author hejinkai
 * @date 2018/6/15
 */
public class JSONUtil {

    /**
     * 设置常规信息返回JSONObject
     * @param success 成功标志
     * @param code  状态码
     * @param msg   信息
     * @return
     */
    public static JSONObject putMsg(boolean success, String code, String msg){
        JSONObject result = new JSONObject();
        result.put("success", success);
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    /**
     * 设置常规信息返回JSONObject
     * @param success 成功标志
     * @param code 状态码
     * @param msg 信息
     * @param storeId 门店id
     * @param userId 用户Id
     * @return
     */
    public static JSONObject putMsg(boolean success,String code,String msg,String userId,String storeId){
        JSONObject result = new JSONObject();
        result.put("success", success);
        result.put("code", code);
        result.put("msg", msg);
        result.put("userId",userId);
        result.put("storeId",storeId);
        return result;
    }

}
