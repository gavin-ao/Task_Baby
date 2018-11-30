package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 收货地址Entity
 * @Date: 2018/11/30 14:01
 * @Version 1.0
 */
public class ReceivingAddressEntity {

    /**
     * 主键id
     */
    private String id;
    /**
     * 用户Openid
     */
    private String openid;

    /**
     * 活动id
     */
    private String actId;

    /**
     * 收货人名字
     */
    private String consignee;

    /**
     * 电话
     */
    private String phone;

    /**
     * 省行政区划码
     */
    private String province;

    /**
     * 市行政区划码
     */
    private String city;

    /**
     * 区县行政区划码
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailedAddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**

     * 创建日期
     */
    private Date createAt;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
