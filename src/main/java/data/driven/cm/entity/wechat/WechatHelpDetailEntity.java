package data.driven.cm.entity.wechat;

import java.util.Date;

/**
 * 微信助力详情表
 * @author hejinkai
 * @date 2018/7/13
 */
public class WechatHelpDetailEntity {
    /** 主键 **/
    private String helpDetailId;
    /** 助力id **/
    private String helpId;
    /** 活动主键 **/
    private String actId;
    /** 门店id **/
    private String storeId;
    /** 小程序id **/
    private String appInfoId;
    /** 被助力者（微信用户与小程序关联表主键sys_map_id） **/
    private String formId;
    /** 被助力者（微信用户表外键。多个应用id都一样） **/
    private String formWechatUserId;
    /** 助力者（微信用户与小程序关联表主键sys_map_id） **/
    private String toId;
    /** 助力者（微信用户表外键。多个应用id都一样） **/
    private String toWechatUserId;
    /** 助力效果 **/
    private Integer helpEffect;
    /** 是否确认助力 0 - 拒绝助力，1 - 助力 **/
    private Integer status;
    /** 助力时间 **/
    private Date helpAt;

    public String getHelpDetailId() {
        return helpDetailId;
    }

    public void setHelpDetailId(String helpDetailId) {
        this.helpDetailId = helpDetailId;
    }

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

    public Integer getHelpEffect() {
        return helpEffect;
    }

    public void setHelpEffect(Integer helpEffect) {
        this.helpEffect = helpEffect;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getHelpAt() {
        return helpAt;
    }

    public void setHelpAt(Date helpAt) {
        this.helpAt = helpAt;
    }
}
