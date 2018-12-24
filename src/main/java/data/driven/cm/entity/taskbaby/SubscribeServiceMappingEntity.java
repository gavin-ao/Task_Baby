package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 订阅号与服务号关系Entity
 * @Date: 2018/12/19 12:19
 * @Version 1.0
 */
public class SubscribeServiceMappingEntity {

    /**
     * 主键
     */
    private String id;

    /**
     * 订阅号的原始id
     */
    private String subscribeWechatAccount;

    /**
     * 服务号的原始id
     */
    private String serviceWechatAccount;

    /**
     * 服务号的appid
     */
    private String serviceAppid;

    /**
     * 创建日期
     */
    private Date createAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscribeWechatAccount() {
        return subscribeWechatAccount;
    }

    public void setSubscribeWechatAccount(String subscribeWechatAccount) {
        this.subscribeWechatAccount = subscribeWechatAccount;
    }

    public String getServiceWechatAccount() {
        return serviceWechatAccount;
    }

    public void setServiceWechatAccount(String serviceWechatAccount) {
        this.serviceWechatAccount = serviceWechatAccount;
    }

    public String getServiceAppid() {
        return serviceAppid;
    }

    public void setServiceAppid(String serviceAppid) {
        this.serviceAppid = serviceAppid;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
