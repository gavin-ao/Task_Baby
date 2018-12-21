package data.driven.cm.component;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.util.HttpUtil;
import data.driven.cm.util.WeChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * @Author: lxl
 * @describe 微信公众号基础配置
 * @Date: 2018/11/12 19:23
 * @Version 1.0
 */
public class WeChatConstant {
    private static final Logger log = LoggerFactory.getLogger(WeChatConstant.class);
    /**
     * 加密类型
     */
    public static final String AES = "aes";

    /**
     * 第三方平台AppID
     */
    public static final String THIRD_PARTY_APPID = "wxe67b87e6f254b78d";
    /**
     * 第三方平台消息加解密Key
     */
    public static final String THIRD_PARTY_ENCODINGAESKEY = "123456789xinkebao123456789xinkebao123456789";
    /**
     * 第三方平台消息校验Token
     */
    public static final String THIRD_PARTY_TOKEN = "xkbXKB123";
    /**
     * 第三方平台AppSecret
     */
    public static final String THIRD_PARTY_SECRET = "b10c69ded74b945b7d1cb6c6a03501d1";
    /**
     * 进度模板消息的模板id
     */
    public static final String PROGRESS_TEMPLATE_MSG_TEMPLATE_ID="CoiW6GOy7xeRNXk5PZDJ4OcRMotyL1rfBnchk9inHkU";
    /**
     * 第三方平台接口调用凭据
     */
    public static final String THIRD_PARTY_TICKET = "third_party_ticket";
    /**
     * 第三方平台component_access_token
     */
    public static final String THIRD_PARTY_ACCESS_TOKEN = "third_party_access_token";
    /**
     * 第三方平台 pre_auth_code
     */
    public static final String THIRD_PARTY_PRE_AUTH_CODE = "pre_auth_code";


    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    public static final String HTTP_HEAD = "http";
    public static final String EVENT_TYPE_SCAN = "SCAN";

    public static final String FROM_USER_NAME = "FromUserName";
    public static final String REPLY_FROM_USER_NAME = "ReplyFromUserName";
    public static final String TO_USER_NAME = "ToUserName";
    public static final String REPLY_TO_USER_NAME = "ReplyToUserName";
    public static final String MSG_TYPE = "MsgType";
    public static final String CONTENT = "Content";
    public static final String EVENT = "Event";
    public static final String MEDIA_ID = "MediaId";
    public static final String EVENT_KEY = "EventKey";

    /**
     * 客服消息常量
     */
    public static final String KEY_CSMSG_TOUSER = "CSMSG_TOUSER";
    public static final String KEY_CSMSG_TYPE = "CSMSG_TYPE";
    public static final String KEY_CSMSG_CONTENT = "CSMSG_CONTENT";
    public static final String VALUE_CSMSG_TYPE_TEXT = "text";
    public static final String VALUE_CSMSG_TYPE_IMG = "image";


    /**
     * 用户个人信息的key
     */
    public static final String KEY_HEADIMG_URL = "headimgurl";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_FILE_PATH = "filePath";
    public static final String KEY_MEDIA_ID = "media_id";

    /**
     * 微信接口json参数的names
     */
    public static final String API_JSON_KEY_COMPONET_APPID = "component_appid";
    public static final String API_JSON_KEY_AUTH_CODE = "authorization_code";
    public static final String API_JSON_KEY_AUTH_INFO = "authorization_info";
    public static final String API_JSON_KEY_AUTH_APPID = "authorizer_appid";
    public static final String API_JSON_KEY_AUTH_ACCESS_TOKEN = "authorizer_access_token";
    public static final String API_JSON_KEY_AUTH_REFRESH_TOKEN = "authorizer_refresh_token";
    public static final String API_JSON_KEY_FUNC_INFO = "func_info";
    public static final String API_JSON_KEY_FUNCSCOPE_CATEGORY = "funcscope_category";
    public static final String API_JSON_KEY_FUNC_ID = "id";
    public static final String API_JSON_KEY_AUTHORIZER_INFO = "authorizer_info";
    public static final String API_JSON_KEY_NICK_NAME = "nick_name";
    public static final String API_JSON_KEY_HEAD_IMG = "head_img";
    public static final String API_JSON_KEY_SERVICE_TYPE_INFO = "service_type_info";
    public static final String API_JSON_KEY_VERIFY_TYPE_INFO = "verify_type_info";
    public static final String API_JSON_KEY_USER_NAME = "user_name";
    public static final String API_JSON_KEY_PRINCIPAL_NAME = "principal_name";
    public static final String API_JSON_KEY_ALIAS = "alias";
    public static final String API_JSON_KEY_BUSINESS_INFO = "business_info";
    public static final String API_JSON_KEY_QRCODE_URL = "qrcode_url";
    public static final String API_JSON_KEY_SERVICE_TYPE_ID = "id";
    public static final String API_JSON_KEY_UNIONID="unionid";


