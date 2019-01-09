package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.CustomerConfigureService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.CustomerConfigureEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lxl
 * @describe 客服配置Impl
 * @Date: 2019/1/8 10:30
 * @Version 1.0
 */
@Service
public class CustomerConfigureServiceImpl implements CustomerConfigureService {
    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    /**
     * @description 获取公众号消息列表
     * @author lxl
     * @date 2019-01-08 10:34
     * @param authorizationAppid 公众号appId
     * @return List<CustomerConfigureEntity>
     */
    @Override
    public List<CustomerConfigureEntity> getCustomerConfigureEntites(String authorizationAppid) {
        String sql = "select ccf.describe,ccf.order from sys_user_info sui, customer_configure ccf " +
                "where sui.authorization_appid = ? and sui.user_id = ccf.sys_user_id and  ccf.parent_id is null " +
                "ORDER BY ccf.order ASC";

        return jdbcBaseDao.queryList(CustomerConfigureEntity.class,sql,authorizationAppid);
    }

    /**
    * 根据关键字获取客服配置项id
    * @author Logan
    * @date 2019-01-09 16:00
    * @param appId
    * @param keyword

    * @return
    */
    @Override
    public String getCustomerServiceConfigId(String appId, String keyword) {
        String sql = "SELECT config.customer_configure_id FROM customer_configure config "+
                      " JOIN sys_user_info sysUser ON sysUser.user_id = config.sys_user_id"+
                      " WHERE sysUser.authorization_appid = ? and config.key_word = ?  ";

        Object result = jdbcBaseDao.getColumn(sql,appId,keyword);
        if(result != null){
            return result.toString();
        }else{
           return null;
        }
    }

    /**
    * 根据客服配置项id获取子配置列表
    * @author Logan
    * @date 2019-01-09 16:00
    * @param configId

    * @return
    */
    @Override
    public List<CustomerConfigureEntity> getChildMenu(String configId) {
        String sql = "SELECT * FROM customer_configure WHERE parent_id=?";
        return jdbcBaseDao.queryList(CustomerConfigureEntity.class,sql,configId);
    }
}
