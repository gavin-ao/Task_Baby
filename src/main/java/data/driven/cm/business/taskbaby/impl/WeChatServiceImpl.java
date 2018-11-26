package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.aes.WXBizMsgCrypt;
import data.driven.cm.business.taskbaby.WeChatService;
import data.driven.cm.business.taskbaby.WechatPublicService;
import data.driven.cm.business.taskbaby.WechatResponseService;
import data.driven.cm.business.taskbaby.WechatUserInfoService;
import data.driven.cm.component.DuplicateRemovalMessage;
import data.driven.cm.component.WeChatConstant;
import data.driven.cm.entity.taskbaby.WechatPublicEntity;
import data.driven.cm.thread.TaskBabyThread;
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
    @Autowired
    private WechatResponseService wechatResponseService;

    /**
     *  调用核心服务类接收处理请求
     * @param request
     * @param response
     * @param appid
     * @return
     * @throws UnsupportedEncodingException
     */
    @Override
    public String processRequest(HttpServletRequest request, HttpServletResponse response,String appid) throws UnsupportedEncodingException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String encryptType = request.getParameter("encrypt_type");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        String msgSignature = request.getParameter("msg_signature");
        // 随机数
        String nonce = request.getParameter("nonce");

        // xml格式的消息数据
        String respXml = null;
        Map<String, String> requestMap = null;
        try {
            if("aes".equals(encryptType)){ //解析加密信息
                requestMap=WeChatUtil.parseRequest(request);
                WechatPublicEntity wechatPublicEntity = wechatPublicService.getEntityByWechatAccount(requestMap.get(WeChatConstant.ToUserName));
                WXBizMsgCrypt pc = new WXBizMsgCrypt(WeChatConstant.TOKEN,WeChatConstant.EncodingAESKey,wechatPublicEntity.getAuthorizationAppid());
                String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
                String fromXML = String.format(format, requestMap.get("Encrypt"));
                respXml = pc.decryptMsg(msgSignature, timestamp, nonce,fromXML);
                requestMap=WeChatUtil.parseXml(respXml);
                TaskBabyThread taskBabyThread = new TaskBabyThread(wechatResponseService,requestMap,appid);
                Thread thread = new Thread(taskBabyThread);
                thread.start();
                return "";
            }else{ //处理明文
                requestMap=WeChatUtil.parseRequest(request);
                TaskBabyThread taskBabyThread = new TaskBabyThread(wechatResponseService,requestMap,appid);
                Thread thread = new Thread(taskBabyThread);
                thread.start();
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 排重，别调用后面的代码
     * @param requestMap
     * @return
     */
    public Boolean deDuplication(Map<String,String> requestMap){
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
        }else{
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













