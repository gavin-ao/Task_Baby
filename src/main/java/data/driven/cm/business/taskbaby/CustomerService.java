package data.driven.cm.business.taskbaby;

import java.util.Map;

/**
 * @program: Task_Baby
 * @description: 客户服务
 * @author: Logan
 * @create: 2019-01-08 16:17
 **/
public interface CustomerService {
    /**
    *  响应客户服务
     * 如果当前消息是客户服务特有的消息，处理完后返回true，否则返回false
    * @author Logan
    * @date 2019-01-08 16:23
    * @param wechatEventMap
    * @param appId

    * @return
    */
     boolean call(Map<String,String> wechatEventMap,String appId);

     void sendNameCard(Map<String,String> wechatEventMap,String option,String appId);

    /**
     * 用户搜索关注后如果存在自定义回复的信息则发送
     *
     * @param appid          公众号appid
     * @param wechatEventMap 发送过来的Map信息
     * @param accessToken    公众号accessToken
     * @return 返回的信息
     * @author lxl
     */
    String sendFollowMsg(String appid, Map<String, String> wechatEventMap, String accessToken);

    /**
     * @description 被动回复，如果存在发送客服信息，不存在不发送
     * @author lxl
     * @date 2019-01-09 15:29
     * @param fromUserName 微信用户OpenId
     * @param appid 公众号appid
     * @param accessToken 公众号的token
     * @return
     */
    String sendCustomServiceMsg(String appid, String fromUserName, String accessToken);

}