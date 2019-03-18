package data.driven.cm.business.taskbaby.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import data.driven.cm.aes.WXBizMsgCrypt;
import data.driven.cm.business.taskbaby.ThirdPartyService;
import data.driven.cm.business.taskbaby.WechatPublicDetailService;
import data.driven.cm.business.taskbaby.WechatPublicService;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.WechatPublicEntity;
import data.driven.cm.util.WeChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    /**
     * 微信公众号 Service层
     */
    @Autowired
    private WechatPublicService wechatPublicService;
    /**
     * 公众号详细信息表Service
     */
    @Autowired
    private WechatPublicDetailService wechatPublicDetailService;

    /**
     * 处理授权后的回调服务
     * @author: Logan
     * @date: 2018/11/23 11:24
     * @param authCode 公众号授权码
     */
    @Override
    public void saveCallbackAuthInfo(String authCode) {
        //获取授权信息
        String authInfoStr = WeChatUtil.getAuthoInfo(authCode);
        logger.info(String.format("返回授权码后，获取授权详细信息:%s",authInfoStr));
        JSONObject authInfo = JSONObject.parseObject(authInfoStr).
                getJSONObject(WeChatConstant.API_JSON_KEY_AUTH_INFO);
        if (authInfo != null) {
            saveAuthToCache(authInfo,authCode);
            String authorizerAppid =
                    authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_APPID);
            JSONArray funcCategory = authInfo.getJSONArray(WeChatConstant.API_JSON_KEY_FUNCSCOPE_CATEGORY);
            saveAuthToMysql(authorizerAppid, funcCategory);
            saveWechatAccountDetail(authorizerAppid);
        }
    }

    /**
     * 保存授权信息到mysql数据库
     * @author: Logan
     * @date: 2018/11/23 12:02
     * @param authAppId 公众号appid
     * @param funcCategory 授权给开发者的权限集列表
     */
    private void saveAuthToMysql(String authAppId, JSONArray funcCategory) {
        logger.info("---------保存授权信息到Mysql-------------");
        WechatPublicEntity wechatPublicEntity =
                wechatPublicService.getEntityByAuthorizationAppid(authAppId);
        if (wechatPublicEntity == null) {
            logger.info(String.format("----------当前appId:%s下的publicEntity无记录------------",authAppId));
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
     * @author:     Logan
     * @date:       2018/11/23 12:10
     * @param authInfo authJson信息
     * @param authCode 授权code
     */
    private void saveAuthToCache(JSONObject authInfo,String authCode) {
        logger.info("--------开始保存授权信息到缓存--------------");
        String authorizerAccessToken =
                authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_ACCESS_TOKEN);
        String authorizerRefreshToken = authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_REFRESH_TOKEN);
        String authorizerAppid =
                authInfo.getString(WeChatConstant.API_JSON_KEY_AUTH_APPID);
        if(StringUtils.isNotEmpty(authCode)){
            RedisFactory.setString(
                    WeChatConstant.getAuthCodeCacheKey(authorizerAppid),authCode,WeChatConstant.CACHE_VALUE_EXPIRE_AUTH_CODE*1000);
        }
        if(StringUtils.isNotEmpty(authorizerAccessToken)) {
            logger.info("------保存accessToken到缓存----------");
            RedisFactory.setString(
                    WeChatConstant.getAccessTokenCacheKey(authorizerAppid),
                    authorizerAccessToken,
                    WeChatConstant.CACHE_VALUE_EXPIRE_ACCESS_TOKEN * 1000);
        }else{
            logger.error("获取accessToken失败");
        }
        if(StringUtils.isNotEmpty(authorizerRefreshToken)) {
            logger.info("-------保存refreshToken到缓存------------");
            RedisFactory.setString(
                    WeChatConstant.getRefreshTokenCacheKey(authorizerAppid),
                    authorizerRefreshToken,-1);
        }else{
            logger.error("获取refresToken失败");
        }
    }
    /**
     * 根据authAppId，获取授权微信账号详情，存数据库
     * @author:     Logan
     * @date:       2018/11/23 16:40
     * @param authAppId 公众号appid
     */
    private void saveWechatAccountDetail(String authAppId){
        String detailStr = WeChatUtil.accessAuthAccountDetailAPI(authAppId);
        logger.info(String.format("-----------wechatDetailStr:%s----------",detailStr));
        JSONObject rootObj = JSONObject.parseObject(detailStr);
        if(rootObj != null){
            JSONObject detailObject = rootObj.getJSONObject(WeChatConstant.API_JSON_KEY_AUTHORIZER_INFO);
           String nickName =
                   detailObject.getString(WeChatConstant.API_JSON_KEY_NICK_NAME);
           String headImg =
                   detailObject.getString(WeChatConstant.API_JSON_KEY_HEAD_IMG);
           JSONObject serviceTypeInfo =
                   detailObject.getJSONObject(WeChatConstant.API_JSON_KEY_SERVICE_TYPE_INFO);
           String serviceTypeInfoId = "";
           if(serviceTypeInfo != null){
               serviceTypeInfoId = serviceTypeInfo.getString(WeChatConstant.API_JSON_KEY_SERVICE_TYPE_ID);
           }
           String verifyTypeInfoId = "";
           JSONObject verifyTypeInfo =
                   detailObject.getJSONObject(WeChatConstant.API_JSON_KEY_VERIFY_TYPE_INFO);
           if(verifyTypeInfo != null){
               verifyTypeInfoId =
                       verifyTypeInfo.getString(WeChatConstant.API_JSON_KEY_VERIFY_TYPE_INFO);
           }
           String userName = detailObject.getString(WeChatConstant.API_JSON_KEY_USER_NAME);
           String principleName =
                   detailObject.getString(WeChatConstant.API_JSON_KEY_PRINCIPAL_NAME);
           JSONObject businessInfo =
                   detailObject.getJSONObject(WeChatConstant.API_JSON_KEY_BUSINESS_INFO);
           String bussinessInfoStr ="";
           if(businessInfo != null){
               bussinessInfoStr = businessInfo.toJSONString();
           }
           String alias = detailObject.getString(WeChatConstant.API_JSON_KEY_ALIAS);
           String qrCode = detailObject.getString(WeChatConstant.API_JSON_KEY_QRCODE_URL);
           logger.info(String.format("---------------qrCode:%s",qrCode));
           JSONObject authorizationInfo =
                   rootObj.getJSONObject(WeChatConstant.API_JSON_KEY_AUTH_INFO);
           StringBuilder funcInfo = new StringBuilder();
           JSONArray funcInfoArr =
                    authorizationInfo.getJSONArray(WeChatConstant.API_JSON_KEY_FUNC_INFO);
           if(funcInfoArr != null){
               for(int i = 0; i< funcInfoArr.size();i++){
                   logger.info(String.format("---------------funcInfo:%s",funcInfoArr.getJSONObject(i).toJSONString()));
                   funcInfo.append(
                           funcInfoArr.getJSONObject(i).getJSONObject(
                                   WeChatConstant.API_JSON_KEY_FUNCSCOPE_CATEGORY).
                                      getString(WeChatConstant.API_JSON_KEY_FUNC_ID)).
                           append(";");
               }
           }
           String funcInfoStr =funcInfo.toString();
            WechatPublicEntity publicEntity =
                    wechatPublicService.getEntityByAuthorizationAppid(authAppId);
            String publicId = publicEntity.getWechatPublicId();
            String detailId=
                  wechatPublicDetailService.getWechatPublicDetailIdByAppId(authAppId);

            if(StringUtils.isEmpty(detailId)){
              logger.info("----------新插入wechatpublicDetail--------------");
              wechatPublicDetailService.insertWechatPublicDetailEntity(publicId,nickName,
                      headImg,serviceTypeInfoId,verifyTypeInfoId,userName,principleName,
                      alias,bussinessInfoStr, qrCode,authAppId,funcInfoStr);
              logger.info("-----------新插入成功-----------");
          }else{
                logger.info("----------存在wechatpublicDetail，更新--------------");
                wechatPublicDetailService.updateWechatPublicDetail(
                        publicId,nickName,headImg,serviceTypeInfoId,verifyTypeInfoId,
                        userName,principleName,alias,bussinessInfoStr,qrCode,authAppId,funcInfoStr);
                logger.info("-----------更新成功--------------");
          }
        }
    }

    /**
     * *根据授权的微信公众号的appId，获取accessToken，
     * 并且将刷新后的token更新到缓存中（如果有必要）
     * 1.先从缓存中去accessToken，
     * 2.如果没有取到，或者redis中已过期，则调用接口重新刷新accessToken，和refreshToken
     * 并更新到缓存中去
     * 如果refresToken丢失，则抛出异常，提示需要重新授权
     * @author:     Logan
     * @param authAppId 公众号appid
     * @date:       2018/11/23 13:35
     * @return accessToken
     * @throws Exception
     */
    @Override
    public String getAuthAccessToken(String authAppId) throws Exception {
      //1.从缓存中取
        String accessToken =
                RedisFactory.get(
                        WeChatConstant.getAccessTokenCacheKey(authAppId));
        //token没有过期
        if(StringUtils.isNotEmpty(accessToken)){
            return accessToken;
        //token过期了
        }else{
            accessToken = refreshAccessToken(authAppId);
        }
        return accessToken;
    }

    /**
     * 刷新authorizerAccessToken
     * @date:       2018/11/23 13:43
     * @author:     Logan
     * @param authAppId 公众号appid
     * @return newAccessToken 新的AccessToken
     * @throws Exception
     */
    private String refreshAccessToken(String authAppId) throws Exception {
        String newAccessToken = "";

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
                            WeChatConstant.getRefreshTokenCacheKey(authAppId),newRefreshToken,-1);
                }
            }

        }else{
            throw new Exception("旧的AuthRefreshToken丢失，需要重新授权");

        }
       return newAccessToken;
    }

    /**
     * 解密第三方发送的xml
     * @author lxl
     * @param request request 内容
     * @param response response 内容
     * @throws Exception
     */
    @Override
    public void hadleAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String timestamp = request.getParameter("timestamp");
        String encryptType = request.getParameter("encrypt_type");
        String nonce = request.getParameter("nonce");
        String msgSignature = request.getParameter("msg_signature");
        try {
            String encodingAesKey = WeChatConstant.THIRD_PARTY_ENCODINGAESKEY;
            String appId = WeChatConstant.THIRD_PARTY_APPID;
            //解密
            Map<String, String> requestMap = WeChatUtil.parseRequest(request);
            WXBizMsgCrypt pc = new WXBizMsgCrypt(WeChatConstant.THIRD_PARTY_TOKEN, encodingAesKey, appId);
            String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
            String fromXML = String.format(format, requestMap.get("Encrypt"));
            String xml = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
            requestMap = WeChatUtil.parseXml(xml);
            String infoType = requestMap.get("InfoType");
            logger.info("InfoType: "+infoType);
            switch (infoType){
                //授权成功
                case "authorized":
                    logger.info("AuthorizerAppid: "+ requestMap.get("AuthorizerAppid"));
                    wechatPublicService.updateWechatPublicEntity(requestMap.get("AuthorizerAppid"),1);
                    break;
                //取消授权
                case "unauthorized":
                    logger.info("AuthorizerAppid: "+ requestMap.get("AuthorizerAppid"));
                    wechatPublicService.updateWechatPublicEntity(requestMap.get("AuthorizerAppid"),0);
                    break;
                //更新授权
                case "updateauthorized":
                    logger.info("AuthorizerAppid: "+ requestMap.get("AuthorizerAppid"));
                    wechatPublicService.updateWechatPublicEntity(requestMap.get("AuthorizerAppid"),2);
                    break;
                //得到tickon
                default:
                    //得到tickon,component_verify_ticket
                    String componentVerifyiTcket = requestMap.get("ComponentVerifyTicket");
                    //保存ticket到redis
                    WeChatUtil.setComponentVerifyTicket(componentVerifyiTcket);
                    componentVerifyiTcket = WeChatUtil.getComponentVerifyTicket();
                    //获取 第三方平台 Ticket 并且需保存到redis中
                    String componentAccessToken = WeChatUtil.getComponentAccessToken();
                    //获取预授权码pre_auth_code
//                    String preAuthCode = WeChatUtil.getPreAuthCode();
                    logger.info("-----------------component_verify_ticket : " + componentVerifyiTcket + "----------------------");
                    logger.info("-----------------component_access_token : " + componentAccessToken + "----------------------");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
