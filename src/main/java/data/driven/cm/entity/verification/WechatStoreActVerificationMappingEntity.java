package data.driven.cm.entity.verification;

import java.util.Date;

/**
 * 核销关联表Entity
 * @author lxl
 * @date 2018/11/8
 */
public class WechatStoreActVerificationMappingEntity {

    /**
     * 主键
     */
    private String verificationId;

    /**
     * 系统用户id
     */
    private String userId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 活动id
     */
    private String actId;

    /**
     * 奖励口令id
     */
    private String commandId;

    /**
     * 微信用户id
     */
    private String wechatUserId;

    /**
     * 应小程序在微信中的唯一标识
     */
    private String openId;

    /**
     * 创建时间
     */
    private Date createAt;

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getWechatUserId() {
        return wechatUserId;
    }

    public void setWechatUserId(String wechatUserId) {
        this.wechatUserId = wechatUserId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
