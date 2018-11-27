package data.driven.cm.business.taskbaby;

import java.util.Map;

/**
 * 活动接口
 * @author lxl
 */
public interface WechatResponseService {

    /**
     * 微信通知，将微信发送的xml转成map传进来
     * @author lxl
     * @param wechatEventMap
     * @param appId 公众号appid
     * @return 返回微信发送的消息
     */
    String notify(Map wechatEventMap,String appId);


}
