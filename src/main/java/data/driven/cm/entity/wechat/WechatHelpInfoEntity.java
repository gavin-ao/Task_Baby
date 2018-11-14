package data.driven.cm.entity.wechat;

import java.util.Date;

/**
 * 微信助力表
 * @author hejinkai
 * @date 2018/7/13
 */
public class WechatHelpInfoEntity {
    /** 主键 **/
    private String helpId;
    /** 活动id **/
    private String actId;
    /** 助力发起者 **/
    private String wechatUserId;
    /** 门店id **/
    private String storeId;
    /** 微信小程序id **/
    private String appInfoId;
    /** 创建时间 **/
    private Date createAt;

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getWechatUserId() {
        return wechatUserId;
    }

    public void setWechatUserId(String wechatUserId) {
        this.wechatUserId = wechatUserId;
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

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
