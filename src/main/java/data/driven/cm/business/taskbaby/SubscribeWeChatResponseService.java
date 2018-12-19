package data.driven.cm.business.taskbaby;

import java.util.Map;

public interface SubscribeWeChatResponseService {
    /**
     * 微信通知，将微信发送的xml转成map传进来
     * @date  2018-12-19  14:22
     * @author Logn
     * @param wechatEventMap
     * @param appId 公众号appid
     * @return 返回微信发送的消息
     */
    String notify(Map wechatEventMap, String appId);
}
