package data.driven.cm.business.taskBaby;

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
     * @param request
     * @param response
     * @throws Exception
     */
    public void hadleAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception;
    /**
     * 处理授权后的回调服务
     * @author:     Logan
     * @date:       2018/11/23 11:24
     * @params:     [authCode]
     * @return:     void
    **/
    public void saveCallbackAuthInfo(String authCode);

    /**
     * 根据授权的微信公众号appID，获取AuthAccessToken，
     * 并将刷新后的保存到缓存中
     * 如果refresToken丢失，则抛出异常，提示需要重新授权
     * @author:     Logan
     * @date:       2018/11/23 14:11
     * @params:     [authAppId]
     * @return:     java.lang.String
    **/
    public String getAuthAccessToken(String authAppId) throws Exception;
}
