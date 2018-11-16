package data.driven.cm.business.taskBaby.impl;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskBaby.WeChatService;
import data.driven.cm.business.taskBaby.WechatPublicService;
import data.driven.cm.business.taskBaby.WechatUserInfoService;
import data.driven.cm.component.DuplicateRemovalMessage;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskBaby.ArticleItem;
import data.driven.cm.entity.taskBaby.WechatPublicEntity;
import data.driven.cm.util.WeChatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @Author: lxl
 * @describe
 * @Date: 2018/11/12 19:19
 * @Version 1.0
 */
@Service
public class WeChatServiceImpl  implements WeChatService {

    private static final int MESSAGE_CACHE_SIZE = 1000;
    private static List<DuplicateRemovalMessage> MESSAGE_CACHE = new ArrayList<DuplicateRemovalMessage>(MESSAGE_CACHE_SIZE);

    @Autowired
    private WechatPublicService wechatPublicService;

    @Autowired
    private WechatUserInfoService wechatUserInfoService;

    public String processRequest(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {



        // xml格式的消息数据
        String respXml = null;
        // 默认返回的文本消息内容
        String respContent;
        try {
            // 调用parseXml方法解析请求消息
            Map<String,String> requestMap = WeChatUtil.parseXml(request);
            String mes = null;
            String fromUserName = requestMap.get("FromUserName");
            String createTime = requestMap.get("CreateTime");
            String msgId = requestMap.get("MsgId");
            DuplicateRemovalMessage duplicateRemovalMessage = new DuplicateRemovalMessage();

            if (msgId != null) {
                duplicateRemovalMessage.setMsgId(msgId);
            } else {
                duplicateRemovalMessage.setCreateTime(createTime);
                duplicateRemovalMessage.setFromUserName(fromUserName);
            }

            if (MESSAGE_CACHE.contains(duplicateRemovalMessage)) {
                // 缓存中存在，直接pass
                mes = mes == null ? "不知道你在干嘛" : mes;
            }else{
                setMessageToCache(duplicateRemovalMessage);
                // 消息类型
                String msgType =  requestMap.get(WeChatConstant.MsgType);
                System.out.println(msgType);
                System.out.println(requestMap.get(WeChatConstant.Content));
                // 文本消息
                if (msgType.equals(WeChatConstant.REQ_MESSAGE_TYPE_TEXT)) {
                    mes =requestMap.get(WeChatConstant.Content).toString();
                    //调用王总的文本接口
                    //return new String(respXml.getBytes(),"ISO-8859-1");

                    if(mes!=null&&mes.length()<2){
                        List<ArticleItem> items = new ArrayList<>();
                        ArticleItem item = new ArticleItem();

                        item.setTitle("百度");
                        item.setDescription("百度一下");
                        item.setPicUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505100912368&di=69c2ba796aa2afd9a4608e213bf695fb&imgtype=0&src=http%3A%2F%2Ftx.haiqq.com%2Fuploads%2Fallimg%2F170510%2F0634355517-9.jpg");
//                    item.setUrl("http://www.baidu.com");
                        items.add(item);
                        requestMap.put(WeChatConstant.Reply_ToUserName,requestMap.get(WeChatConstant.FromUserName));
                        requestMap.put(WeChatConstant.Reply_FromUserName,requestMap.get(WeChatConstant.ToUserName));
                        respXml = WeChatUtil.sendArticleMsg(requestMap, items);
                    }
                    else if("我的信息".equals(mes)){

                        WechatPublicEntity wechatPublicEntity = wechatPublicService.getEntityByWechatAccount("gh_2d2266631fa7");
                        Map<String, String> userInfo = WeChatUtil.getUserInfo(requestMap.get(WeChatConstant.FromUserName),wechatPublicEntity.getAppid(), wechatPublicEntity.getSecret());

                        List<ArticleItem> items = new ArrayList<>();
                        String nickname = userInfo.get("nickname").toString();
                        String city = userInfo.get("city").toString();
                        String province = userInfo.get("province").toString();
                        String country = userInfo.get("country").toString();
                        String headimgurl = userInfo.get("headimgurl").toString();

                        ArticleItem item = new ArticleItem();
                        item.setTitle("你的信息");
                        item.setDescription("昵称:"+nickname+"  地址:"+country+" "+province+" "+city);
                        item.setPicUrl(headimgurl);
                        item.setUrl(userInfo.get("headimgurl").toString());
                        items.add(item);
                        requestMap.put(WeChatConstant.Reply_ToUserName,requestMap.get(WeChatConstant.FromUserName));
                        requestMap.put(WeChatConstant.Reply_FromUserName,requestMap.get(WeChatConstant.ToUserName));

                        respXml = WeChatUtil.sendArticleMsg(requestMap, items);

                    }
                    else if ("二维码".equals(mes)){
//                    WeChatUtil.getWXPublicQRCode("1",2592000,"QR_STR_SCENE","oH1q_0bt1c9GXWzdx3l9fRKRE6rk_123456",requestMap.get("FromUserName"),WeChatConstant.APPID,WeChatConstant.SECRET);
                        String codeUrl = WeChatUtil.getWXPublicQRCode("1",2592000,"QR_STR_SCENE","oH1q_0bt1c9GXWzdx3l9fRKRE6rk_123456", WeChatConstant.APPID, WeChatConstant.SECRET);
                        System.out.println("临时二维码URL "+codeUrl);
                        requestMap.put(WeChatConstant.Content,"已生成二维码，请找刘晓磊同学索要");
                        respXml = WeChatUtil.sendTextMsg(requestMap);

                    }else if("加图片".equals(mes)){
                        Map<String,Object> map = WeChatUtil.UploadMeida("image","dd", WeChatConstant.APPID, WeChatConstant.SECRET);
                        System.out.println("type "+map.get("type"));
                        System.out.println(map.get("media_id"));
                        System.out.println(map.get("created_at"));
                    }else if("图片".equals(mes)){
//                    Map<String,Object> map = new HashMap<>();
//                    map.put("ToUserName",requestMap.get(WeChatConstant.FromUserName));
//                    map.put("FromUserNam",requestMap.get(WeChatConstant.ToUserName));
                        requestMap.put("MediaId","N11YRmZ_2SBJtA306ft9DProkxYU3aY-28Lq_G8KA_JYyQtVLA46UF5X5Gm01yvZ");
                        respXml = WeChatUtil.sendImageMsg(requestMap);
                    }else if("客服".equals(mes)){
                        WechatPublicEntity wechatPublicEntity = wechatPublicService.getEntityByWechatAccount("gh_2d2266631fa7");

                        JSONObject testJson = new JSONObject();
                        JSONObject contentJson = new JSONObject();
                        testJson.put("touser",requestMap.get(WeChatConstant.FromUserName));
                        testJson.put("msgtype","text");
                        contentJson.put("content","齐狗大傻逼");
                        testJson.put("text",contentJson);
                        //发送客服信息-文本
                        WeChatUtil.sendCustomMsg(testJson,wechatPublicEntity.getAppid(),wechatPublicEntity.getSecret());

                        //发送客服信息-图片
                        JSONObject imageJson = new JSONObject();
                        JSONObject mediaIdJson = new JSONObject();
                        imageJson.put("touser",requestMap.get(WeChatConstant.FromUserName));
                        imageJson.put("msgtype","image");
                        mediaIdJson.put("media_id","N11YRmZ_2SBJtA306ft9DProkxYU3aY-28Lq_G8KA_JYyQtVLA46UF5X5Gm01yvZ");
                        imageJson.put("image",mediaIdJson);
                        WeChatUtil.sendCustomMsg(imageJson,wechatPublicEntity.getAppid(),wechatPublicEntity.getSecret());
                    }
                } else if (msgType.equals(WeChatConstant.REQ_MESSAGE_TYPE_IMAGE)) { // 图片消息
                    requestMap.put(WeChatConstant.Content,"您发送的是图片消息！");
                    respXml = WeChatUtil.sendTextMsg(requestMap);
                } else if (msgType.equals(WeChatConstant.REQ_MESSAGE_TYPE_VOICE)) { // 语音消息
                    requestMap.put(WeChatConstant.Content,"您发送的是语音消息！");
                    respXml = WeChatUtil.sendTextMsg(requestMap);
                } else if (msgType.equals(WeChatConstant.REQ_MESSAGE_TYPE_VIDEO)) { // 视频消息
                    requestMap.put(WeChatConstant.Content,"您发送的是视频消息！");
                    respXml = WeChatUtil.sendTextMsg(requestMap);
                } else if (msgType.equals(WeChatConstant.REQ_MESSAGE_TYPE_LOCATION)) { // 地理位置消息
                    requestMap.put(WeChatConstant.Content,"您发送的是地理位置消息！");
                    respXml = WeChatUtil.sendTextMsg(requestMap);
                } else if (msgType.equals(WeChatConstant.REQ_MESSAGE_TYPE_LINK)) { // 链接消息
                    requestMap.put(WeChatConstant.Content,"您发送的是链接消息！");
                    respXml = WeChatUtil.sendTextMsg(requestMap);
                } else if (msgType.equals(WeChatConstant.REQ_MESSAGE_TYPE_EVENT)) { // 事件推送
                    // 事件类型
                    String eventType =  requestMap.get(WeChatConstant.Event);
                    if (eventType.equals(WeChatConstant.EVENT_TYPE_SUBSCRIBE)) { // 关注
                        // 关注的时间默认返回活动信息,不用输入关键词
                        WechatPublicEntity wechatPublicEntity = wechatPublicService.getEntityByWechatAccount(requestMap.get(WeChatConstant.ToUserName));//通过原始Id得到公众号appid、secret
                        Map<String,String> userInfo = WeChatUtil.getUserInfo(requestMap.get(WeChatConstant.FromUserName),wechatPublicEntity.getAppid(),wechatPublicEntity.getSecret());



                    wechatUserInfoService.insertWechatUserInfoEntity(Integer.parseInt(userInfo.get("subscribe")),userInfo.get("openid"),userInfo.get("nickname"),Integer.parseInt(userInfo.get("sex")),
                            userInfo.get("country"),userInfo.get("province"),userInfo.get("language"),userInfo.get("headimgurl"),userInfo.get("unionid"),userInfo.get("remark"),
                            userInfo.get("subscribe_scene"),requestMap.get(WeChatConstant.ToUserName),Integer.parseInt(userInfo.get("subscribe_time")),userInfo.get("city"),Integer.parseInt(userInfo.get("qr_scene")),
                            userInfo.get("qr_scene_str"));
                        requestMap.put(WeChatConstant.Content,"谢谢您的关注！");
                        respXml = WeChatUtil.sendTextMsg(requestMap);
                    } else if (eventType.equals(WeChatConstant.EVENT_TYPE_UNSUBSCRIBE)) { // 取消关注
                        //取消关注时需要修改用户关注状态
                        System.out.println("用户已取消关注");
                        wechatUserInfoService.updateSubscribe(requestMap.get(WeChatConstant.ToUserName),requestMap.get(WeChatConstant.FromUserName),0);
                        // TODO 取消订阅后用户不会再收到公众账号发送的消息，因此不需要回复
                    } else if (eventType.equals(WeChatConstant.EVENT_TYPE_SCAN)) { // 扫描带参数二维码
                        System.out.println("通过二维码关注");
                        requestMap.put(WeChatConstant.Content,"谢谢您的关注！");
                        respXml = WeChatUtil.sendTextMsg(requestMap);
                        // TODO 处理扫描带参数二维码事件
                    } else if (eventType.equals(WeChatConstant.EVENT_TYPE_LOCATION)) { // 上报地理位置
                        // TODO 处理上报地理位置事件
                    } else if (eventType.equals(WeChatConstant.EVENT_TYPE_CLICK)) { // 自定义菜单
                        // TODO 处理菜单点击事件
                    }
                }
            }
            if(respXml == null){
                requestMap.put(WeChatConstant.Content,mes);
                respXml = WeChatUtil.sendTextMsg(requestMap);
            }
            System.out.println(respXml);
            return new String(respXml.getBytes(),"ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    private static void setMessageToCache(DuplicateRemovalMessage duplicateRemovalMessage) {
        if (MESSAGE_CACHE.size() >= MESSAGE_CACHE_SIZE) {
            MESSAGE_CACHE.remove(0);
        }
        MESSAGE_CACHE.add(duplicateRemovalMessage);
    }
}













