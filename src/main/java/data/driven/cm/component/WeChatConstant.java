package data.driven.cm.component;

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
    public static final String TOKEN = "lxl";
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
    public static final String ToUserName = "ToUserName";
    public static final String MsgType = "MsgType";
    public static final String Content = "Content";
    public static final String Event = "Event";
    public static final String MediaId = "MediaId";
    public static final String MEDIA_ID= "media_id"; //用于得到返回临时素材的media_id
    public static final String EventKey = "EventKey";
    public static final String QREventKeyPrefix = "qrscene_";//扫描二维码，eventkey的前缀未qrscene_
}
