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

    String getCustomerServiceConfigId(String appId, String keyword);

    List<CustomerConfigureEntity> getChildMenu(String configId);
}
