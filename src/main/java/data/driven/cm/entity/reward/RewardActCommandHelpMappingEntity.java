package data.driven.cm.entity.reward;

import java.util.Date;

/**
 * 活动助力奖励关联表Entity
 * @author lxl
 * @date 2018/11/7
 */

public class RewardActCommandHelpMappingEntity {

    /**
     * 活动助力奖励关联表 唯一id
     */
    private String mapId;

    /**
     * 助力id
     */
    private String helpId;

    /**
     * 奖励口令id
     */
    private String commandId;

    /**
     *活动id
     */
    private String actId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 小程序id
     */
    private String appInfoId;

    /**
     * 微信用户id
     */
    private String wechatUserId;

    /**
     * 创建时间
     */
    private Date createAt;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
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

    public String getWechatUserId() {
        return wechatUserId;
    }

    public void setWechatUserId(String wechatUserId) {
        this.wechatUserId = wechatUserId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
