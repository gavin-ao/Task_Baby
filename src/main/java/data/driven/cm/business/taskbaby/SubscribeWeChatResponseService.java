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

    /**
     * @description 订阅号用户扫自己的二维码发送我的活动进度
     * @author lxl
     * @date 2018-12-21 10:51
     * @param appId 订阅号的appId
     * @param activityId 活动id
     * @param openIdWho 用户在订阅号中的openid
     * @return
     */
    void subscribeSendMyActivityStatus(String appId,String activityId,String openIdWho);
}