    /**
     * 消息类微信API常量
     */

    public static final String API_JSON_KEY_TOUSER = "touser";
    public static final String API_JSON_KEY_TEMPLATE_ID ="template_id";
    public static final String API_JSON_KEY_URL = "url";
    public static final String API_JSON_KEY_DATA = "data";
    public static final String API_JSON_KEY_FIRST = "first";
    public static final String API_JSON_KEY_KEYWORD1 = "keyword1";
    public static final String API_JSON_KEY_KEYWORD2 = "keyword2";
    public static final String API_JSON_KEY_KEYWORD3 = "keyword3";

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     *
     * @author: Logan
     * @date: 2018/11/23 10:32
     * @params: [thirdPartyAccessToken] 第三放平台的accessToken
     * @return: 授权信息api地址
     **/
    public static String getAPIAddressAuthInfoURL(String thirdPartyAccessToken) {
        String apiAddressAuthInfo =
                "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=%s";
        return String.format(apiAddressAuthInfo, thirdPartyAccessToken);
    }

    /**
     * 获取预授权码
     *
     * @param thirdPartyAccessToken
     * @return
     */
    public static String getPreAuthCode(String thirdPartyAccessToken) {
        String preAuthCodeUrl =
                "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=%s";
        return String.format(preAuthCodeUrl, thirdPartyAccessToken);
    }

    /**
     * 获取第三方平台component_access_token URL
     */
    public static final String COMPONENT_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";


    //REDIS KEY

    /**
     *  拼接AUTH_CODE_信息
     * @param appid 公众号appid
     * @return 返回拼接好的信息
     */
    public static String getAuthCodeCacheKey(String appid) {
        //授权码 Key
        //%s:微信公众号appid;
        String cacheKeyAuthCode = "AUTH_CODE_%s";
        return String.format(cacheKeyAuthCode, appid);
    }

    public static String getAccessTokenCacheKey(String appid) {
        //微信公众号访问凭证 key
        //%s:微信公众号appid；
        String cachKeyAccessToken = "ACCESS_TOKEN_%s";
        return String.format(cachKeyAccessToken, appid);
    }

    public static String getRefreshTokenCacheKey(String appid) {
        //微信公众号访问刷新凭证 key
        //%s:微信公众号appid；
        String cacheKeyRefreshAccessToken = "REFRESH_ACCESS_TOKEN_%s";
        return String.format(cacheKeyRefreshAccessToken, appid);
    }

    public static String getRefreshTokenURL(String thirdPartyAccessToken) {
        String apiRefreshTokenUrl =
                "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=%s";
        return String.format(apiRefreshTokenUrl, thirdPartyAccessToken);
    }

    //REDIS VALUE
    /**
     * 授权码过期时间
     * 比微信平台少预留60s
     */
    public static int CACHE_VALUE_EXPIRE_AUTH_CODE = 60 * 10 - 60;
    /**
     * access_token 过期时间
     * 比微信平台少预留5分钟时间
     */
    public static int CACHE_VALUE_EXPIRE_ACCESS_TOKEN = 60 * 60 * 2 - 300;
    /**
     * pre_auth_code 过期时间
     * 比微信平台10分钟少预留60s
     */
    public static int CATCE_VALUE_EXPIRE_PRE_AUTH_CODE = 550;
    /**
     * 第三方 component_access_token
     * 比微信平台2小时少200s
     */
    public static int CATCE_VALUE_EXPIRE_COMPONENT_ACCESS_TOKEN = 7000;

