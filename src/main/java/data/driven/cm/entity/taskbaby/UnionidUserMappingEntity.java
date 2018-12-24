package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe unionid用户关系Entity
 * @Date: 2018/12/19 12:16
 * @Version 1.0
 */
public class UnionidUserMappingEntity {

    /**
     * 主键
     */
    private String id;

    /**
     * 任务id
     */
    private String actId;

    /**
     * 被助力者unionid
     */
    private String fromUnionid;

    /**
     * 助力者unionid
     */
    private String toUnionid;

    /**
     * 创建日期
     */
    private Date createAt;

    /**
     * 订阅号原始id
     */
    private String subscribeWechatAccount;

    /**
     *处理状态
     */
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSubscribeWechatAccount() {
        return subscribeWechatAccount;
    }

    public void setSubscribeWechatAccount(String subscribeWechatAccount) {
        this.subscribeWechatAccount = subscribeWechatAccount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getFromUnionid() {
        return fromUnionid;
    }

    public void setFromUnionid(String fromUnionid) {
        this.fromUnionid = fromUnionid;
    }

    public String getToUnionid() {
        return toUnionid;
    }

    public void setToUnionid(String toUnionid) {
        this.toUnionid = toUnionid;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
