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

    //第三方平台appid、EncodingAESKey
    public static final String THIRD_PARTY_APPID = "wxe67b87e6f254b78d";
    public static final String THIRD_PARTY_ENCODINGAESKEY = "123456789xinkebao123456789xinkebao123456789";
    public static final String THIRD_PARTY_TOKEN = "xkbXKB123";
    public static final String THIRD_PARTY_SECRET = "a735ffc4ee824360243ec8cfb52c909d";


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


    //REDIS KEY
    //预授权码 key
    public static String CACHE_KEY_PRE_AUTH_CODE="ACCESS_TOKEN_%s";//%s:微信公众号appid；
    //授权码 Key
    public static String CACHE_KEY_AUTH_CODE="AUTH_CODE_%s";//%s:微信公众号appid;
   //微信公众号访问凭证 key
    public static String CACHE_KEY_ACCESS_TOKEN="ACCESS_TOKEN_%s";//%s:微信公众号appid；
    //微信公众号访问刷新凭证 key
    public static String CACHE_KEY_REFRESH_ACCESS_TOKEN="REFRESH_ACCESS_TOKEN_%s";//%s:微信公众号appid；

    //REDIS VALUE
    //授权码过期时间
    public static int CACHE_VALUE_EXPIRE_AUTH_CODE= 60*10-60;//比微信平台少预留60s
    //access_token 过期时间
    public static int CATCH_VALUE_EXPIRE_ACCESS_TOKEN =60*60*2-300;//比微信平台少预留5分钟时间
    //refresh_access_token 过期时间
    public static int CATCH_VALUE_EXPIRE_REFRESH_ACCESS_TOKEN =60*60*2-300;//比微信平台少预留5分钟时间
}
