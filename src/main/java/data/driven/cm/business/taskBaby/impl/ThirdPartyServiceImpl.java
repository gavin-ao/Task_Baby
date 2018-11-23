package data.driven.cm.business.taskBaby.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.driven.cm.aes.WXBizMsgCrypt;
import data.driven.cm.business.taskBaby.ThirdPartyService;
import data.driven.cm.business.taskBaby.WechatPublicService;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskBaby.WechatPublicEntity;
import data.driven.cm.util.HttpUtil;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Map;

/**
 * @Author: lxl
 * @describe 第三方平台Impl
 * @Date: 2018/11/23 9:25
 * @Version 1.0
 */
@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {
    Logger logger = LoggerFactory.getLogger(ThirdPartyServiceImpl.class);
    @Autowired
    private WechatPublicService wechatPublicService;

    /**
     * 处理授权后的回调
     *
     * @author: Logan
     * @date: 2018/11/23 11:25
     * @params: [infoStr, authCode]
     * @return: void
     **/
    @Override
    public void saveCallbackAuthInfo(String infoStr, String authCode) {
        JSONObject authInfo = JSONObject.parseObject(infoStr).
                getJSONObject(WeChatConstant.API_JSON_KEY_AUTH_INFO);
        if (authInfo != null) {
            saveAuthToCache(authInfo);
            String authorizerAppid =
                    authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_APPID);
            JSONArray funcCategory = authInfo.getJSONArray(WeChatConstant.API_JSON_KEY_FUNCSCOPE_CATEGORY);
            saveAuthToMysql(authorizerAppid, funcCategory);
        }
    }

    /**
     * 保存授权信息到mysql数据库
     * authAppId,funcCategoryId
     *
     * @author: Logan
     * @date: 2018/11/23 12:02
     * @params: [authAppId, funcCategory]
     * @return: void
     **/
    private void saveAuthToMysql(String authAppId, JSONArray funcCategory) {
        logger.info("---------保存授权信息到Mysql-------------");
        WechatPublicEntity wechatPublicEntity =
                wechatPublicService.getEntityByAuthorizationAppid(authAppId);
        if (wechatPublicEntity == null) {
            StringBuilder funcCategoryIds = new StringBuilder();
            if (funcCategory != null) {
                for (int i = 0; i < funcCategory.size(); i++) {
                    JSONObject funcInfo = funcCategory.getJSONObject(i);
                    if (funcInfo != null) {
                        JSONObject func = funcInfo.getJSONObject(WeChatConstant.API_JSON_KEY_FUNC_INFO);
                        String id = func.getString(WeChatConstant.API_JSON_KEY_FUNC_ID);
                        funcCategoryIds.append(id).append(";");
                    }
                }
            }
            wechatPublicService.insertWechatPublicEntity(authAppId, funcCategoryIds.toString());
            logger.info("-------授权信息插入数据库完成-----------");
        }
    }
    /**
     * 将授权信息保存到缓存中
     * authorizer_access_token,authorizer_refresh_token
     * @author:     Logan
     * @date:       2018/11/23 12:10
     * @params:     [authInfo]
     * @return:     void
    **/
    private void saveAuthToCache(JSONObject authInfo) {
        logger.info("--------开始保存授权信息到缓存--------------");
        String authorizer_access_token =
                authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_ACCESS_TOKEN);
        String authorizerRefreshToken = authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_REFRESH_TOKEN);
        String authorizerAppid =
                authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_APPID);
        if(StringUtils.isNotEmpty(authorizer_access_token)) {
            logger.info("------保存accessToken到缓存----------");
            RedisFactory.setString(
                    WeChatConstant.getAccessTokenCacheKey(authorizerAppid),
                    authorizer_access_token,
                    WeChatConstant.CACHE_VALUE_EXPIRE_ACCESS_TOKEN * 1000);
        }else{
            logger.error("获取accessToken失败");
        }
        if(StringUtils.isNotEmpty(authorizerRefreshToken)) {
            logger.info("-------保存refreshToken到缓存------------");
            RedisFactory.setString(
                    WeChatConstant.getRefreshTokenCacheKey(authorizerAppid),
                    authorizerRefreshToken,
                    WeChatConstant.CACHE_VALUE_EXPIRE_REFRESH_TOKEN * 1000);
        }else{
            logger.error("获取refresToken失败");
        }
    }
