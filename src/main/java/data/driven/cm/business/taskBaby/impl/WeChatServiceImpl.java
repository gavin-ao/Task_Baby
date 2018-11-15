package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.WeChatService;
import data.driven.cm.component.WeChatContant;
import data.driven.cm.entity.taskBaby.ArticleItem;
import data.driven.cm.util.WeChatUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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

//    @Autowired
//    private FeignUtil feignUtil;
//    @Autowired
//    private RedisUtils redisUtils;
    public String processRequest(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
        // xml格式的消息数据
        String respXml = null;
        // 默认返回的文本消息内容
        String respContent;
        try {
            // 调用parseXml方法解析请求消息
            Map<String,String> requestMap = WeChatUtil.parseXml(request);

            for (Map.Entry<String,String> entry : requestMap.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
            // 消息类型
            String msgType =  requestMap.get(WeChatContant.MsgType);
            System.out.println(msgType);
            System.out.println(requestMap.get(WeChatContant.Content));

            String mes = null;

            // 文本消息
            if (msgType.equals(WeChatContant.REQ_MESSAGE_TYPE_TEXT)) {
                mes =requestMap.get(WeChatContant.Content).toString();
                //调用王总的文本接口
                //return new String(respXml.getBytes(),"ISO-8859-1");

                if(mes!=null&&mes.length()<2){
                    List<ArticleItem> items = new ArrayList<>();
                    ArticleItem item = new ArticleItem();

                    item.setTitle("百度");
                    item.setDescription("百度一下");
                    item.setPicUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1505100912368&di=69c2ba796aa2afd9a4608e213bf695fb&imgtype=0&src=http%3A%2F%2Ftx.haiqq.com%2Fuploads%2Fallimg%2F170510%2F0634355517-9.jpg");
                    item.setUrl("http://www.baidu.com");
                    items.add(item);
                    respXml = WeChatUtil.sendArticleMsg(requestMap, items);
                }
                else if("我的信息".equals(mes)){
                    Map<String, String> userInfo = WeChatUtil.getUserInfo(requestMap.get(WeChatContant.FromUserName),WeChatContant.APPID,WeChatContant.SECRET);
                    System.out.println(userInfo.toString());
                    List<ArticleItem> items = new ArrayList<>();
                    if ("true".equals(userInfo.get("success"))){
                        String nickname = userInfo.get("nickname");
                        String city = userInfo.get("city");
                        String province = userInfo.get("province");
                        String country = userInfo.get("country");
                        String headimgurl = userInfo.get("headimgurl");

                        ArticleItem item = new ArticleItem();
                        item.setTitle("你的信息");
                        item.setDescription("昵称:"+nickname+"  地址:"+country+" "+province+" "+city);
                        item.setPicUrl(headimgurl);
                        item.setUrl(userInfo.get("headimgurl"));
                        items.add(item);

                        respXml = WeChatUtil.sendArticleMsg(requestMap, items);
                    }else{
                        requestMap.put("Content","没查到");
                        respXml = WeChatUtil.sendTextMsg(requestMap);
                    }
                }else if ("二维码".equals(mes)){
//                    WeChatUtil.getWXPublicQRCode("1",2592000,"QR_STR_SCENE","oH1q_0bt1c9GXWzdx3l9fRKRE6rk_123456",requestMap.get("FromUserName"),WeChatContant.APPID,WeChatContant.SECRET);
                    String codeUrl = WeChatUtil.getWXPublicQRCode("1",2592000,"QR_STR_SCENE","oH1q_0bt1c9GXWzdx3l9fRKRE6rk_123456",WeChatContant.APPID,WeChatContant.SECRET);
                    System.out.println("临时二维码URL "+codeUrl);
                    requestMap.put(WeChatContant.Content,"已生成二维码，请找刘晓磊同学索要");
                    respXml = WeChatUtil.sendTextMsg(requestMap);

                }else if("加图片".equals(mes)){
                    Map<String,Object> map = WeChatUtil.UploadMeida("image","dd",WeChatContant.APPID,WeChatContant.SECRET);
                    System.out.println("type "+map.get("type"));
                    System.out.println(map.get("media_id"));
                    System.out.println(map.get("created_at"));
                }else if("图片".equals(mes)){
//                    Map<String,Object> map = new HashMap<>();
//                    map.put("ToUserName",requestMap.get(WeChatContant.FromUserName));
//                    map.put("FromUserNam",requestMap.get(WeChatContant.ToUserName));
                    requestMap.put("MediaId","N11YRmZ_2SBJtA306ft9DProkxYU3aY-28Lq_G8KA_JYyQtVLA46UF5X5Gm01yvZ");
                    respXml = WeChatUtil.sendImageMsg(requestMap);
                }
            } else if (msgType.equals(WeChatContant.REQ_MESSAGE_TYPE_IMAGE)) { // 图片消息
                requestMap.put(WeChatContant.Content,"您发送的是图片消息！");
                respXml = WeChatUtil.sendTextMsg(requestMap);
            } else if (msgType.equals(WeChatContant.REQ_MESSAGE_TYPE_VOICE)) { // 语音消息
                requestMap.put(WeChatContant.Content,"您发送的是语音消息！");
                respXml = WeChatUtil.sendTextMsg(requestMap);
            } else if (msgType.equals(WeChatContant.REQ_MESSAGE_TYPE_VIDEO)) { // 视频消息
                requestMap.put(WeChatContant.Content,"您发送的是视频消息！");
                respXml = WeChatUtil.sendTextMsg(requestMap);
            } else if (msgType.equals(WeChatContant.REQ_MESSAGE_TYPE_LOCATION)) { // 地理位置消息
                requestMap.put(WeChatContant.Content,"您发送的是地理位置消息！");
                respXml = WeChatUtil.sendTextMsg(requestMap);
            } else if (msgType.equals(WeChatContant.REQ_MESSAGE_TYPE_LINK)) { // 链接消息
                requestMap.put(WeChatContant.Content,"您发送的是链接消息！");
                respXml = WeChatUtil.sendTextMsg(requestMap);
            } else if (msgType.equals(WeChatContant.REQ_MESSAGE_TYPE_EVENT)) { // 事件推送
                // 事件类型
                String eventType =  requestMap.get(WeChatContant.Event);
                if (eventType.equals(WeChatContant.EVENT_TYPE_SUBSCRIBE)) { // 关注
                    // 关注的时间默认返回活动信息,不用输入关键词
                    requestMap.put(WeChatContant.Content,"谢谢您的关注！");
                    respXml = WeChatUtil.sendTextMsg(requestMap);
                } else if (eventType.equals(WeChatContant.EVENT_TYPE_UNSUBSCRIBE)) { // 取消关注
                    //取消关注时需要修改用户关注状态
                    System.out.println("用户已取消关注");
                    // TODO 取消订阅后用户不会再收到公众账号发送的消息，因此不需要回复
                } else if (eventType.equals(WeChatContant.EVENT_TYPE_SCAN)) { // 扫描带参数二维码
                    System.out.println("通过二维码关注");
                    requestMap.put(WeChatContant.Content,"谢谢您的关注！");
                    respXml = WeChatUtil.sendTextMsg(requestMap);
                    // TODO 处理扫描带参数二维码事件
                } else if (eventType.equals(WeChatContant.EVENT_TYPE_LOCATION)) { // 上报地理位置
                    // TODO 处理上报地理位置事件
                } else if (eventType.equals(WeChatContant.EVENT_TYPE_CLICK)) { // 自定义菜单
                    // TODO 处理菜单点击事件
                }
            }
            mes = mes == null ? "不知道你在干嘛" : mes;
            if(respXml == null){
                requestMap.put(WeChatContant.Content,mes);
                respXml = WeChatUtil.sendTextMsg(requestMap);
            }
            System.out.println(respXml);
            return new String(respXml.getBytes(),"ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}












