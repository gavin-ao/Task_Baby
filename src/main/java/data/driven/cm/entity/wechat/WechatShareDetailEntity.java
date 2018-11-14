package data.driven.cm.entity.wechat;

import java.util.Date;

/**
 * 分享详情
 * @author hejinkai
 * @date 2018/7/4
 */
public class WechatShareDetailEntity {
    /** 主键 **/
    private String shareDetailId;
    /** 分享信息主键 **/
    private String shareId;
    /** 门店id **/
    private String storeId;
    /** 小程序id **/
    private String appInfoId;
    /** 分享者（微信用户与小程序关联表主键sys_mapId） **/
    private String formId;
    /** 分享者（微信用户表外键。多个应用id都一样） **/
    private String formWechatUserId;
    /** 被分享者（微信用户与小程序关联表主键sys_mapId） **/
    private String toId;
    /** 被分享者（微信用户表外键。多个应用id都一样） **/
    private String toWechatUserId;
    /** 分享次数 **/
    private Integer frequency;
    /** 分享时间 **/
    private Date shareAt;
    /** 最后一次分享时间 **/
    private Date lastShareAt;

    public String getShareDetailId() {
        return shareDetailId;
    }

    public void setShareDetailId(String shareDetailId) {
        this.shareDetailId = shareDetailId;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getAppInfoId() {
        return appInfoId;
    }

    public void setAppInfoId(String appInfoId) {
        this.appInfoId = appInfoId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormWechatUserId() {
        return formWechatUserId;
    }

    public void setFormWechatUserId(String formWechatUserId) {
        this.formWechatUserId = formWechatUserId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToWechatUserId() {
        return toWechatUserId;
    }

    public void setToWechatUserId(String toWechatUserId) {
        this.toWechatUserId = toWechatUserId;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Date getShareAt() {
        return shareAt;
    }

    public void setShareAt(Date shareAt) {
        this.shareAt = shareAt;
    }

    public Date getLastShareAt() {
        return lastShareAt;
    }

    public void setLastShareAt(Date lastShareAt) {
        this.lastShareAt = lastShareAt;
    }
}
