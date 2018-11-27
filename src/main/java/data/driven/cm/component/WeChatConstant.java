package data.driven.cm.component;

import data.driven.cm.common.RedisFactory;

/**
 * @Author: lxl
 * @describe 微信公众号基础配置
 * @Date: 2018/11/12 19:23
 * @Version 1.0
 */
public class WeChatConstant {

    /** 公众号APPID **/
    public static final String APPID = "APPID";
    /** 公众号签名appsecret **/
    public static final String SECRET = "SECRET";

    // Token
    public static final String TOKEN = "xkb";
    public static final String EncodingAESKey = "CJpofN6kdTJmCwXksXL3NYV4Oi2yw69CEwUPNjnU4De";

    //第三方平台
    //第三方平台AppID
    public static final String THIRD_PARTY_APPID = "wxe67b87e6f254b78d";
    //第三方平台消息加解密Key
    public static final String THIRD_PARTY_ENCODINGAESKEY = "123456789xinkebao123456789xinkebao123456789";
    //第三方平台消息校验Token
    public static final String THIRD_PARTY_TOKEN = "xkbXKB123";
    //第三方平台AppSecret
    public static final String THIRD_PARTY_SECRET = "b10c69ded74b945b7d1cb6c6a03501d1";
    //第三方平台接口调用凭据
    public static final String THIRD_PARTY_TICKET = "third_party_ticket";
    //第三方平台component_access_token
    public static final String THIRD_PARTY_ACCESS_TOKEN = "third_party_access_token";
    //第三方平台 pre_auth_code
    public static final String THIRD_PARTY_PRE_AUTH_CODE = "pre_auth_code";


    public static final String RESP_MESSAGE_TYPE_TEXT = "text";
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
    public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
    public static final String REQ_MESSAGE_TYPE_LINK = "link";
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    public static final String HTTP_HEAD="http";
    public static final String EVENT_TYPE_SCAN = "SCAN";
    public static final String EVENT_TYPE_LOCATION = "LOCATION";
    public static final String EVENT_TYPE_CLICK = "CLICK";

    public static final String FromUserName = "FromUserName";
    public static final String Reply_FromUserName = "ReplyFromUserName";
    public static final String ToUserName = "ToUserName";
    public static final String Reply_ToUserName = "ReplyToUserName";
    public static final String MsgType = "MsgType";
    public static final String Content = "Content";
    public static final String Event = "Event";
    public static final String MediaId = "MediaId";
    public static final String MEDIA_ID= "media_id"; //用于得到返回临时素材的media_id
    public static final String EventKey = "EventKey";
    public static final String QREventKeyPrefix = "qrscene_";//扫描二维码，eventkey的前缀未qrscene_

   //客服消息常量
    public static final String KEY_CSMSG_TOUSER ="CSMSG_TOUSER";
    public static final String KEY_CSMSG_TYPE ="CSMSG_TYPE";
    public static final String KEY_CSMSG_CONTENT="CSMSG_CONTENT";
    public static final String VALUE_CSMSG_TYPE_TEXT ="text";
    public static final String VALUE_CSMSG_TYPE_IMG ="image";


    //用户个人信息的key；
    public static final String KEY_HEADIMG_URL="headimgurl";
    public static final String KEY_NICKNAME="nickname";
    public static final String KEY_FILE_PATH ="filePath";
    public static final String KEY_APP_ID ="appId";
    public static final String KEY_SECRET_CODE="secretCode";
    public static final String KEY_MEDIA_ID="media_id";

