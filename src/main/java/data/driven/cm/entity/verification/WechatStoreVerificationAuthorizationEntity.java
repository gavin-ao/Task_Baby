package data.driven.cm.entity.verification;

import java.util.Date;

/**
 * 微信门店核销权限表
 * @author lxl
 * @date 2018/11/6
 */

public class WechatStoreVerificationAuthorizationEntity {

    /**
     * 微信门店核销权限 主键id
     */
    private String id;

    /**
     * 系统用户id
     */
    private String userId;

    /**
     * 应小程序在微信中的唯一标识
     */
    private String openId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 修改时间
     */
    private Date updateAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
