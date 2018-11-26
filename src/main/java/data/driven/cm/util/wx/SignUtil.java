package data.driven.cm.util.wx;

import data.driven.cm.aes.AesException;
import data.driven.cm.aes.WXBizMsgCrypt;
import data.driven.cm.component.WeChatConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: Task_Baby
 * @description: 微信消息加解密工具类
 * @author: Logan
 * @create: 2018-11-25 17:11
 **/

public class SignUtil {
   /**
    * 
    * @author:     Logan
    * @date:       2018/11/25 20:25
    * @params:     [request, appId, encodingAesKey]   
    * @return:     java.lang.String
   **/        
    public static String decrypt(HttpServletRequest request,String appId, String encodingAesKey) throws Exception {
        WXBizMsgCrypt pc = null;
        try {
            pc = new WXBizMsgCrypt(WeChatConstant.THIRD_PARTY_TOKEN, encodingAesKey, appId);
        } catch (AesException e) {
            e.printStackTrace();
        }
        String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
        Map<String,String> requestMap = XMLUtil.parseXML(request);
        String fromXML = String.format(format, requestMap.get("Encrypt"));
        String msgsignature= request.getParameter("msg_signature");
        String timestamp = request.getParameter("timestamp");;
        String nonce = request.getParameter("nonce");
        String xml = pc.decryptMsg(msgsignature, timestamp, nonce, fromXML);
        return xml;
    }

}
