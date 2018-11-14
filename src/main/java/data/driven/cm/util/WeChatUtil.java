package data.driven.cm.util;

import java.io.InputStream;
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

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import data.driven.cm.component.WeChatContant;
import data.driven.cm.entity.taskBaby.ArticleItem;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * @Author: lxl
 * @describe
 * @Date: 2018/11/12 19:16
 * @Version 1.0
 */
public class WeChatUtil {

    /**微信用户**/
    private static final String user_url = "https://api.weixin.qq.com/cgi-bin/user/info";
    /**生成代参二维码**/
    private static final String qrcode_url = "https://api.weixin.qq.com/cgi-bin/qrcode/create";
    /**获取二维码**/
    private static final String showqr_code = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";


    /**
     * 验证签名
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[] { WeChatContant.TOKEN, timestamp, nonce };
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
     * @param content
     * @return
     */
    public static String sendTextMsg(Map<String,String> requestMap,String content){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_TEXT);
        map.put("CreateTime", new Date().getTime());
        map.put("Content", content);
        return  mapToXML(map);
//        return "<xml><Content><![CDATA[你谁阿]]></Content><CreateTime>1542034822929</CreateTime><ToUserName><![CDATA[oH1q_0bt1c9GXWzdx3l9fRKRE6rk]]></ToUserName><FromUserName><![CDATA[gh_2d2266631fa7]]></FromUserName><MsgType><![CDATA[text]]></MsgType></xml>";
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
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName", requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", "news");
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
     * @param fromUserName 相当于Openid
     * @return 返回用户信息map格式
     */
    public static Map<String, String> getUserInfo(String fromUserName,String APPID,String SECRET){
        Map<String,String> map = new HashMap<>();
        JSONObject jsonObject = WXUtil.getAccessToken(APPID,SECRET);
        String access_token = jsonObject.getString("access_token");
        String url = user_url+"?access_token="+access_token+"&openid="+fromUserName+"&lang=zh_CN";
        String resultStr = HttpUtil.doGetSSL(url);
        System.out.println("url " + url);
        if(resultStr == null){
            map.put("success","false");
            return map;
        }
        JSONObject result = parseObject(resultStr);
        map = JSONObject.toJavaObject(result,Map.class);
        map.put("success","true");
        return map;
    }

    /**
     * 获取微信公众号二维码 并保存
     * @param codeType 二维码类型 "1": 临时二维码  "2": 永久二维码
     * @param expireSeconds 临时二维码的过期时间，最多是2592000（即30天）
     * @param actionName 二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
     * @param sceneStr 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64,就是带参数的
     * @param fileName 图片名称
     */
    public static void getWXPublicQRCode(String codeType,int expireSeconds, String actionName,String sceneStr, String fileName,String appid,String secret) {
        JSONObject jsonObject = WXUtil.getAccessToken(appid,secret);
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
        // 得到ticket票据,用于换取二维码图片
        String resultStr = HttpUtil.doPost(url, data);
        JSONObject jsonticket = JSON.parseObject(resultStr);
        String ticket = jsonticket.getString("ticket");
//                (String) jsonticket.get("ticket");
        System.out.println("ticket " +ticket);
//         WXConstants.QRCODE_SAVE_URL: 填写存放图片的路径
        // WXConstants.QRCODE_SAVE_URL: 填写存放图片的路径
        HttpUtil.httpsRequestPicture(showqr_code + URLEncoder.encode(ticket),
                "GET", null, "F:\\testProject",fileName, "jpg");
    }
}
