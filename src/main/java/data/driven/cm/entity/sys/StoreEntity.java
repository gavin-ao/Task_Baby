package data.driven.cm.entity.sys;

import java.util.Date;

/**
 * 门店实体
 * @author hejinkai
 * @date 2018/10/27
 */
public class StoreEntity {
    /** 主键 **/
    private String storeId;
    /** 系统用户id **/
    private String userId;
    /** 门店名称 **/
    private String storeName;
    /** 门店地址 **/
    private String storeAddr;
    /** 省份 **/
    private String province;
    /** 城市 **/
    private String city;
    /** 国家 **/
    private String country;
    /** 创建日期 **/
    private Date createAt;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddr() {
        return storeAddr;
    }

    public void setStoreAddr(String storeAddr) {
        this.storeAddr = storeAddr;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
