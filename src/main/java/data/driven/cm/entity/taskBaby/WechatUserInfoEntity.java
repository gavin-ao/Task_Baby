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
    private String wechatUerId;

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
}
