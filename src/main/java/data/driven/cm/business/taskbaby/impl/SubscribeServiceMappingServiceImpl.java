package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.SubscribeServiceMappingService;
import data.driven.cm.dao.JDBCBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: lxl
 * @describe 订阅号与服务号关系Impl
 * @Date: 2018/12/19 12:23
 * @Version 1.0
 */
@Service
public class SubscribeServiceMappingServiceImpl implements SubscribeServiceMappingService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    /**
     * 根据订阅号的原始id获取与他绑定的服务号的appid
     * @author Logan
     * @date 2018-12-19 15:55
     * @param subscribeWechatAccount

     * @return
     */
    @Override
    public String getServiceWechatAppId(String subscribeWechatAccount) {
        String sql = "select service_appid from subscribe_service_mapping where subscribe_wechat_account = ?";
        Object serviceAppid = jdbcBaseDao.getColumn(sql,subscribeWechatAccount);
        if (serviceAppid != null){
            return serviceAppid.toString();
        }
        return null;
    }
}
