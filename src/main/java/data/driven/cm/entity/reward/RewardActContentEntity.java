package data.driven.cm.entity.reward;

import java.util.Date;

/**
 * 活动奖励文案
 * @author hejinkai
 * @date 2018/8/8
 */
public class RewardActContentEntity {
    /** 主键 **/
    private String contentId;
    /** 活动id **/
    private String actId;
    /** 奖励文案标题 **/
    private String contentTitle;
    /** 奖励文案头部 **/
    private String contentHead;
    /** 奖励文案中部 **/
    private String contentMid;
    /** 奖励文案脚部 **/
    private String contentFoot;
    /** 奖励按钮文案 **/
    private String contentBtn;
    /** 奖励口令类型，1 - 发起人奖励，2 - 参与人奖励 **/
    private Integer commandType;
    /** 创建时间 **/
    private Date createAt;

    /**
     * 备注 优惠信息
     */
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentHead() {
        return contentHead;
    }

    public void setContentHead(String contentHead) {
        this.contentHead = contentHead;
    }

    public String getContentMid() {
        return contentMid;
    }

    public void setContentMid(String contentMid) {
        this.contentMid = contentMid;
    }

    public String getContentFoot() {
        return contentFoot;
    }

    public void setContentFoot(String contentFoot) {
        this.contentFoot = contentFoot;
    }

    public String getContentBtn() {
        return contentBtn;
    }

    public void setContentBtn(String contentBtn) {
        this.contentBtn = contentBtn;
    }

    public Integer getCommandType() {
        return commandType;
    }

    public void setCommandType(Integer commandType) {
        this.commandType = commandType;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
