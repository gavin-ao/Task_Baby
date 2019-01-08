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
    public List<CustomerConfigureEntity> customerConfigureEntites(String authorizationAppid) {
        String sql = "select ccf.describe,ccf.order from sys_user_info sui, customer_configure ccf " +
                "where sui.authorization_appid = ? and sui.user_id = ccf.sys_user_id ORDER BY ccf.order ASC";

        return jdbcBaseDao.queryList(CustomerConfigureEntity.class,sql,authorizationAppid);
    }
}
