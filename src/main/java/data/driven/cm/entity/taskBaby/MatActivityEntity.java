package data.driven.cm.entity.taskBaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 活动信息表
 * @Date: 2018/11/15 10:55
 * @Version 1.0
 */
public class MatActivityEntity {

    /**
     * 活动id
     */
    private String actId;

    /**
     * 公众号原始ID
     */
    private String wechatAccount;

    /**
     * 活动类型
     */
    private Integer actType;

    /**
     * 活动名称
     */
    private String actName;

    /**
     * 活动介绍
     */
    private String actIntroduce;

    /**
     * 图片id,海报id
     */
    private String pictureId;

    /**
     * 活动关键词
     */
    private String actKeyWord;

    /**
     * 活动标题
     */
    private String actTitle;

    /**
     * 活动分享方案
     */
    private String actShareCopywriting;

    /**
     * 活动规则
     */
    private String actRule;

    /**
     * 兑换规则
     */
    private String exchangeRule;

    /**
     * 参与人数
     */
    private Integer partakeNum;

    /**
     * 奖品数量
     */
    private Integer prizeNum;

    /**
     * 活动奖励地址
     */
    private String rewardUrl;

    /**
     * 活动开始日期
     */
    private Date startAt;

    /**
     * 活动结束日期
     */
    private Date endAt;

    /**
     * 活动状态
     */
    private Integer status;

    /**
     * 创建日期
     */
    private Date createAt;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public Integer getActType() {
        return actType;
    }

    public void setActType(Integer actType) {
        this.actType = actType;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActIntroduce() {
        return actIntroduce;
    }

    public void setActIntroduce(String actIntroduce) {
        this.actIntroduce = actIntroduce;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getActKeyWord() {
        return actKeyWord;
    }

    public void setActKeyWord(String actKeyWord) {
        this.actKeyWord = actKeyWord;
    }

    public String getActTitle() {
        return actTitle;
    }

    public void setActTitle(String actTitle) {
        this.actTitle = actTitle;
    }

    public String getActShareCopywriting() {
        return actShareCopywriting;
    }

    public void setActShareCopywriting(String actShareCopywriting) {
        this.actShareCopywriting = actShareCopywriting;
    }

    public String getActRule() {
        return actRule;
    }

    public void setActRule(String actRule) {
        this.actRule = actRule;
    }

    public String getExchangeRule() {
        return exchangeRule;
    }

    public void setExchangeRule(String exchangeRule) {
        this.exchangeRule = exchangeRule;
    }

    public Integer getPartakeNum() {
        return partakeNum;
    }

    public void setPartakeNum(Integer partakeNum) {
        this.partakeNum = partakeNum;
    }

    public Integer getPrizeNum() {
        return prizeNum;
    }

    public void setPrizeNum(Integer prizeNum) {
        this.prizeNum = prizeNum;
    }

    public String getRewardUrl() {
        return rewardUrl;
    }

    public void setRewardUrl(String rewardUrl) {
        this.rewardUrl = rewardUrl;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
