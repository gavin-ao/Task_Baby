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
     * 助力者id,微信用户的id
     */
    private String toId;

    /**
     * 活动id
     */
    private String actId;

    /**
     * 公众号原始ID
     */
    private String wechatAccount;

    /**
     * 助力状态,0 未助力 1 已助力
     */
    private Integer helpStatus;

    /**
     * 被助力者
     */
    private String formId;

    /**
     * 粉丝状态,0  否 1 是 注：针对于老用户是不算助力成功
     */
    private Integer fansStatus;

    /**
     * 助力日期
     */
    private Date helpAt;

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public Integer getHelpStatus() {
        return helpStatus;
    }

    public void setHelpStatus(Integer helpStatus) {
        this.helpStatus = helpStatus;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Integer getFansStatus() {
        return fansStatus;
    }

    public void setFansStatus(Integer fansStatus) {
        this.fansStatus = fansStatus;
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
}
