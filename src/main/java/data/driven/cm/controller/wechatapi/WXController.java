package data.driven.cm.controller.wechatapi;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.util.JSONUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static data.driven.cm.util.WXUtil.executeDecodeUserInfo;
import static data.driven.cm.util.WXUtil.getSessionKey;

/**
 * 微信测试controller
 * @author hejinkai
 * @date 2018/6/15
 */
@Controller
@RequestMapping(path = "/wx/test")
public class WXController {

    @ResponseBody
    @RequestMapping(path = "/getSession")
    public JSONObject getSession(String appid, String secret, String code){
        return getSessionKey(appid, secret, code);
    }

    public static String session_key = "";
    public static String session_Code = "";
    /**
     * 解密用户信息
     * @param encryptedData 用户信息密文
     * @param iv    初始向量 - 微信接口返回
     * @param code  微信登录接口返回的code，用于获取session_key
     */
    @ResponseBody
    @RequestMapping(path = "/decodeUserInfo")
    public JSONObject decodeUserInfo(String encryptedData, String iv, String code){
        encryptedData = encryptedData.replace(" ","+");
        boolean bl = true;
        if(session_Code != null){
            if(!session_Code.equals(code)){
                bl = setSession_key(code);
            }
        }else{
            bl = setSession_key(code);
        }
        if(!bl){
            JSONUtil.putMsg(false, "102", "获取不到session_key，请重新登录");
        }
        JSONObject result = executeDecodeUserInfo(encryptedData, iv, session_key);
        System.out.println(result);
        return result;
    }

    private boolean setSession_key(String code){
        JSONObject sessionJsonObject = getSessionKey(code);
        if(sessionJsonObject.containsKey("session_key")){
            session_key = sessionJsonObject.getString("session_key");
            session_Code = code;
            return true;
        }else{
            return false;
        }
    }

}
