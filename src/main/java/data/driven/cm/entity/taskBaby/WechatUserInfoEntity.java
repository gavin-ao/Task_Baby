package data.driven.cm.entity.taskBaby;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 微信用户表Entity
 * @Date: 2018/11/15 11:16
 * @Version 1.0
 */
public class WechatUserInfoEntity {

    /**
     * 微信用户表id主键
     */
    private String wechatUserId;

    /**
     * 是否订阅公众号,1 是 0 否
     */
    private Integer subscribe;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 性别,1 男 2 女 0 未知
     */
    private Integer sex;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 语言
     */
    private String language;

    /**
     * 头像URL
     */
    private String headimgurl;

    /**
     * 用户在公众号中的唯一id,只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
     */
    private String unionid;

    /**
     * 备注,公众号对粉丝的备注
     */
    private String remark;

    /**
     * 渠道来源:
     * ADD_SCENE_SEARCH 公众号搜索
     * ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移
     * ADD_SCENE_PROFILE_CARD 名片分享
     * ADD_SCENE_QR_CODE 扫描二维码
     * ADD_SCENEPROFILE LINK 图文页内名称点击
     * ADD_SCENE_PROFILE_ITEM 图文页右上角菜单
     * ADD_SCENE_PAID 支付后关注
     * ADD_SCENE_OTHERS 其他
     */
    private String subscribeScene;

    /**
     * 公众号信息表外键原始ID
     */
    private String wechatAccount;

    /**
     * 用户关注时间
     */
    private Integer subscribeTime;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 城市
     */
    private String city;

    /**
     * 二维码扫码场景id
     */
    private Integer qrScene;

    /**
     * 二维码扫码场景描述
     */
    private String qrSceneStr;

    /**
     * 用户的标识，对当前公众号唯一
     */
    private String openid;

    public String getWechatUserId() {
        return wechatUserId;
    }

    public void setWechatUserId(String wechatUserId) {
        this.wechatUserId = wechatUserId;
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSubscribeScene() {
        return subscribeScene;
    }

    public void setSubscribeScene(String subscribeScene) {
        this.subscribeScene = subscribeScene;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public Integer getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Integer subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getQrScene() {
        return qrScene;
    }

    public void setQrScene(Integer qrScene) {
        this.qrScene = qrScene;
    }

    public String getQrSceneStr() {
        return qrSceneStr;
    }

    public void setQrSceneStr(String qrSceneStr) {
        this.qrSceneStr = qrSceneStr;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
