package data.driven.cm.entity.taskBaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 活动助力表Entity
 * @Date: 2018/11/15 10:47
 * @Version 1.0
 */
public class ActHelpEntity {

    /**
     * 活动助力id 主键
     */
    private String helpId;

    /**
     * 活动id
     */
    private String actId;

    /**
     * 公众号原始ID
     */
    private String wechatAccount;

    /**
     * 被助力者
     */
    private String formId;

    /**
     * 助力日期
     */
    private Date helpAt;

    /**
     * 助力成功状态,0 助力未成功 1 助力成功
     */
    private Integer helpSuccessStatus;

    /**
     * 助力人数
     */
    private Integer helpNumber;

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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Date getHelpAt() {
        return helpAt;
    }

    public void setHelpAt(Date helpAt) {
        this.helpAt = helpAt;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public Integer getHelpSuccessStatus() {
        return helpSuccessStatus;
    }

    public void setHelpSuccessStatus(Integer helpSuccessStatus) {
        this.helpSuccessStatus = helpSuccessStatus;
    }

    public Integer getHelpNumber() {
        return helpNumber;
    }

    public void setHelpNumber(Integer helpNumber) {
        this.helpNumber = helpNumber;
    }
}
