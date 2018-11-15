package data.driven.cm.util;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.common.Constant;
import data.driven.cm.common.RedisFactory;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;
import static data.driven.cm.util.JSONUtil.putMsg;

/**
 * 微信接口调用工具类
 * @author hejinkai
 * @date 2018/6/14
 */
public class WXUtil {
    /** 小程序id **/
    private static final String APPID = "wx99ab70b860fd745b";
    /** 小程序签名 **/
    private static final String SECRET = "e69553204ed744041f449d8fc29b4622";
    /** 许可类型-授权code **/
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    /** 许可类型-获取access_token **/
    private static final String GRANT_TYPE_CLIENT_CREDENTIAL = "client_credential";
    /** 根据登录code获取session_key的url **/
    private static final String jscode2session_url = "https://api.weixin.qq.com/sns/jscode2session";
    /** 用于获取小程序access_token的url **/
    private static final String token_url = "https://api.weixin.qq.com/cgi-bin/token";
    /** 用于获取二维码-接口A方式-无数量限制 **/
    private static final String typea_qrcode_url = "https://api.weixin.qq.com/wxa/getwxacode";
    /** 用于获取二维码-接口B方式-无数量限制 **/
    private static final String typeb_qrcode_url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";

    /**
     * 根据appid,secret,code获取sessionKey
     * @param appid
     * @param secret
     * @param code
     * @return
     */
    public static JSONObject getSessionKey(String appid, String secret, String code){
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("appid", appid);
        paramMap.put("secret", secret);
        paramMap.put("grant_type", GRANT_TYPE_AUTHORIZATION_CODE);
        paramMap.put("js_code", code);
        String resultStr = HttpUtil.doPostSSL(jscode2session_url, paramMap);
        if(resultStr == null){
            return new JSONObject();
        }
        return parseObject(resultStr);
    }

    public static JSONObject createWXQrcodeA(String path, String accessToken){
        return createWXQrcodeA(path, accessToken, true);
    }

    /**
     * 根据页面生成二维码
     * @param path 路径，参数用?xxx=xxx传递
     * @param accessToken
     * @param isHyaline 是否透明 ， true - 透明，
     * @return
     */
    public static JSONObject createWXQrcodeA(String path, String accessToken, boolean isHyaline){
        JSONObject paramJson = new JSONObject();
        paramJson.put("path", path);
        //默认配置
//        paramJson.put("width", 430);//二维码的宽度
//        paramJson.put("auto_color", false);//自动配置线条颜色，如果颜色依然是黑色，则说明不建议配置主色调
//        paramJson.put("line_color", JSONObject.parseObject("{\"r\":\"0\",\"g\":\"0\",\"b\":\"0\"}"));//auto_color 为 false 时生效，使用 rgb 设置颜色 例如 {"r":"xxx","g":"xxx","b":"xxx"} 十进制表示
        paramJson.put("is_hyaline", isHyaline);//	是否需要透明底色， is_hyaline 为true时，生成透明底色的小程序码
        String tempFileName = Constant.WXQRCODE_TEMP_FILE_FOLDER + UUIDUtil.getUUID() + ".jpg";
        String fileName = Constant.FILE_UPLOAD_PATH + tempFileName;
        JSONObject success = HttpUtil.doPostSSLAttachment(typea_qrcode_url + "?access_token="+accessToken, paramJson, fileName);
        if(success.getBoolean("success")){
            JSONObject result = JSONUtil.putMsg(true, "200", "二维码生成成功");
            result.put("qrcodeUrl", tempFileName);
            return result;
        }else{
            return success;
        }
    }

