package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.CustomerConfigureEntity;

import java.util.List;

/**
 * @Author: lxl
 * @describe 客服配置Service
 * @Date: 2019/1/8 10:30
 * @Version 1.0
 */
public interface CustomerConfigureService {

    /**
     * @description 获取公众号消息列表
     * @author lxl
     * @date 2019-01-08 10:34
     * @param authorizationAppid 公众号appId
     * @return List<CustomerConfigureEntity>
     */
    List<CustomerConfigureEntity> getCustomerConfigureEntites (String authorizationAppid);

    /**
     * 根据关键字获取客服配置项id
     * @author Logan
     * @date 2019-01-09 16:00
     * @param appId
     * @param keyword

     * @return
     */
    String getCustomerServiceConfigId(String appId, String keyword);

    /**
     * 根据客服配置项id获取子配置列表
     * @author Logan
     * @date 2019-01-09 16:00
     * @param configId

     * @return
     */
    List<CustomerConfigureEntity> getChildMenu(String configId);
}
