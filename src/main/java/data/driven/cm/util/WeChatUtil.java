package data.driven.cm.util;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskBaby.ArticleItem;
import data.driven.cm.entity.wechat.WechatCSImgMsgEntity;
import data.driven.cm.entity.wechat.WechatCSTxtMsgEntity;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.fastjson.JSON.parseObject;
import static data.driven.cm.component.WeChatConstant.*;

/**
 * @Author: lxl
 * @describe
 * @Date: 2018/11/12 19:16
 * @Version 1.0
 */
public class WeChatUtil {
    private static final Logger log = LoggerFactory.getLogger(WeChatUtil.class);

    /**微信用户**/
    private static final String user_url = "https://api.weixin.qq.com/cgi-bin/user/info";
    /**生成代参二维码**/
    private static final String qrcode_url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
    /**获取二维码**/
    private static final String showqr_url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
    /** 客服接口-发消息 **/
    private static final String custom_url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";


    public static final String QR_TYPE_TEMPORARY ="1";//临时码
    public static final String QR_TYPE_PERMANENT ="2";//永久码；
    public static final int QR_MAX_EXPIREDTIME = 2592000; //临时二维码最大过期时间，单位是秒，30天
    public static final String QR_SCENE_NAME_ID = "QR_SCENE";
    public static final String QR_SCENE_NAME_STR = "QR_STR_SCENE";




    /**
     * 验证签名
     *
     * @param signature
     * @param timestamp
     * @param nonce
     *
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[] { WeChatConstant.TOKEN, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        // Arrays.sort(arr);
        sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    private static void sort(String a[]) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].compareTo(a[i]) < 0) {
                    String temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }

    /**
     * 解析微信发来的请求(xml)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked"})
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap<String,String>();

        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());

        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
    }

    /**
     * 组装 xml
     * @param map
     * @return 返回 字符串信息
     */
    public static String mapToXML(Map map) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        mapToXML2(map, sb);
        sb.append("</xml>");
        try {
            return sb.toString();
        } catch (Exception e) {
        }
        return null;
    }

    private static void mapToXML2(Map map, StringBuffer sb) {
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);
                sb.append("<" + key + ">");
                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    mapToXML2(hm, sb);
                }
                sb.append("</" + key + ">");

            } else {
                if (value instanceof HashMap) {
                    sb.append("<" + key + ">");
                    mapToXML2((HashMap) value, sb);
                    sb.append("</" + key + ">");
                } else {
                    sb.append("<" + key + "><![CDATA[" + value + "]]></" + key + ">");
                }

            }

        }
    }
    /**
     * 回复文本消息
     * @param requestMap
     *  map 参数说明
     *      ToUserName 接收方帐号（收到的OpenID）
     *      FromUserName 开发者微信号
     *      Content 要发送的内容
     * @return 返回 String 类型的 xml
     */
    public static String sendTextMsg(Map<String,String> requestMap){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatConstant.Reply_ToUserName));
        map.put("FromUserName",  requestMap.get(WeChatConstant.Reply_FromUserName));
        map.put("MsgType", WeChatConstant.RESP_MESSAGE_TYPE_TEXT);
        map.put("CreateTime", new Date().getTime());
        map.put("Content", requestMap.get(WeChatConstant.Content));
        return  mapToXML(map);
    }

    /**
     *  通过客服接口发送文本和图片
     * @param testJson 需要发送的信息
     *          文本：
     *                 {
                            "touser":"OPENID",
                            "msgtype":"text",
                            "text":
                            {
                            "content":"Hello World"
                            }
                        }
                 图片：
                        {
                            "touser":"OPENID",
                            "msgtype":"image",
                            "image":
                            {
                            "media_id":"MEDIA_ID"
                            }
                        }
     * @param appId
     * @param secret
     */
    public static void sendCustomMsg(JSONObject testJson,String appId,String secret){
        JSONObject jsonObject = WXUtil.getAccessToken(appId,secret);
        String access_token = jsonObject.getString("access_token");
        String url = custom_url+access_token;
        HttpUtil.doPost(url, testJson.toJSONString());
    }
    /**
     * 
     * @author:     Logan
     * @date:       2018/11/16 03:48 
     * @params:     [JsonStr, appId, secret]   
     * @return:     void
    **/        
    public static void sendCustomMsgByJsonStr(String JsonStr, String appId,String secret){
        JSONObject jsonObject = WXUtil.getAccessToken(appId,secret);
        String access_token = jsonObject.getString("access_token");
        String url = custom_url+access_token;
        HttpUtil.doPost(url, JsonStr);
    }
