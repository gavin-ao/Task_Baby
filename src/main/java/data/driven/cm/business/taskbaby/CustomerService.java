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
    * @author Logan
    * @date 2019-01-08 16:23
    * @param wechatEventMap
    * @param appId

    * @return
    */
     String call(Map<String,String> wechatEventMap,String appId);

     void sendNameCard(Map<String,String> wechatEventMap,String option,String appId);

}
