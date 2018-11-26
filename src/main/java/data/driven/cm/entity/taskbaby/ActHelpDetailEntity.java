package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 活动助力详细表Entity
 * @Date: 2018/11/16 18:08
 * @Version 1.0
 */
public class ActHelpDetailEntity {

    /**
     * 活动助力详细Id
     */
    private String actHelpDetailId;

    /**
     * 活动助力表外键
     */
    private String helpId;

    /**
     * 助力状态
     */
    private Integer helpStatus;

    /**
     * 粉丝状态
     */
    private Integer fansStatus;

    /**
     * 活动id
     */
    private String actId;

    /**
     * 创建日期
     */
    private Date createAt;

    /**
     * 助力者OpenId
     */
    private String helpOpenId;

    public String getActHelpDetailId() {
        return actHelpDetailId;
    }

    public void setActHelpDetailId(String actHelpDetailId) {
        this.actHelpDetailId = actHelpDetailId;
    }

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public Integer getHelpStatus() {
        return helpStatus;
    }

    public void setHelpStatus(Integer helpStatus) {
        this.helpStatus = helpStatus;
    }

    public Integer getFansStatus() {
        return fansStatus;
    }

    public void setFansStatus(Integer fansStatus) {
        this.fansStatus = fansStatus;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getHelpOpenId() {
        return helpOpenId;
    }

    public void setHelpOpenId(String helpOpenId) {
        this.helpOpenId = helpOpenId;
    }
}
