package data.driven.cm.business.taskbaby;

/**
 * @Author: lxl
 * @describe 收货地址Service
 * @Date: 2018/11/30 14:07
 * @Version 1.0
 */
public interface ReceivingAddressService {

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
    void insertReceivingAddressEntity(String openId,String actId,String consignee,String phone,String province,
                                        String city,String district,String detailedAddress);
}
