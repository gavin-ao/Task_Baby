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

}