    //微信接口json参数的names
    public static final String API_JSON_KEY_COMPONET_APPID="component_appid";
    public static final String API_JSON_KEY_AUTH_CODE="authorization_code";
    public static final String API_JSON_KEY_AUTH_INFO="authorization_info";
    public static final String API_JSON_KEY_AUTH_APPID="authorizer_appid";
    public static final String API_JSON_KEY_AUTH_ACCESS_TOKEN="authorizer_access_token";
    public static final String API_JSON_KEY_AUTH_EXPIRES_IN="expires_in";
    public static final String API_JSON_KEY_AUTH_REFRESH_TOKEN="authorizer_refresh_token";
    public static final String API_JSON_KEY_FUNC_INFO = "func_info";
    public static final String API_JSON_KEY_FUNCSCOPE_CATEGORY= "funcscope_category";
    public static final String API_JSON_KEY_FUNC_ID= "id";
    public static final String API_JSON_KEY_AUTHORIZER_INFO ="authorizer_info";
    public static final String API_JSON_KEY_NICK_NAME="nick_name";
    public static final String API_JSON_KEY_HEAD_IMG="head_img";
    public static final String API_JSON_KEY_SERVICE_TYPE_INFO="service_type_info";
    public static final String API_JSON_KEY_VERIFY_TYPE_INFO="verify_type_info";
    public static final String API_JSON_KEY_USER_NAME="user_name";
    public static final String API_JSON_KEY_PRINCIPAL_NAME="principal_name";
    public static final String API_JSON_KEY_ALIAS="alias";
    public static final String API_JSON_KEY_BUSINESS_INFO="business_info";
    public static final String API_JSON_KEY_QRCODE_URL="qrcode_url";
    public static final String API_JSON_KEY_AUTHORIZATION_APPID="authorization_appid";
    public static final String API_JSON_KEY_SERVICE_TYPE_ID= "id";
    public static final String API_JSON_KEY_VERIFY_TYPE_ID= "id";

    //微信接口api地址

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     * @author:     Logan
     * @date:       2018/11/23 10:32
     * @params:     [thirdPartyAccessToken] 第三放平台的accessToken
     * @return:     授权信息api地址
    **/
    public static String getAPIAddressAuthInfoURL(String thirdPartyAccessToken){
      String API_ADDRESS_AUTH_INFO=
                "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=%s";
        return String.format(API_ADDRESS_AUTH_INFO,thirdPartyAccessToken);
    }

    /**
     *  获取预授权码
     * @param thirdPartyAccessToken
     * @return
     */
    public static String getPreAuthCode(String thirdPartyAccessToken){
        String preAuthCode_URL =
                "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=%s";
        return String.format(preAuthCode_URL,thirdPartyAccessToken);
    }
    //获取第三方平台component_access_token URL
    public static final String COMPONENT_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";


    //REDIS KEY
    public static String getAuthCodeCacheKey(String  appid){
        //授权码 Key
        String CACHE_KEY_AUTH_CODE="AUTH_CODE_%s";//%s:微信公众号appid;
        return String.format(CACHE_KEY_AUTH_CODE,appid);
    }
   public static String getAccessTokenCacheKey(String appid){
       //微信公众号访问凭证 key
        String CACHE_KEY_ACCESS_TOKEN="ACCESS_TOKEN_%s";//%s:微信公众号appid；
       return String.format(CACHE_KEY_ACCESS_TOKEN,appid);
    }
   public static String getRefreshTokenCacheKey(String appid){
       //微信公众号访问刷新凭证 key
        String CACHE_KEY_REFRESH_ACCESS_TOKEN="REFRESH_ACCESS_TOKEN_%s";//%s:微信公众号appid；
        return String.format(CACHE_KEY_REFRESH_ACCESS_TOKEN,appid);
   }
    public static String getRefreshTokenURL(String thirdPartyAccessToken){
        String API_REFRESH_TOKEN_URL=
                "https:// api.weixin.qq.com /cgi-bin/component/api_authorizer_token?component_access_token=%s";
        return String.format(API_REFRESH_TOKEN_URL,thirdPartyAccessToken);
    }

    //REDIS VALUE
    //授权码过期时间
    public static int CACHE_VALUE_EXPIRE_AUTH_CODE= 60*10-60;//比微信平台少预留60s
    //access_token 过期时间
    public static int CACHE_VALUE_EXPIRE_ACCESS_TOKEN =60*60*2-300;//比微信平台少预留5分钟时间
    //pre_auth_code 过期时间
    public static int CATCE_VALUE_EXPIRE_PRE_AUTH_CODE = 550 ;//比微信平台10分钟少预留60s
    //第三方 component_access_token
    public static int CATCE_VALUE_EXPIRE_COMPONENT_ACCESS_TOKEN = 7000 ; //比微信平台2小时少200s


}