    /**
     * 根据页面生成二维码-可带参数
     * @param scene 自定义参数，最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用 urlencode 处理，请使用其他编码方式
     * @param page 必须是已经发布的小程序存在的页面（否则报错），例如 "pages/index/index" ,根路径前不要填加'/',不能携带参数（参数请放在scene字段里），如果不填写这个字段，默认跳主页面
     * @return
     */
    public static JSONObject createWXQrcodeB(String scene, String page, String accessToken){
        JSONObject paramJson = new JSONObject();
        paramJson.put("scene", scene);
        if(page != null){
            paramJson.put("page", page);
        }
        //默认配置
//        paramJson.put("width", 430);//二维码的宽度
//        paramJson.put("auto_color", false);//自动配置线条颜色，如果颜色依然是黑色，则说明不建议配置主色调
//        paramJson.put("line_color", JSONObject.parseObject("{\"r\":\"0\",\"g\":\"0\",\"b\":\"0\"}"));//auto_color 为 false 时生效，使用 rgb 设置颜色 例如 {"r":"xxx","g":"xxx","b":"xxx"} 十进制表示
//        paramJson.put("is_hyaline", false);//	是否需要透明底色， is_hyaline 为true时，生成透明底色的小程序码
        String tempFileName = Constant.WXQRCODE_TEMP_FILE_FOLDER + UUIDUtil.getUUID() + ".jpg";
        String fileName = Constant.FILE_UPLOAD_PATH + tempFileName;
        JSONObject success = HttpUtil.doPostSSLAttachment(typeb_qrcode_url + "?access_token="+accessToken, paramJson, fileName);
        if(success.getBoolean("success")){
            JSONObject result = JSONUtil.putMsg(true, "200", "二维码生成成功");
            result.put("qrcodeUrl", tempFileName);
            return result;
        }else{
            return success;
        }
    }

    public static final String access_token = "access_token_";

    /**
     * 根据appid,secret获取access_token
     * @param appid
     * @param secret
     * @return
     */
    public static JSONObject getAccessToken(String appid, String secret){
        String key = access_token + appid;
        String acessToken = RedisFactory.get(key);
        if(acessToken != null && acessToken.trim().length() > 1){
            JSONObject result = new JSONObject();
            result.put("access_token", acessToken);
            return result;
        }
        String url = token_url+"?grant_type="+GRANT_TYPE_CLIENT_CREDENTIAL+"&appid="+appid+"&secret="+secret;
        String resultStr = HttpUtil.doGetSSL(url);
        if(resultStr == null){
            return new JSONObject();
        }
        JSONObject result = parseObject(resultStr);
        if(result.getString("access_token") != null && result.getString("access_token").trim().length() > 1){
            RedisFactory.setString(key, result.getString("access_token"), 3600 * 1000);
        }
        return result;
    }

    /**
     * 根据code获取sessionKey
     * @param code
     * @return
     */
    public static JSONObject getSessionKey(String code){
        return getSessionKey(APPID, SECRET, code);
    }

    /**
     * 解密用户信息
     * @param encryptedData 用户信息密文
     * @param iv    初始向量 - 微信接口返回
     * @param code  微信登录接口返回的code，用于获取session_key
     */
    public static JSONObject decodeUserInfo(String encryptedData, String iv, String code){
        if(encryptedData == null || iv == null || code == null){
            return putMsg(false, "101", "参数为空");
        }
        String session_key = null;
        JSONObject sessionJsonObject = getSessionKey(code);
        if(sessionJsonObject.containsKey("session_key")){
            session_key = sessionJsonObject.getString("session_key");
        }else{
            return putMsg(false, "102", "获取不到session_key，请重新登录");
        }

        JSONObject result = executeDecodeUserInfo(encryptedData, iv, session_key);
        return result;
    }

    /**
     * 开始解密
     * @param encryptedData 用户信息密文
     * @param iv    初始向量 - 微信接口返回
     * @param session_key   利用code获取的session_key
     * @return
     */
    public static JSONObject executeDecodeUserInfo(String encryptedData, String iv, String session_key) {
        JSONObject result = null;
        try {
            byte[] resultByte  = AESUtil.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(session_key), Base64.decodeBase64(iv));
            if(null != resultByte && resultByte.length > 0){
                String userInfo = new String(resultByte, "UTF-8");
                result = putMsg(true, "200", "解密成功");
                result.put("userInfo", JSONObject.parseObject(userInfo));
            }else{
                result = putMsg(true, "109", "解密失败");
            }
        }catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将输入流转换为图片
     * @param input 输入流
     * @param savePath 图片需要保存的路径
     * @param fileType 图片类型
     */
    public static void inputStreamToMedia(InputStream input, String savePath, String fileName, String fileType) throws Exception {
        String filePath = savePath + "\\" + fileName + "." + fileType;
        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        int length;
        byte[] data = new byte[1024];
        while ((length = input.read(data)) != -1) {
            outputStream.write(data, 0, length);
        }
        outputStream.flush();
        outputStream.close();
    }


