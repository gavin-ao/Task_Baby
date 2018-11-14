package data.driven.cm.entity.material;

import java.util.Date;

/**
 * 素材-活动
 * @author hejinkai
 * @date 2018/7/15
 */
public class MatActivityEntity {
    /** 活动id **/
    private String actId;
    /** 活动用户id **/
    private String userId;
    /** 门店id **/
    private String storeId;
    /** 微信小程序id **/
    private String appInfoId;
    /** 活动名称 **/
    private String actName;
    /** 活动介绍 **/
    private String actIntroduce;
    /** 图片id **/
    private String pictureId;
    /** 活动地址 **/
    private String actUrl;
    /** 活动回复文字 **/
    private String actReply;
    /** 活动标题 **/
    private String actTitle;
    /** 活动分享标题 **/
    private String actShareTitle;
    /** 活动分享文案 **/
    private String actShareCopywriting;
    /** 活动规则 **/
    private String actRule;
    /** 兑换规则 **/
    private String exchangeRule;
    /** 参与人数 **/
    private Integer partakeNum;
    /** 活动奖励url **/
    private String rewardUrl;
    /** 活动开始日期 **/
    private Date startAt;
    /** 活动结束日期 **/
    private Date endAt;
    /** 活动状态 0 - 未开始； 1 进行中 ； 2 - 已结束 **/
    private Integer status;
    /** 创建日期 **/
    private Date createAt;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
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

    public String getAppInfoId() {
        return appInfoId;
    }

    public void setAppInfoId(String appInfoId) {
        this.appInfoId = appInfoId;
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

    public String getActUrl() {
        return actUrl;
    }

    public void setActUrl(String actUrl) {
        this.actUrl = actUrl;
    }

    public String getActReply() {
        return actReply;
    }

    public void setActReply(String actReply) {
        this.actReply = actReply;
    }

    public String getActTitle() {
        return actTitle;
    }

    public void setActTitle(String actTitle) {
        this.actTitle = actTitle;
    }

    public String getActShareTitle() {
        return actShareTitle;
    }

    public void setActShareTitle(String actShareTitle) {
        this.actShareTitle = actShareTitle;
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
