package data.driven.cm.component;

/**
 * @Author: lxl
 * @describe 微信公众号基础配置
 * @Date: 2018/11/12 19:23
 * @Version 1.0
 */
public class WeChatConstant {

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


}
