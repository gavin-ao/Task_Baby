package data.driven.cm.util;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import com.google.gson.GsonBuilder;
import data.driven.cm.common.RedisFactory;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.ArticleItem;
import data.driven.cm.entity.wechat.WechatCSImgMsgEntity;
import data.driven.cm.entity.wechat.WechatCSTxtMsgEntity;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
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
    private static final String USER_URL = "https://api.weixin.qq.com/cgi-bin/user/info";
    /**生成代参二维码**/
    private static final String QRCODE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
    /**获取二维码**/
    private static final String SHOWQR_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
    /** 客服接口-发消息 **/
    private static final String CUSTOM_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    /** 模版接口-发消息 **/
    private static final String TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";


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
        String[] arr = new String[] { WeChatConstant.THIRD_PARTY_TOKEN, timestamp, nonce };
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
        char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = digit[mByte & 0X0F];

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
     * @param respXml 解密后的xml文件
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked"})
    public static Map<String,String> parseXml(String respXml) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap<>();

        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(new ByteArrayInputStream(respXml.getBytes()));
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();
            // 遍历所有子节点
            for (Element e : elementList){
                map.put(e.getName(), e.getText());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析微信发来的请求(request)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked"})
    public static Map<String,String> parseRequest(HttpServletRequest request) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap<>();

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
        for (Element e : elementList){
            map.put(e.getName(), e.getText());
        }

        // 释放资源
        inputStream.close();
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
            if (null == value){
                value = "";
            }
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
        map.put("ToUserName", requestMap.get(WeChatConstant.REPLY_TO_USER_NAME));
        map.put("FromUserName",  requestMap.get(WeChatConstant.REPLY_FROM_USER_NAME));
        map.put("MsgType", WeChatConstant.RESP_MESSAGE_TYPE_TEXT);
        map.put("CreateTime", System.currentTimeMillis());
        map.put("Content", requestMap.get(WeChatConstant.CONTENT));
        return  mapToXML(map);
    }

    /**
     *
     * @author:     Logan
     * @date:       2018/11/16 03:48
     * @params:     [JsonStr, appId, secret]
     * @return:     void
    **/
    public static void sendCustomMsgByJsonStr(String jsonStr, String accessToken){
        String url = CUSTOM_URL+accessToken;
        log.debug("——————————调用微信接口发送客服信息——————————");
        long begin =System.currentTimeMillis();
        String result = HttpUtil.doPost(url, jsonStr);
        log.info(String.format("----------发送客服消息返回值:%s-----------",result));
        WeChatUtil.log(log,begin,"调用微信接口发送客服信息");
    }

    public static void sendTemplateMsg(String jsonStr,String accessToken){
        String url = TEMPLATE_URL + accessToken;
        log.info("----------------调用模版接口 start ---------------------");
        HttpUtil.doPost(url,jsonStr);
        log.info("----------------调用模版接口 end -----------------------");
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
    public static void sendCustomMsg(Map<String,String> requestMap,String accessToken){
        String touser = requestMap.get(KEY_CSMSG_TOUSER);
        String msgType = requestMap.get(KEY_CSMSG_TYPE);
        if(StringUtils.isNotEmpty(touser) && StringUtils.isNotEmpty(msgType) &&
                StringUtils.isNotEmpty(accessToken)) {
            switch (msgType) {
                case "text":
                    String msg = getCSJsonTxtMsg(requestMap);//得到客服消息的json字符串；
                    log.info(String.format("--------------发送客服消息的参数JSON：%s:",msg));
                    if(StringUtils.isNotEmpty(msg)) {
                        sendCustomMsgByJsonStr(msg, accessToken);
                    }
                    break;
                case "image":
                    String imgMsg = getCSJsonImgMsg(requestMap,accessToken);
                    if(StringUtils.isNotEmpty(imgMsg)) {
                        sendCustomMsgByJsonStr(imgMsg, accessToken);
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
              Gson gson = new GsonBuilder().disableHtmlEscaping().create();
              return  gson.toJson(msgEntity).replace("\\\\r\\\\n","\\r\\n");
          }else{
              return null;
          }
    }

    private static String getCSJsonImgMsg(Map<String,String> requestMap,String accessToken){
        String filePath = requestMap.get(KEY_FILE_PATH);
        String fileType =WeChatConstant.REQ_MESSAGE_TYPE_IMAGE;
        String touser = requestMap.get(KEY_CSMSG_TOUSER);
        if(StringUtils.isNotEmpty(filePath) && StringUtils.isNotEmpty(accessToken)) {
            try {
                log.debug("-----------开始上传临时素材--------------");
                long begin=System.currentTimeMillis();
                //先新增临时素材，返回media_id
                Map<String, Object> uploadInfoMap = UploadMeida(
                        fileType, filePath, accessToken);
                WeChatUtil.log(log,begin,"上传临时素材");
                String mediaId = uploadInfoMap.get(KEY_MEDIA_ID).toString();
                if(StringUtils.isNotEmpty(mediaId)) {
                    WechatCSImgMsgEntity imgMsgEntity = new WechatCSImgMsgEntity(touser,mediaId);
                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    return gson.toJson(imgMsgEntity).replace("\\\\r\\\\n","\\r\\n");
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
        map.put(WeChatConstant.TO_USER_NAME,requestMap.get(WeChatConstant.REPLY_FROM_USER_NAME));
        map.put(WeChatConstant.FROM_USER_NAME,requestMap.get(WeChatConstant.REPLY_TO_USER_NAME));
        map.put("MsgType", WeChatConstant.REQ_MESSAGE_TYPE_IMAGE);
        Map<String,Object> mediaId = new HashMap<>();
        mediaId.put("MediaId",requestMap.get(WeChatConstant.MEDIA_ID));
        map.put("Image",mediaId);
        map.put("CreateTime", System.currentTimeMillis());
        return mapToXML(map);
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
        map.put(WeChatConstant.TO_USER_NAME, requestMap.get(WeChatConstant.REPLY_TO_USER_NAME));
        map.put(WeChatConstant.FROM_USER_NAME, requestMap.get(WeChatConstant.REPLY_FROM_USER_NAME));
        map.put(WeChatConstant.MSG_TYPE, "news");
        map.put("CreateTime", System.currentTimeMillis());
        List<Map<String,Object>> articles=new ArrayList<Map<String,Object>>();
        for(ArticleItem itembean : items){
            Map<String,Object> item=new HashMap<String, Object>();
            Map<String,Object> itemContent=new HashMap<String, Object>();
            itemContent.put("Title", itembean.getTitle());
            itemContent.put("Description", itembean.getDescription());
            itemContent.put("PicUrl", itembean.getPicUrl());
            itemContent.put("Url", itembean.getUrl());
            item.put("item",itemContent);
            articles.add(item);
        }
        map.put("Articles", articles);
        map.put("ArticleCount", articles.size());
        return mapToXML(map);
    }

    /**
     * 通过微信用户OpenID 得到用户信息
     * @param fromUserName 相当于用户Openid
     * @appId 开发者ID
     * @secret 开发者密码
     * @return 返回用户信息map格式
     */
    public static Map<String,String> getUserInfo(String fromUserName,String accessToken){
        String url = USER_URL+"?access_token="+accessToken+"&openid="+fromUserName+"&lang=zh_CN";
        String resultStr = HttpUtil.doGetSSL(url);

//        JSONObject result = parseObject(resultStr);
        Map<String, String> map = JSONObject.parseObject(resultStr, new TypeReference<Map<String, String>>(){});
        return map;
    }

    /**
     * 获取微信公众号二维码 并保存
     * @author:     Logan
     * @date:       2018/11/19 12:15
     * @params:     [codeType, expireSeconds, actionName, sceneStr, access_token]
     * @return:     java.lang.String
    **/
    public static String getWXPublicQRCode(String codeType,int expireSeconds, String actionName,String sceneStr,String accessToken) {
        String url = QRCODE_URL+"?access_token="+accessToken;
        log.info(String.format("-----------带参数二维码的scenestr:%s-------------",sceneStr));
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
        log.info(String.format("-------------调用二维码的url:%s, 参数data:%s-------",url,data));
        log.info(String.format("--------------调用接口换取二维码图片的返回值:%s---------",resultStr));
        JSONObject jsonticket = JSON.parseObject(resultStr);
        String ticket = jsonticket.getString("ticket");
        String showqrUrl = SHOWQR_URL + URLEncoder.encode(ticket);
        return showqrUrl;
    }

    public static Map<String,Object> UploadMeida(String fileType,String filePath,String accessToken) throws IOException {
        long begin = System.currentTimeMillis();
        //返回结果
        String result = null;
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }
//        String token = WechatUtil.getToken();
        String urlString = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + accessToken + "&type=" + fileType;
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        log.debug("----------------openConnection End---------------");
        conn.setRequestMethod("POST");//以POST方式提交表单
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);//POST方式不能使用缓存
        //设置请求头信息
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Charset", "UTF-8");
        //设置边界
        String bounDay = "----------" + System.currentTimeMillis();
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + bounDay);
        //请求正文信息
        //第一部分
        StringBuilder sb = new StringBuilder();
        sb.append("--");//必须多两条道
        sb.append(bounDay);
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
        log.debug("---------开始写入文件流--------------");
        DataInputStream din = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] buffer = new byte[1024];
        while ((bytes = din.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
        }
        din.close();
        float duration = (System.currentTimeMillis()-begin)/1000f;
        log.debug("------------上传素材的写入文件结束，耗时：",duration,"————————————");
        //结尾部分
        byte[] foot = ("\r\n--" + bounDay + "--\r\n").getBytes("UTF-8");//定义数据最后分割线
        out.write(foot);
        out.flush();
        out.close();
        log.debug("----------------开始把文件流写入conn-----------------");
        long connBegin = System.currentTimeMillis();
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
                 duration = (System.currentTimeMillis()-connBegin)/1000f;
                log.debug("-----------------写入conn结束,耗时：",duration,"-----------");
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
        duration = (System.currentTimeMillis()-begin)/1000f;
        return map;
    }
    public static void log(Logger log, long begin, String info){
        float duration = (System.currentTimeMillis()-begin)/1000f;
        log.info(String.format(
                "---------------%s完成，总耗时:%f秒----------------",info,duration));
    }

    /**
     * 使用授权码换取公众号或小程序的接口调用凭据和授权信息
     * @author:     Logan
     * @date:       2018/11/23 10:16
     * @params:     [authCode]
     * @return:     授权信息JSONStr
    **/
    public static String getAuthoInfo(String authCode){
       String thirdPartyAccessToken =WeChatUtil.getComponentAccessToken();
       String postBodyStr = String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}",
                WeChatConstant.API_JSON_KEY_COMPONET_APPID,WeChatConstant.THIRD_PARTY_APPID,
                WeChatConstant.API_JSON_KEY_AUTH_CODE,authCode);
        log.info(String.format("-----------获取授权信息postBody %s ",postBodyStr));
       log.info("----------调用获取公众号授权信息接口,参数：------------------------");
       log.info(postBodyStr);
        String url = WeChatConstant.getAPIAddressAuthInfoURL(thirdPartyAccessToken);
       log.info(String.format("-------------接口地址:%s",url));
       JSONObject postJSON = JSONObject.parseObject(postBodyStr);
       return HttpUtil.doPost(url, postBodyStr);

    }
    /**
     * 访问刷新authorizerToken的接口
     * @author:     Logan
     * @date:       2018/11/23 14:42
     * @params:     [authAppId, reFreshToken]
     * @return:     JSON格式字符串 刷新接口的返回值
    **/
    public static String accessFreshTokenAPI(String authAppId, String reFreshToken){
        log.info("-----------调用刷新token的接口---------------");
        //组织post的消息体
        String postStr =
                String.format("{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":\"%s\"}",
                        WeChatConstant.API_JSON_KEY_COMPONET_APPID,WeChatConstant.THIRD_PARTY_APPID,
                        WeChatConstant.API_JSON_KEY_AUTH_APPID,authAppId,
                        WeChatConstant.API_JSON_KEY_AUTH_REFRESH_TOKEN,reFreshToken);
        log.info("------------postStr : "+postStr);
        //获取刷新Token的URL
        String thirdPartyAccessToken =WeChatUtil.getComponentAccessToken();
        String refreshTokenUrl =
                WeChatConstant.getRefreshTokenURL(thirdPartyAccessToken);//获取刷新token的url地址
        log.info(String.format("-------------调用刷新token，url:%s-----------",refreshTokenUrl));
        String newTokenResult = HttpUtil.doPost(refreshTokenUrl,postStr);
        return newTokenResult;
    }
    /**
     * 获取授权扫码url
     * @author:     Logan
     * @date:       2018/11/23 16:08
     * @params:     [preAuthCode]
     * @return:     授权扫码url
    **/
    public static String getAuthorizeWebsite(String preAuthCode){
        String apiURL="https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=%s&pre_auth_code=%s&redirect_uri=%s";
        String callBackURL = "http://easy7share.com/authcallback";
        String url = String.format(apiURL,THIRD_PARTY_APPID,preAuthCode,callBackURL);
        log.info(String.format("--------------授权url%s",url));
        return url;
    }

    /**
     * 存入第三方平台接口调用凭据
     * @param componentVerifyTicket 第三方平台接口调用凭据
     * @return
     */
    public static void setComponentVerifyTicket(String componentVerifyTicket){
        RedisFactory.setString(WeChatConstant.THIRD_PARTY_TICKET, componentVerifyTicket, 600 * 1000);
    }

    /**
     * 获取第三方平台接口调用凭据
     * @return ticket
     */
    public static String getComponentVerifyTicket(){
        return RedisFactory.get(WeChatConstant.THIRD_PARTY_TICKET);
    }

    /**
     * 返回 第三方 component_access_token
     * @author lxl
     * @return
     */
    public static String getComponentAccessToken(){
        String key =WeChatConstant.THIRD_PARTY_ACCESS_TOKEN;
        String componentAccessToken = RedisFactory.get(key);
        log.info("-----------component_access_token start ------------- "+componentAccessToken + "  end");
        if (componentAccessToken != null && componentAccessToken.trim().length() > 0){
            return componentAccessToken;
        }
        Map<String, Object> paramMap = new HashMap<>();
        String componentVerifyTicket = getComponentVerifyTicket();
        log.info("-----------componentVerifyTicket start ------------- "+componentVerifyTicket + " end");
        paramMap.put("component_appid",WeChatConstant.THIRD_PARTY_APPID);
        paramMap.put("component_appsecret",WeChatConstant.THIRD_PARTY_SECRET);
        paramMap.put("component_verify_ticket",componentVerifyTicket);
        String data = JSON.toJSONString(paramMap);
        String resultStr = HttpUtil.doPost(WeChatConstant.COMPONENT_TOKEN_URL, data);
        if(resultStr == null){
            log.info("------------------获取component_access_token失败！-----------------");
            return "";
        }
        JSONObject resultJson = parseObject(resultStr);
        log.info("------------------resultJson -----------------" + resultJson.toString());
        if (resultJson.getString("component_access_token") != null &&
                resultJson.getString("component_access_token").trim().length() > 0){
            log.info("------------------获取component_access_token: "+resultJson.getString("component_access_token")+"-----------------");
            RedisFactory.setString(key,resultJson.getString("component_access_token"),WeChatConstant.CATCE_VALUE_EXPIRE_COMPONENT_ACCESS_TOKEN * 1000);
        }
        return resultJson.getString("component_access_token");
    }
    /**
     * 获取预授权码pre_auth_code
     * @author lxl
     * @return
     */
    public static String getPreAuthCode(){
        String key =WeChatConstant.THIRD_PARTY_PRE_AUTH_CODE;
//        String preAuthCode = RedisFactory.get(key);
//        log.info("-----------preAuthCode start ------------- "+preAuthCode + "  end");
//        if (preAuthCode != null && preAuthCode.trim().length() > 0){
//            return preAuthCode;
//        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("component_appid",WeChatConstant.THIRD_PARTY_APPID);
        String thirdPartyAccessToken = getComponentAccessToken();
        String data = JSON.toJSONString(paramMap);
        String resultStr = HttpUtil.doPost(WeChatConstant.getPreAuthCode(thirdPartyAccessToken), data);
        if(resultStr == null){
            log.info("------------------获取pre_auth_code失败！-----------------");
            return "";
        }
        JSONObject resultJson = parseObject(resultStr);
//        if (resultJson.getString("pre_auth_code") != null &&
//                resultJson.getString("pre_auth_code").trim().length() > 0){
//            log.info("------------------pre_auth_code: "+resultJson.getString("pre_auth_code")+"-----------------");
//            RedisFactory.setString(key,resultJson.getString("pre_auth_code"),WeChatConstant.CATCE_VALUE_EXPIRE_PRE_AUTH_CODE * 1000);
//        }
        return resultJson.getString("pre_auth_code");
    }

   /**
    * 根据授权微信公众号的appId,获取此公众号的账号详情
    * @author:     Logan
    * @date:       2018/11/23 16:36
    * @params:     [authAppId]
    * @return:     java.lang.String
   **/
    public static String accessAuthAccountDetailAPI(String authAppId){
        String urlTemplate = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=%s";
        String thirdPartyAccessToken = WeChatUtil.getComponentAccessToken();
        String url =String.format(urlTemplate,thirdPartyAccessToken);
        String postStrTempate ="{\"%s\":\"%s\",\"%s\":\"%s\"}";
        String postStr = String.format(postStrTempate,
                WeChatConstant.API_JSON_KEY_COMPONET_APPID,WeChatConstant.THIRD_PARTY_APPID,
                WeChatConstant.API_JSON_KEY_AUTH_APPID,authAppId);
        JSONObject postObj = JSONObject.parseObject(postStr);
        return HttpUtil.doPost(url,postStr);
    }

    /**
    * @description  发送未完成情况下的模板消息
    * @author Logan
    * @date 2018-11-30 12:25
    * @param touser  接收者OpenId
    * @param ServiceStatus 还差几个人助力 ""
    * @param validPeriod
    * @param accessToken

    * @return
    */
    public static String sendUncompleteProgressTemplateMsg(String touser, String ServiceStatus, String validPeriod,String accessToken ){
        JSONObject msgParam = new JSONObject();
        msgParam.put(API_JSON_KEY_TOUSER,touser);
        msgParam.put(API_JSON_KEY_TEMPLATE_ID,PROGRESS_TEMPLATE_MSG_TEMPLATE_ID);
//        msgParam.put(API_JSON_KEY_URL,"")  todo:模板消息跳转功能
        JSONObject first = new JSONObject();
        first.put("value","亲爱的用户，您还有未完成的活动");
        first.put("color","#173177");
        JSONObject keyworkd1 = new JSONObject();
        keyworkd1.put("value","未完成奖励领");
        keyworkd1.put("color","#173177");
        JSONObject keyworkd2 = new JSONObject();
        keyworkd2.put("value",ServiceStatus);
        keyworkd2.put("color","#173177");
        JSONObject keyworkd3 = new JSONObject();
        keyworkd3.put("value",validPeriod);
        keyworkd3.put("color","#173177");
        JSONObject remark = new JSONObject();
        remark.put("value","请在继续努力完成奖励哦~");
        remark.put("color","#173177");
        JSONObject paramData = new JSONObject();
        paramData.put("keyworkd1",keyworkd1);
        paramData.put("keyworkd2",keyworkd2);
        paramData.put("keyworkd3",keyworkd3);
        paramData.put("remark",remark);
        msgParam.put("data",paramData);
        String urlTemplate ="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
        String url = String.format(urlTemplate,accessToken);
        return HttpUtil.doPost(url,msgParam.toJSONString());
    }

    private static String getAuthWebPageRedirectUrl(String rootUrl){
       StringBuffer redirectUrlBff = new StringBuffer(rootUrl).append("/subscribe/authcallback");
        try {
            log.info(String.format("-------原始RedirectUrl:%s----------",redirectUrlBff.toString()));
            return URLEncoder.encode(redirectUrlBff.toString(),"UTF-8") ;
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
    public static String getWebPageAuthUrl(String rootUrl, String serviceWechatAppId,String subscribeWechatAccount,String fromUnionId,String actId){
        String urlTemplate = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=%s&" +
                "component_appid=%s#wechat_redirect";
//        String urlTemplate = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
//                "appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&" +
//                "state=%s#wechat_redirect";
        String state = String.format("%s@@%s@@%s@@%s",serviceWechatAppId,actId,fromUnionId,subscribeWechatAccount);
        String authUrl = String.format(urlTemplate,serviceWechatAppId,getAuthWebPageRedirectUrl(rootUrl),state,WeChatConstant.THIRD_PARTY_APPID);

        log.info( String.format("--------------------WebPageAuthUrl:%s----------",authUrl));
        return authUrl;
    }
}
