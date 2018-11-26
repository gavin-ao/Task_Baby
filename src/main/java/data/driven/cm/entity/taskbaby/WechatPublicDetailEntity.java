package data.driven.cm.entity.taskbaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 公众号详细信息表
 * @Date: 2018/11/22 17:06
 * @Version 1.0
 */
public class WechatPublicDetailEntity {
    /**
     * 主键
     */
    private String id;

    /**
     * 公众号信息表外键
     */
    private String wechatPublicId;

    /**
     * 授权方昵称
     */
    private String nickName;

    /**
     * 授权方头像
     */
    private String headImg;

    /**
     * 授权方公众号类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     */
    private String serviceTypeInfo;

    /**
     * 授权方认证类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     */
    private String verifyTypeInfo;

    /**
     * 授权方公众号的原始ID
     */
    private String userName;

    /**
     * 公众号的主体名称
     */
    private String principalName;

    /**
     * 授权方公众号所设置的微信号
     */
    private String alias;

    /**
     * 以了解以下功能的开通状况（0代表未开通，1代表已开通）：
     * open_store:是否开通微信门店功能
     * open_scan:是否开通微信扫商品功能
     * open_pay:是否开通微信支付功能
     * open_card:是否开通微信卡券功能
     * open_shake:是否开通微信摇一摇功能
     * 微信公众号发过来的是json格式的字符串，现直接存储到字段里
     */
    private String businessInfo;

    /**
     * 二维码图片的URL
     */
    private String qrcodeUrl;

    /**
     * 授权方appid
     */
    private String authorizationAppid;

    /**
     * 公众号授权给开发者的权限集列表
     */
    private String funcInfo;

    /**
     *创建时间
     */
    private Date createAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWechatPublicId() {
        return wechatPublicId;
    }

    public void setWechatPublicId(String wechatPublicId) {
        this.wechatPublicId = wechatPublicId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getServiceTypeInfo() {
        return serviceTypeInfo;
    }

    public void setServiceTypeInfo(String serviceTypeInfo) {
        this.serviceTypeInfo = serviceTypeInfo;
    }

    public String getVerifyTypeInfo() {
        return verifyTypeInfo;
    }

    public void setVerifyTypeInfo(String verifyTypeInfo) {
        this.verifyTypeInfo = verifyTypeInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(String businessInfo) {
        this.businessInfo = businessInfo;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getAuthorizationAppid() {
        return authorizationAppid;
    }

    public void setAuthorizationAppid(String authorizationAppid) {
        this.authorizationAppid = authorizationAppid;
    }

    public String getFuncInfo() {
        return funcInfo;
    }

    public void setFuncInfo(String funcInfo) {
        this.funcInfo = funcInfo;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
