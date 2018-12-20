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

    /**
     * @description 获取订阅号的关注的图片地址
     * @author lxl
     * @date 2018-12-19 17:38
     * @param subscribeWechatAccount 订阅号的原始id
     * @return
     */
    @Override
    public String getQrPicIdBySubscribeWechatAccount(String subscribeWechatAccount) {
        String sql = "select wpd.qr_pic_id from subscribe_service_mapping ssm, wechat_public_detail wpd where " +
                "ssm.subscribe_wechat_account = wpd.user_name and ssm.subscribe_wechat_account = ? ";
        Object qrPicId = jdbcBaseDao.getColumn(sql,subscribeWechatAccount);
        if (qrPicId != null){
            return  qrPicId.toString();
        }
        return null;
    }
}
