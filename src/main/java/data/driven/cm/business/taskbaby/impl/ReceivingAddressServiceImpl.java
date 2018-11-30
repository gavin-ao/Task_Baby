package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.ReceivingAddressService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.ReceivingAddressEntity;
import data.driven.cm.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 收货地址Impl
 * @Date: 2018/11/30 14:08
 * @Version 1.0
 */
@Service
public class ReceivingAddressServiceImpl implements ReceivingAddressService {
    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    /**
     * @description
     * @author lxl
     * @date 2018-11-30 14:14
     * @param openId 用户openid
     * @param actId 活动id
     * @param consignee 收货人名字
     * @param phone 电话
     * @param province 省行政区划码
     * @param city 市行政区划码
     * @param district 区行政区划码
     * @param detailedAddress 详细地址
     */
    @Override
    public void insertReceivingAddressEntity(String openId, String actId, String consignee, String phone,
                                             String province, String city, String district, String detailedAddress) {
        Date createAt = new Date();
        String id = gettReceivingAddressById(openId);
        if(id != null){
            String sql = "update receiving_address set act_id = ? ,consignee = ?, phone = ?,province = ?,city = ?,district = ?,detailed_address = ?,create_at = ? where openid = ?";
            jdbcBaseDao.executeUpdate(sql, actId,consignee,phone,province,city,district,detailedAddress,createAt,openId);
        }else{
            id = UUIDUtil.getUUID();
            ReceivingAddressEntity receivingAddressEntity = new ReceivingAddressEntity();
            receivingAddressEntity.setId(id);
            receivingAddressEntity.setOpenid(openId);
            receivingAddressEntity.setActId(actId);
            receivingAddressEntity.setConsignee(consignee);
            receivingAddressEntity.setPhone(phone);
            receivingAddressEntity.setProvince(province);
            receivingAddressEntity.setCity(city);
            receivingAddressEntity.setDistrict(district);
            receivingAddressEntity.setDetailedAddress(detailedAddress);
            receivingAddressEntity.setCreateAt(createAt);
            jdbcBaseDao.insert(receivingAddressEntity,"receiving_address");
        }
    }

    /**
     * @description 通过openId查找是否存在数据
     * @author lxl
     * @date 2018-11-30 14:26
     * @param openId 用户openid
     * @return
     */
    private String gettReceivingAddressById(String openId) {
        String sql = "select id from receiving_address where openid = ?";
        Object id = jdbcBaseDao.getColumn(sql, openId);
        if(id != null){
            return id.toString();
        }
        return null;
    }
}
