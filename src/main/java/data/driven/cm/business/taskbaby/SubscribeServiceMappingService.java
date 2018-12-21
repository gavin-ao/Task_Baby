package data.driven.cm.business.taskbaby;

/**
 * @Author: lxl
 * @describe 订阅号与服务号关系Service
 * @Date: 2018/12/19 12:22
 * @Version 1.0
 */
public interface SubscribeServiceMappingService {
    /**
    * 根据订阅号的appId获取与他绑定的服务号的appid
    * @author Logan
    * @date 2018-12-19 15:55
    * @param appId

    * @return
    */
    String getServiceWechatAppId(String appId);

    /**
     * @description 获取订阅号的关注的图片地址
     * @author lxl
     * @date 2018-12-19 17:38
     * @param subscribeWechatAccount 订阅号的原始id
     * @return
     */
    String getQrPicIdBySubscribeWechatAccount(String subscribeWechatAccount);

    /**
     * @description 获取订阅号的appid
     * @author lxl
     * @date 2018-12-21 11:05
     * @param subscribeWechatAccount 订阅号的原始id
     * @return
     */
    String getAuthorizationAppidBySubscribeWechatAccount(String subscribeWechatAccount);
}
