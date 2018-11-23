package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.aes.WXBizMsgCrypt;
import data.driven.cm.business.taskBaby.ThirdPartyService;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.util.WeChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Map;

/**
 * @Author: lxl
 * @describe 第三方平台Impl
 * @Date: 2018/11/23 9:25
 * @Version 1.0
 */
public class ThirdPartyServiceImpl implements ThirdPartyService{
    Logger logger = LoggerFactory.getLogger(ThirdPartyServiceImpl.class);

    /**
     * 解密第三方发送的xml
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    public void hadleAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String timestamp = request.getParameter("timestamp");
        String encrypt_type = request.getParameter("encrypt_type");
        String nonce = request.getParameter("nonce");
        String msg_signature=request.getParameter("msg_signature");
        logger.info("------------------------timestamp "+ timestamp +"------------------------" );
        logger.info("------------------------encrypt_type "+ encrypt_type +"------------------------" );
        logger.info("------------------------nonce "+ nonce +"------------------------" );
        logger.info("------------------------msg_signature "+ msg_signature +"------------------------" );

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = request.getReader();
        String line;
        while((line = in.readLine()) != null){
            stringBuilder.append(line);
        }
        String xml = stringBuilder.toString();
        logger.info("第三方平台收到的原生: "+ xml);
        String encodingAesKey = WeChatConstant.THIRD_PARTY_ENCODINGAESKEY;
        String appId = WeChatConstant.THIRD_PARTY_APPID;
        //解密
        WXBizMsgCrypt pc = new WXBizMsgCrypt(WeChatConstant.THIRD_PARTY_TOKEN,encodingAesKey,appId);
        String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
//        String fromXML = String.format(format, requestMap.get("Encrypt"));
        xml = pc.decryptMsg(msg_signature, timestamp, nonce,xml);
        logger.info("第三方平台解析后的xml: "+xml);
        Map<String,String> requestMap= WeChatUtil.parseXml(xml);
        //得到tickon
        String component_verify_ticket = requestMap.get("ComponentVerifyTicket");
        logger.info("-----------------ticket : "+component_verify_ticket + "----------------------");
    }
}
