package data.driven.cm.entity.taskBaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 公众号信息表Entity
 * @Date: 2018/11/15 11:13
 * @Version 1.0
 */
public class WechatPublicEntity {

    /**
     * 主键
     */
    private String id;

    /**
     * 公众号名称
     */
    private String wechatPublicName;

    /**
     * 开发者ID
     */
    private String appid;

    /**
     * 开发者密码
     */
    private String secret;

    /**
     * 公众号原始ID
     */
    private String wechatAccount;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 令牌
     */
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWechatPublicName() {
        return wechatPublicName;
    }

    public void setWechatPublicName(String wechatPublicName) {
        this.wechatPublicName = wechatPublicName;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
