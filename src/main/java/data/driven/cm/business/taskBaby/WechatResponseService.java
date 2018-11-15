package data.driven.cm.business.taskBaby;

import java.util.Map;

/**
 * 活动接口
 *
 */
public interface WechatResponseService {

    /**
     * 微信通知，将微信发送的xml转成map传进来
     * @param wechatEventMap
     * @return 返回微信发送的消息
     */
    public String notify(Map wechatEventMap);

    /**
     * 判断活动是否激活
     * @param wechatEventMap
     * @return
     */
    public boolean checkActive(Map wechatEventMap);
}
