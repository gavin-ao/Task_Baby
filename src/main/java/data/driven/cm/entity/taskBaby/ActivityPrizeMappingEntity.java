package data.driven.cm.entity.taskBaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 活动奖品关联表
 * @Date: 2018/11/19 16:20
 * @Version 1.0
 */
public class ActivityPrizeMappingEntity {

    /**
     * 活动奖品主键
     */
    private String prizeId;

    /**
     * 活动表Id
     */
    private String actId;

    /**
     * 口令
     */
    private String token;

    /**
     * 奖品URL
     */
    private String linkUrl;

    /**
     * 状态
     */
    private Integer tokenStatus;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建日期
     */
    private Date createAt;

    public String getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(String prizeId) {
        this.prizeId = prizeId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Integer getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(Integer tokenStatus) {
        this.tokenStatus = tokenStatus;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
