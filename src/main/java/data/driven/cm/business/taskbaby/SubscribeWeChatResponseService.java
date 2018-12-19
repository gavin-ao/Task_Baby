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

    /**
     * @description 获取用户unionid
     * @author lxl
     * @date 2018-12-19 15:43
     * @param code code作为换取access_token的票据
     * @param appid 服务号的appid
     * @return
     */
    String getCodeByUnionid(String code,String appid);
}
