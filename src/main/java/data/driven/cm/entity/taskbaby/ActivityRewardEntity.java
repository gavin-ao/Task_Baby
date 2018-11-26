package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 活动奖励表Entity
 * @Date: 2018/11/15 10:52
 * @Version 1.0
 */
public class ActivityRewardEntity {

    /**
     * 奖励id
     */
    private String rewardId;

    /**
     * 活动id
     */
    private String actId;

    /**
     * 微信用户id
     */
    private String wechatUserId;

    /**
     * 公众号原始ID
     */
    private String wechatAccount;

    /**
     * 领取状态,1 已领取 0 未领取
     */
    private Integer receiveStatus;

    /**
     * 创建日期
     */
    private Date createAt;

    /**
     * 活动奖品关联表
     */
    private String prize_id;

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
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

    public Integer getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(Integer receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public String getPrize_id() {
        return prize_id;
    }

    public void setPrize_id(String prize_id) {
        this.prize_id = prize_id;
    }
}
