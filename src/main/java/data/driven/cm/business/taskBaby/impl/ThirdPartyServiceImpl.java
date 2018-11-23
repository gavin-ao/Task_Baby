package data.driven.cm.business.taskBaby.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.driven.cm.aes.WXBizMsgCrypt;
import data.driven.cm.business.taskBaby.ThirdPartyService;
import data.driven.cm.business.taskBaby.WechatPublicService;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskBaby.WechatPublicEntity;
import data.driven.cm.util.WeChatUtil;
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
    public void authCallback(String infoStr, String authCode) {
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
        String authorizer_access_token =
                authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_ACCESS_TOKEN);
        int expiresIn = authInfo.getInteger(WeChatConstant.API_JSON_KEY_AUTH_EXPIRES_IN);
        String authorizerRefreshToken = authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_REFRESH_TOKEN);

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