/**
 * 根据授权的微信公众号的appId，获取accessToken，
 * 并且将刷新后的token更新到缓存中（如果有必要）
 * 1.先从缓存中去accessToken，
 * 2.如果没有取到，或者redis中已过期，则调用接口重新刷新accessToken，和refreshToken
 * 并更新到缓存中去
 * 如果refresToken丢失，则抛出异常，提示需要重新授权
 *
 * @author:     Logan
 * @date:       2018/11/23 13:35
 * @params:     [authAppId]
 * @return:     java.lang.String
**/
    @Override
    public String getAuthAccessToke(String authAppId) throws Exception {
      //1.从缓存中取
        String accessToken =
                RedisFactory.get(
                        WeChatConstant.getAccessTokenCacheKey(authAppId));
        if(StringUtils.isNotEmpty(accessToken)){//token没有过期
            return accessToken;
        }else{ //token过期了
            accessToken = refreshAccessToken(authAppId);
        }
        return accessToken;
    }


    /**
     * 刷新authorizerAccessToken
     * @author:     Logan
     * @date:       2018/11/23 13:43
     * @params:     [authAppId]
     * @return:     java.lang.String
    **/
    private String refreshAccessToken(String authAppId) throws Exception {
        String newAccessToken = "";
        String thirdPartyAccessToken = "";//todo:获取第三方平台的accessToken

        logger.info("--------得到旧的refreshToken---------------");
        //当前redis里面存的refreshToken
        String oldAuthRefreshToken =
                RedisFactory.get(
                        WeChatConstant.getRefreshTokenCacheKey(authAppId));
        if(StringUtils.isNotEmpty(oldAuthRefreshToken)){
            logger.info("-------------oldRefreshToken存在，准备刷新token,postStr：--------------------");
            String newTokenResult = WeChatUtil.accessFreshTokenAPI(authAppId,oldAuthRefreshToken);
            if(StringUtils.isNotEmpty(newTokenResult)) {
                logger.info(String.format(
                        "---------------调用刷新tokenApi返回：%s",newTokenResult));
                JSONObject newTokenObj = JSONObject.parseObject(newTokenResult);
                newAccessToken =newTokenObj.getString(
                        WeChatConstant.API_JSON_KEY_AUTH_ACCESS_TOKEN);

                if(StringUtils.isNotEmpty(newAccessToken)){
                    logger.info("--------保存newToken到缓存------------------");
                    RedisFactory.setString(
                            WeChatConstant.getAccessTokenCacheKey(authAppId),newAccessToken,
                            WeChatConstant.CACHE_VALUE_EXPIRE_ACCESS_TOKEN *1000);
                }

                String newRefreshToken = newTokenObj.getString(
                        WeChatConstant.API_JSON_KEY_AUTH_REFRESH_TOKEN);

                if(StringUtils.isNotEmpty(newRefreshToken)){
                    logger.info("--------保存newRefreshToken到缓存------------------");
                    RedisFactory.setString(
                            WeChatConstant.getRefreshTokenCacheKey(authAppId),newRefreshToken,
                            WeChatConstant.CACHE_VALUE_EXPIRE_REFRESH_TOKEN *1000);
                }
            }

        }else{
            throw new Exception("旧的AuthRefreshToken丢失，需要重新授权");

        }
       return newAccessToken;
    }

    /**
     * 解密第三方发送的xml
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    public void hadleAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String timestamp = request.getParameter("timestamp");
        String encrypt_type = request.getParameter("encrypt_type");
        String nonce = request.getParameter("nonce");
        String msg_signature = request.getParameter("msg_signature");
        logger.info("------------------------timestamp " + timestamp + "------------------------");
        logger.info("------------------------encrypt_type " + encrypt_type + "------------------------");
        logger.info("------------------------nonce " + nonce + "------------------------");
        logger.info("------------------------msg_signature " + msg_signature + "------------------------");

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = request.getReader();
        String line;
        while ((line = in.readLine()) != null) {
            stringBuilder.append(line);
        }
        String xml = stringBuilder.toString();
        logger.info("第三方平台收到的原生: " + xml);
        String encodingAesKey = WeChatConstant.THIRD_PARTY_ENCODINGAESKEY;
        String appId = WeChatConstant.THIRD_PARTY_APPID;
        //解密
        WXBizMsgCrypt pc = new WXBizMsgCrypt(WeChatConstant.THIRD_PARTY_TOKEN, encodingAesKey, appId);
        String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
//        String fromXML = String.format(format, requestMap.get("Encrypt"));
        xml = pc.decryptMsg(msg_signature, timestamp, nonce, xml);
        logger.info("第三方平台解析后的xml: " + xml);
        Map<String, String> requestMap = WeChatUtil.parseXml(xml);
        //得到tickon
        String component_verify_ticket = requestMap.get("ComponentVerifyTicket");
        logger.info("-----------------ticket : " + component_verify_ticket + "----------------------");
    }
}
