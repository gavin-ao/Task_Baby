package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.aes.WXBizMsgCrypt;
import data.driven.cm.business.taskbaby.WeChatService;
import data.driven.cm.business.taskbaby.WechatResponseService;
import data.driven.cm.component.DuplicateRemovalMessage;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.thread.TaskBabyThread;
import data.driven.cm.util.WeChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class WeChatServiceImpl implements WeChatService {
    Logger logger = LoggerFactory.getLogger(WeChatServiceImpl.class);
    private static final int MESSAGE_CACHE_SIZE = 1000;
    private static List<DuplicateRemovalMessage> MESSAGE_CACHE = new ArrayList<DuplicateRemovalMessage>(MESSAGE_CACHE_SIZE);

    @Autowired
    private WechatResponseService wechatResponseService;

    /**
     * 调用核心服务类接收处理请求
     *
     * @author lxl
     * @param request
     * @param response
     * @param appid
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String processRequest(HttpServletRequest request, HttpServletResponse response, String appid) throws UnsupportedEncodingException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        logger.info("进入processRequest");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String encryptType = request.getParameter("encrypt_type");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        String msgSignature = request.getParameter("msg_signature");
        // 随机数
        String nonce = request.getParameter("nonce");

        // xml格式的消息数据
        String respXml = "success";
        Map<String, String> requestMap = null;
        try {
            //解析加密信息
            if ("aes".equals(encryptType)) {
                logger.info("解析加密信息");
                requestMap = WeChatUtil.parseRequest(request);
                WXBizMsgCrypt pc = new WXBizMsgCrypt(WeChatConstant.THIRD_PARTY_TOKEN, WeChatConstant.THIRD_PARTY_ENCODINGAESKEY, WeChatConstant.THIRD_PARTY_APPID);
                String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
                String fromXML = String.format(format, requestMap.get("Encrypt"));
                respXml = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
                requestMap = WeChatUtil.parseXml(respXml);
                TaskBabyThread taskBabyThread = new TaskBabyThread(wechatResponseService, requestMap, appid);
                Thread thread = new Thread(taskBabyThread);
                thread.start();

                //当不是关键词时返回信息

//                Map<String,String> respMap = new HashMap<>();
//                respMap.put("MsgType", WeChatConstant.RESP_MESSAGE_TYPE_TEXT);
//                respMap.put("Content", "稍后客服会联系您！");
//                respMap.put(WeChatConstant.Reply_ToUserName,requestMap.get("FromUserName") );
//                respMap.put(WeChatConstant.Reply_FromUserName, requestMap.get("ToUserName"));
//                String replyMsg = WeChatUtil.sendTextMsg(respMap);
//                logger.info("加密前的信息：" + replyMsg);
//                respXml = pc.encryptMsg(replyMsg, timestamp, nonce);
//                logger.info("加密的信息：" + respXml);
//                for (Map.Entry<String, String> entry : requestMap.entrySet()) {
//                    logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                }
//                return respXml;
                return "";
            } else { //处理明文
                logger.info("处理明文");
                requestMap = WeChatUtil.parseRequest(request);
                TaskBabyThread taskBabyThread = new TaskBabyThread(wechatResponseService, requestMap, appid);
                Thread thread = new Thread(taskBabyThread);
                thread.start();
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respXml;
    }

    /**
     * 排重，别调用后面的代码
     *
     * @param requestMap
     * @return
     */
    public Boolean deDuplication(Map<String, String> requestMap) {
        String fromUserName = requestMap.get("FromUserName");
        String createTime = requestMap.get("CreateTime");
        String msgId = requestMap.get("MsgId");
        DuplicateRemovalMessage duplicateRemovalMessage = new DuplicateRemovalMessage();

        // xml格式的消息数据
        String respXml = null;
        String mes = null;
        if (msgId != null) {
            duplicateRemovalMessage.setMsgId(msgId);
        } else {
            duplicateRemovalMessage.setCreateTime(createTime);
            duplicateRemovalMessage.setFromUserName(fromUserName);
        }

        if (MESSAGE_CACHE.contains(duplicateRemovalMessage)) {
            // 缓存中存在，直接pass
            return false;
        } else {
            setMessageToCache(duplicateRemovalMessage);
            return true;
        }
    }

    private static void setMessageToCache(DuplicateRemovalMessage duplicateRemovalMessage) {
        if (MESSAGE_CACHE.size() >= MESSAGE_CACHE_SIZE) {
            MESSAGE_CACHE.remove(0);
        }
        MESSAGE_CACHE.add(duplicateRemovalMessage);
    }
}