    public static void main(String[] args){
//        appid=wx6f8fab67827259b0&secret=ed71f12d39b999ee6c47d0b77a6f2c8c&scene=
        JSONObject jsonObject = getAccessToken("wx99ab70b860fd745b", "e69553204ed744041f449d8fc29b4622");
//        JSONObject jsonObject = getAccessToken("wx2c560cde533807da", "f3a0460c07461349d0b80cd618eade77");
        System.out.println(jsonObject.toJSONString());
//        JSONObject jsonObject2 = getAccessToken(APPID, SECRET);
//        JSONObject jsonObject2 = getAccessToken("wx2c560cde533807da", "f3a0460c07461349d0b80cd618eade77");
//        System.out.println(jsonObject2.toJSONString());
//        String accessToken = jsonObject.getString("access_token");
//        JSONObject r = createWXQrcodeA("pages/instrustor/main?actId=8&helpId=5b506d52721f1eecace73f99", accessToken);
//        System.out.println(r.toJSONString());

//        String encryptedData = "hqZuO3yN7xpjh660AgzL0iQwIUrqnDqgclRGaxm7RatzkuH25bpxaTcolvSYQXqbbjZjgSbqRvbmi/ypycmnVRi69LGKy+SwGVDQC6Eoic6cFYJdoC3no5cu4bAfjtwTTW0x1M3gDnc/KNOBAFN/iqgcZbk6wPDA7vJU8wf0WwvC2+0x9z5AZLjnGQrN7cp6oc0e1RCjAQo2RJNp93fO0v5GRrD7HC4+oL/ioaddqSv5t72skesFGjUPI2exP3G/3DUGg5bvz515nxrYV+ocRCmk38bR8PYwhKmn9YiDo1ChQ+kwwKyzlIU7K4cWm91UxWka3UWv9irLNmWNNi/UUFmjutjOP0OH0BQblp4g54USgsjFR1Gylcjz4reyDrQPQxb74NmFuSDaquuyLdT4k1qtFrcZahogCVTbIU97NPimT8k08OR50DRMQYwzSoCnviOSBIvhbiAWg6klclX4w7HMkWgM6pujTuTs6VYTgE4=";
//        String iv = "tP5c7E8+xkZoxGu6eT1HAQ==";
//        String sessionKey = "YowUQR+UWuSTMzZmWmnv5g==";
//        String appId = "wx4f4bc4dec97d474b";
//        String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
//        String encryptedData =
//                "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZM"+
//                        "QmRzooG2xrDcvSnxIMXFufNstNGTyaGS"+
//                        "9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+"+
//                        "3hVbJSRgv+4lGOETKUQz6OYStslQ142d"+
//                        "NCuabNPGBzlooOmB231qMM85d2/fV6Ch"+
//                        "evvXvQP8Hkue1poOFtnEtpyxVLW1zAo6"+
//                        "/1Xx1COxFvrc2d7UL/lmHInNlxuacJXw"+
//                        "u0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn"+
//                        "/Hz7saL8xz+W//FRAUid1OksQaQx4CMs"+
//                        "8LOddcQhULW4ucetDf96JcR3g0gfRK4P"+
//                        "C7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB"+
//                        "6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns"+
//                        "/8wR2SiRS7MNACwTyrGvt9ts8p12PKFd"+
//                        "lqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYV"+
//                        "oKlaRv85IfVunYzO0IKXsyl7JCUjCpoG"+
//                        "20f0a04COwfneQAGGwd5oa+T8yO5hzuy"+
//                        "Db/XcxxmK01EpqOyuxINew==";
//        String iv = "r7BXXKkLb8qrSNn05n0qiA==";
//        JSONObject j = executeDecodeUserInfo(encryptedData, iv, sessionKey);
//        System.out.println(getSessionKey("sss"));
//        System.out.println(getSessionKey("a","b","2"));
//        System.out.println(j);
    }

}