    /**
     * 获取微信用户的信息
     */

    /**
     * @description 获取用户特殊access_token，openid
     * @author lxl
     * @date 2018-12-19 15:56
     * @param appId 服务号的appid
     * @param  code code作为换取access_token的票据
     * @Param thirdPartyAccessToken 第三方平台自己的accessToken
     * @return
     */
    public static String getUserAccessTokenURL(String appId,String code,String thirdPartyAccessToken) {
        String key = "access_token_"+code+appId;
        String refreshKey = "refresh_token_"+code+appId;
        String accessToken = RedisFactory.get(key);
        String refreshToken = RedisFactory.get(refreshKey);
        log.info("-----------用户服务号的access_Token ------------- "+accessToken + "  end");
        log.info("-----------用户服务号的refreshToken ------------- "+refreshToken + "  end");
        if (accessToken != null && accessToken.trim().length() > 0){
            return accessToken;
        }
        //如果用户刷新access_token存在则重新请求用户的access_token,否则跳过
        if (refreshToken != null && refreshToken.trim().length() > 0){
            String getUserRefreshToeknUrl = "https://api.weixin.qq.com/sns/oauth2/component/refresh_token?appid=%s" +
                    "&grant_type=refresh_token&component_appid=%s&component_access_token=%s&refresh_token=%s";
            String resultStr = HttpUtil.doGetSSL(String.format(getUserRefreshToeknUrl, appId,
                    WeChatConstant.THIRD_PARTY_APPID, thirdPartyAccessToken,refreshToken));
            if(resultStr == null){
                log.info("------------------获取服务号access_token失败！-----------------");
                return "";
            }
            JSONObject resultJson = parseObject(resultStr);
            log.info("------------------resultJson -----------------" + resultJson.toString());
            if (resultJson.getString("access_token") != null &&
                    resultJson.getString("access_token").trim().length() > 0){
                log.info("------------------获取服务号access_token: "+resultJson.getString("access_token")+"-----------------");
                RedisFactory.setString(key,resultStr,WeChatConstant.CATCE_VALUE_EXPIRE_COMPONENT_ACCESS_TOKEN * 1000);
                //用户刷新token保存为30天
                RedisFactory.setString(refreshKey,resultJson.getString("refresh_token"),2591000 * 1000);
            }
            return resultStr;
        }
        // 应该调用第三方平台制定的接口,而非微信公众平台文档上的接口  Logan 2018-12-20  17:22
        String getUserAccessTokenURL =
                "https://api.weixin.qq.com/sns/oauth2/component/access_token?" +
                        "appid=%s&code=%s&grant_type=authorization_code&" +
                        "component_appid=%s&component_access_token=%s";
        String resultStr = HttpUtil.doGetSSL(String.format(getUserAccessTokenURL, appId,code, WeChatConstant.THIRD_PARTY_APPID,thirdPartyAccessToken));
        if(resultStr == null){
            log.info("------------------获取服务号access_token失败！-----------------");
            return "";
        }
        JSONObject resultJson = parseObject(resultStr);
        log.info("------------------resultJson -----------------" + resultJson.toString());
        if (resultJson.getString("access_token") != null &&
                resultJson.getString("access_token").trim().length() > 0){
            log.info("------------------获取服务号access_token: "+resultJson.getString("access_token")+"-----------------");
            RedisFactory.setString(key,resultStr,WeChatConstant.CATCE_VALUE_EXPIRE_COMPONENT_ACCESS_TOKEN * 1000);
            //用户刷新token保存为30天
            RedisFactory.setString(refreshKey,resultJson.getString("refresh_token"),2591000L * 1000);
        }
        return resultStr;
    }

    /**
     * @description 获取用户
     * @author lxl
     * @date 2018-12-19 16:21
     * @param accessToken 用户的access_token
     * @param openid 用户openid
     * @return
     */
    public static String getUserInfoURL(String accessToken,String openid) {
        String getUserInfoURL =
                "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
        return String.format(getUserInfoURL,accessToken,openid);
    }

}
