package data.driven.cm.business.taskbaby;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lxl
 * @describe 第三方平台Service
 * @Date: 2018/11/23 9:24
 * @Version 1.0
 */
public interface ThirdPartyService {
    /**
     * 解密第三方发送的xml
     * @author lxl
     * @param request request 内容
     * @param response response 内容
     * @throws Exception
     */
    void hadleAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception;


    /**
     * 处理授权后的回调服务
     * @author: Logan
     * @date: 2018/11/23 11:24
     * @param authCode 公众号授权码
     */
    void saveCallbackAuthInfo(String authCode);

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
    String getAuthAccessToken(String authAppId) throws Exception;
}
