package data.driven.cm.controller.wechatapi;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.wechat.WechatAppInfoService;
import data.driven.cm.business.wechat.WechatLoginLogService;
import data.driven.cm.business.wechat.WechatUserService;
import data.driven.cm.common.WechatApiSession;
import data.driven.cm.common.WechatApiSessionBean;
import data.driven.cm.entity.wechat.WechatAppInfoEntity;
import data.driven.cm.util.JSONUtil;
import data.driven.cm.vo.wechat.WechatUserInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static data.driven.cm.common.Constant.USER_INFO_MAX_INTERVAL;
import static data.driven.cm.util.WXUtil.executeDecodeUserInfo;
import static data.driven.cm.util.WXUtil.getSessionKey;

/**
 * 登录
 * @author hejinkai
 * @date 2018/6/27
 */

@Controller
@RequestMapping(path = "/wechatapi/service")
public class WechatLoginApiController {

    private static final Logger logger = LoggerFactory.getLogger(WechatLoginApiController.class);

    @Autowired
    private WechatAppInfoService wechatAppInfoService;
    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private WechatLoginLogService wechatLoginLogService;

    @ResponseBody
    @RequestMapping(path = "/newLogin")
    public JSONObject newLogin(String appid, String secret, String code, String encryptedData, String iv, String storeId){
        long start = System.currentTimeMillis();
        //根据code获取sessionKey
        JSONObject sessionJsonObject = getSessionKey(appid, secret, code);
        String sessionKey = null;
        if(sessionJsonObject.containsKey("session_key")){
            sessionKey = sessionJsonObject.getString("session_key");
        }else{
            return JSONUtil.putMsg(false, "101", "获取不到session_key，请重新登录");
        }
        //设置缓存
        WechatApiSessionBean wechatApiSessionBean = new WechatApiSessionBean();
        wechatApiSessionBean.setSessionKey(sessionKey);
        if(sessionJsonObject.containsKey("openid")){
            WechatUserInfoVO userInfo = new WechatUserInfoVO();
            userInfo.setOpenId(sessionJsonObject.getString("openid"));
            wechatApiSessionBean.setUserInfo(userInfo);
        }

        String sessionID = WechatApiSession.setSessionBean(wechatApiSessionBean);
        JSONObject result = JSONUtil.putMsg(true, "200", "调用成功");
        result.put("sessionID", sessionID);

        encryptedData = encryptedData.replace(" ","+");
        iv = iv.replace(" ","+");
        JSONObject syncUser = syncUser(encryptedData, iv, sessionID, wechatApiSessionBean, storeId);
        result.putAll(syncUser);
        long end = System.currentTimeMillis();
        logger.warn("总耗时："+(end-start)/1000.0+"秒");
        return result;
    }

    /**
     * 同步用户信息
     * @param encryptedData
     * @param iv
     * @param sessionID
     * @param wechatApiSessionBean
     * @param storeId
     * @return
     */
    private JSONObject syncUser(String encryptedData, String iv, String sessionID, WechatApiSessionBean wechatApiSessionBean, String storeId) {
        WechatUserInfoVO sUser = wechatApiSessionBean.getUserInfo();
        String openId = null;
        if(sUser != null){
            openId = sUser.getOpenId();
        }

        String sessionKey = wechatApiSessionBean.getSessionKey();
        JSONObject userJson = executeDecodeUserInfo(encryptedData, iv, sessionKey);
        String appid = null;
        try{
            JSONObject watermark = userJson.getJSONObject("userInfo").getJSONObject("watermark");
            appid = watermark.getString("appid");
            Long timestamp = watermark.getLong("timestamp");
            long now = System.currentTimeMillis();
            long interval = now / 1000 - timestamp;
            System.out.println(timestamp);
            System.out.println(now);
            System.out.println(interval);
            if(interval < 0 || interval > USER_INFO_MAX_INTERVAL){
                return JSONUtil.putMsg(false, "102", "处理超时，请重新登录");
            }

        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        if(appid == null){
            return JSONUtil.putMsg(false, "103", "获取的信息有误，请重试");
        }

        WechatAppInfoEntity wechatAppInfoEntity = wechatAppInfoService.getAppInfo(appid);
        if(wechatAppInfoEntity == null){
            return JSONUtil.putMsg(false, "104", "小程序不存在，非法登录");
        }
        WechatUserInfoVO userObj = userJson.getJSONObject("userInfo").toJavaObject(WechatUserInfoVO.class);
        if(openId == null){
            openId = userObj.getOpenId();
        }
        //根据openId获取数据库中的用户信息
        WechatUserInfoVO userInfo = wechatUserService.getUserInfoByOpenId(openId);
        if(userInfo == null){
//            //如果openId获取的用户为空，就根据unionId获取用户信息
//            userInfo = wechatUserService.getUserInfoByUnionId(userObj.getUnionId());
//            if(userObj.getUnionId() == null || userObj.getUnionId().trim().length() < 1){
//                logger.error("------unionId为空，同步用户失败。登录失败");
//                return JSONUtil.putMsg(true, "200", "登录成功");
//            }
            String wechatUserInfoId = null;
            //如果用户信息为空，就新增用户到数据库中
//            if(userInfo == null){
            wechatUserInfoId = wechatUserService.addUserInfo(userObj);
//            }else{
//                wechatUserInfoId = userInfo.getWechatUserId();
//            }
            //根据openId获取用户为空时，就新增一条用户关联app的关系信息到数据库中
            wechatUserService.addUserAndAppMap(wechatAppInfoEntity.getAppInfoId(), wechatUserInfoId, openId);
            userInfo = wechatUserService.getUserInfoByOpenId(openId);
        }

        wechatApiSessionBean.setUserInfo(userInfo);
        WechatApiSession.setSessionBean(sessionID, wechatApiSessionBean);
        //插入登录日志
        wechatLoginLogService.insertLoginLog(userInfo.getWechatUserId(), userInfo.getAppInfoId(), storeId);
        JSONObject result = JSONUtil.putMsg(true, "200", "登录成功");
        return result;
    }
}
