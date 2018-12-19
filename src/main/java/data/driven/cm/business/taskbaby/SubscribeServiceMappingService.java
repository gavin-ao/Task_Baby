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
}