/**
 * 根据Map发送客服消息
 * @author:     Logan
 * @date:       2018/11/16 02:45
 * @params:     [requestMap]
 * touser:粉丝的openId
 * msgtype:  消息类型text/image
 * filePath:海报的路径
 * appId:
 * secret:
 *
 * @return:     void
**/
    public static void sendCustomMsg(Map<String,String> requestMap){
        String touser = requestMap.get(KEY_CSMSG_TOUSER);
        String msgType = requestMap.get(KEY_CSMSG_TYPE);
        String appId = requestMap.get(KEY_APP_ID);
        String seretCode = requestMap.get(KEY_SECRET_CODE);
        if(StringUtils.isNotEmpty(touser) && StringUtils.isNotEmpty(msgType) &&
                StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(seretCode)) {
            switch (msgType) {
                case "text":
                    String msg = getCSJsonTxtMsg(requestMap);//得到客服消息的json字符串；
                    if(StringUtils.isNotEmpty(msg)) {
                        sendCustomMsgByJsonStr(msg, appId, seretCode);
                    }
                    break;
                case "image":
                    String imgMsg = getCSJsonImgMsg(requestMap,appId,seretCode);
                    if(StringUtils.isNotEmpty(imgMsg)) {
                        sendCustomMsgByJsonStr(imgMsg, appId, seretCode);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 将客服文本消息体从map转化成Json
     * @author:     Logan
     * @date:       2018/11/16 03:48
     * @params:     [requestMap]
     * @return:     消息体的JSON串
    **/
    private static String getCSJsonTxtMsg(Map<String,String> requestMap){
          String content = requestMap.get(KEY_CSMSG_CONTENT);
          String touser = requestMap.get(KEY_CSMSG_TOUSER);
          if(StringUtils.isNotEmpty(content)){
              WechatCSTxtMsgEntity msgEntity = new WechatCSTxtMsgEntity(touser,content);
              Gson gson = new Gson();
              return gson.toJson(msgEntity);
          }else{
              return null;
          }
    }

    private static String getCSJsonImgMsg(Map<String,String> requestMap,String appId,String secretCode){
        String filePath = requestMap.get(KEY_FILE_PATH);
        String fileType =WeChatConstant.REQ_MESSAGE_TYPE_IMAGE;
        String touser = requestMap.get(KEY_CSMSG_TOUSER);
        if(StringUtils.isNotEmpty(filePath) && StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(secretCode)) {
            try {
                Map<String, Object> uploadInfoMap = UploadMeida(
                        fileType, filePath, appId, secretCode);
                //先新增临时素材，返回media_id
                String mediaId = uploadInfoMap.get(KEY_MEDIA_ID).toString();
                if(StringUtils.isNotEmpty(mediaId)) {
                    WechatCSImgMsgEntity imgMsgEntity = new WechatCSImgMsgEntity(touser,mediaId);
                    Gson gson = new Gson();
                    return gson.toJson(imgMsgEntity);
                }
            } catch (IOException e) {
                log.error("---------------发送临时图片消息失败------------");
                log.error(e.getMessage());
                return "";
            }
        }
        return null;
    }
    /**
     * 回复图片消息
     * @param requestMap
     *      requestMap 参数说明
     *          ReplyToUserName 接收方账号(收到的OpenID)
     *          ReplyFromUserName 发送方账号(微信账号)
     *          MediaId
     * @return 返回 String 类型的 xml
     */
    public static String sendImageMsg(Map<String,String> requestMap){
        Map<String,Object> map = new HashMap<>();
        map.put(WeChatConstant.ToUserName,requestMap.get(WeChatConstant.Reply_FromUserName));
        map.put(WeChatConstant.FromUserName,requestMap.get(WeChatConstant.Reply_ToUserName));
        map.put("MsgType", WeChatConstant.REQ_MESSAGE_TYPE_IMAGE);
        Map<String,Object> mediaId = new HashMap<>();
        mediaId.put("MediaId",requestMap.get(WeChatConstant.MediaId));
        map.put("Image",mediaId);
        map.put("CreateTime", new Date().getTime());
        return mapToXML(map);
    }

    /**
     * 回复图片消息
     * @param requestMap
     *      requestMap 参数说明
     *          ReplyToUserName 接收方账号(收到的OpenID)
     *          ReplyFromUserName 发送方账号(微信账号)
     *          filePath 图片down在本地的路径
     *          appId 微信公众号的appId
     *          secretCode 微信的secretCode
     * @return 返回 String 类型的 xml
     */
    public static String sendTemporaryImageMsg(Map<String,String> requestMap){
        String fileType =WeChatConstant.REQ_MESSAGE_TYPE_IMAGE;
        String filePath =requestMap.get(KEY_FILE_PATH);
        String appId = requestMap.get(KEY_APP_ID);
        String secretCode = requestMap.get(KEY_SECRET_CODE);
        if(StringUtils.isNotEmpty(filePath) && StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(secretCode)) {
            try {
                Map<String, Object> uploadInfoMap = UploadMeida(
                        fileType, filePath, appId, secretCode);
                String mediaId = uploadInfoMap.get(KEY_MEDIA_ID).toString();
                if(StringUtils.isNotEmpty(mediaId)) {
                    requestMap.put(WeChatConstant.MediaId, mediaId);
                    requestMap.put(WeChatConstant.Reply_FromUserName,requestMap.get(WeChatConstant.Reply_ToUserName));
                    requestMap.put(WeChatConstant.Reply_ToUserName,requestMap.get(WeChatConstant.Reply_FromUserName));
                    return sendImageMsg(requestMap);
                }
            } catch (IOException e) {
                log.error("---------------发送临时图片消息失败------------");
                log.error(e.getMessage());
                return "";
            }
        }
        return "";
    }

    /**
     * 回复图文消息
     * @param requestMap
     * @param items
     * @return
     */
    public static String sendArticleMsg(Map<String,String> requestMap, List<ArticleItem> items){
        if(items == null || items.size()<1){
            return "";
        }
        Map<String,Object> map=new HashMap<String, Object>();
        map.put(WeChatConstant.ToUserName, requestMap.get(WeChatConstant.Reply_ToUserName));
        map.put(WeChatConstant.FromUserName, requestMap.get(WeChatConstant.Reply_FromUserName));
        map.put(WeChatConstant.MsgType, "news");
        map.put("CreateTime", new Date().getTime());
        List<Map<String,Object>> Articles=new ArrayList<Map<String,Object>>();
        for(ArticleItem itembean : items){
            Map<String,Object> item=new HashMap<String, Object>();
            Map<String,Object> itemContent=new HashMap<String, Object>();
            itemContent.put("Title", itembean.getTitle());
            itemContent.put("Description", itembean.getDescription());
            itemContent.put("PicUrl", itembean.getPicUrl());
            itemContent.put("Url", itembean.getUrl());
            item.put("item",itemContent);
            Articles.add(item);
        }
        map.put("Articles", Articles);
        map.put("ArticleCount", Articles.size());
        return mapToXML(map);
    }

    /**
     * 通过微信用户OpenID 得到用户信息
     * @param fromUserName 相当于用户Openid
     * @appId 开发者ID
     * @secret 开发者密码
     * @return 返回用户信息map格式
     */
    public static Map<String,String> getUserInfo(String fromUserName,String appId,String secret){
        JSONObject jsonObject = WXUtil.getAccessToken(appId,secret);
        String access_token = jsonObject.getString("access_token");
        String url = user_url+"?access_token="+access_token+"&openid="+fromUserName+"&lang=zh_CN";
        String resultStr = HttpUtil.doGetSSL(url);

        JSONObject result = parseObject(resultStr);
        Map<String, String> map = JSONObject.parseObject(resultStr, new TypeReference<Map<String, String>>(){});
        return map;
    }

    /**
     * 获取微信公众号二维码 并保存
     * @param codeType 二维码类型 "1": 临时二维码  "2": 永久二维码
     * @param expireSeconds 临时二维码的过期时间，最多是2592000（即30天）
     * @param actionName 二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
     * @param sceneStr 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64,就是带参数的
//     * @param fileName 图片名称
     * @param appId 开发者ID
     * @param secret 开发者密码
     */
    public static String getWXPublicQRCode(String codeType,int expireSeconds, String actionName,String sceneStr,String appId,String secret) {
        JSONObject jsonObject = WXUtil.getAccessToken(appId,secret);
        String access_token = jsonObject.getString("access_token");
        String url = qrcode_url+"?access_token="+access_token;

        Map<String, Object> map = new HashMap<>();
        if ("1".equals(codeType)) { // 临时二维码
            map.put("expire_seconds", expireSeconds);
            map.put("action_name", actionName);
            Map<String, Object> sceneMap = new HashMap<>();
            Map<String, Object> sceneIdMap = new HashMap<>();
            sceneIdMap.put("scene_str", sceneStr);
            sceneMap.put("scene", sceneIdMap);
            map.put("action_info", sceneMap);
        } else if ("2".equals(codeType)) { // 永久二维码
            map.put("action_name", actionName);
            Map<String, Object> sceneMap = new HashMap<>();
            Map<String, Object> sceneIdMap = new HashMap<>();
            sceneIdMap.put("scene_str", sceneStr);
            sceneMap.put("scene", sceneIdMap);
            map.put("action_info", sceneMap);
        }
        String data = JSON.toJSONString(map);
        // 得到ticket票据,用于换取二维码图片
        String resultStr = HttpUtil.doPost(url, data);
        JSONObject jsonticket = JSON.parseObject(resultStr);
        String ticket = jsonticket.getString("ticket");
//                (String) jsonticket.get("ticket");
//        System.out.println("ticket " +ticket);
//         WXConstants.QRCODE_SAVE_URL: 填写存放图片的路径
        String showqrUrl = showqr_url + URLEncoder.encode(ticket);
        return showqrUrl;
//        HttpUtil.httpsRequestPicture(showqr_url + URLEncoder.encode(ticket),
//                "GET", null, "F:\\testProject",fileName, "jpg");
    }

    /**
     * 新增临时素材
     * @param fileType 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @param filePath 文件路径
     * @param appId 开发者ID
     * @param secret 开发者密码
     * @return {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
     *      type 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb，主要用于视频与音乐格式的缩略图）
     *      media_id 媒体文件上传后，获取标识
     *      created_at 媒体文件上传时间戳
     * @throws IOException
     */
    public static Map<String,Object> UploadMeida(String fileType,String filePath,String appId,String secret) throws IOException {
        //返回结果
        String result = null;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
//        String token = WechatUtil.getToken();
        JSONObject jsonObject = WXUtil.getAccessToken(appId,secret);
        String token = jsonObject.getString("access_token");
        String urlString = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + token + "&type=" + fileType;
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");//以POST方式提交表单
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);//POST方式不能使用缓存
        //设置请求头信息
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        //请求正文信息
        //第一部分
        StringBuilder sb = new StringBuilder();
        sb.append("--");//必须多两条道
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"media\"; filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");
//        System.out.println("sb:" + sb);

        //获得输出流
        OutputStream out = new DataOutputStream(conn.getOutputStream());
        //输出表头
        out.write(sb.toString().getBytes("UTF-8"));
        //文件正文部分
        //把文件以流的方式 推送道URL中
        DataInputStream din = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] buffer = new byte[1024];
        while ((bytes = din.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
        }
        din.close();
        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");//定义数据最后分割线
        out.write(foot);
        out.flush();
        out.close();
        if (HttpsURLConnection.HTTP_OK == conn.getResponseCode()) {

            StringBuffer strbuffer = null;
            BufferedReader reader = null;
            try {
                strbuffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String lineString = null;
                while ((lineString = reader.readLine()) != null) {
                    strbuffer.append(lineString);

                }
                if (result == null) {
                    result = strbuffer.toString();
//                    System.out.println("result:" + result);
                }
            } catch (IOException e) {
//                System.out.println("发送POST请求出现异常！" + e);
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        //返回的字符串转成json
        JSONObject resultJson = JSON.parseObject(result);
        Map<String,Object> map = new HashMap<>();
        map = JSONObject.toJavaObject(resultJson,Map.class);
        return map;
    }

    public static final String access_token = "access_token_";

//    /**
//     * 根据wechatAccount获取 appid、secret
//     * @param wechatAccount 原始公众号ID
//     * @return
//     */
//    public static Map<String, Object> getAppIdAndSecret(String wechatAccount,String appid,String secret){
//
//        Map<String, Object> redisMap = RedisFactory.get(wechatAccount,Map.class);
//        Map<String,Object> wechatAndSecretMap = new HashMap<>();
//        if(redisMap != null && redisMap.size() > 1){
//            wechatAndSecretMap.put(WeChatContants.APPID,redisMap.get(WeChatContants.APPID));
//            wechatAndSecretMap.put(WeChatContants.SECRET,redisMap.get(WeChatContants.SECRET));
//            return wechatAndSecretMap;
//        }else{
//            System.out.println(" appid "+appid);
//            System.out.println(" secret "+secret);
//            wechatAndSecretMap.put(WeChatContants.APPID,appid);
//            wechatAndSecretMap.put(WeChatContants.SECRET,secret);
//            RedisFactory.set(wechatAccount,Map.class,3600 * 2000);
//        }
//        return wechatAndSecretMap;
//    }
}
