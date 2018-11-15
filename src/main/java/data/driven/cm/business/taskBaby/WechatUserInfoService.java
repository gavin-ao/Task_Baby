package data.driven.cm.business.taskBaby;


import data.driven.cm.entity.taskBaby.WechatUserInfoEntity;

public interface WechatUserInfoService {

    /**
     * 得到微信用户的 id
     * @param wechatAccount 公众号原始ID
     * @param openId 微信用户在公众号中唯一标示
     * @return wechatUserId 微信用户id
     */
    public String getUserInfoById(String wechatAccount, String openId);

    /**
     *  得到微信用户实体类
     * @param wechatAccount 公众号原始ID
     * @param openId 微信用户在公众号中唯一标示
     * @return WechatUserInfoEntity 实体类
     */
    public WechatUserInfoEntity getWechatUserInfoEntityByAcountOpenId(String wechatAccount, String openId);

    /**
     * 新增微信用户信息
     * @param subscribe 是否订阅公众号,1 是 0 否
     * @param openid 微信用户在公众号中唯一的标示
     * @param nickname 用户昵称
     * @param sex 性别,1 男 2 女 0 未知
     * @param country 国家
     * @param province 省份
     * @param language 语言
     * @param headimgurl 头像URL
     * @param unionid 用户在公众号中的唯一id,只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
     * @param remark 备注,公众号对粉丝的备注
     * @param subscribeScene 渠道来源
     * @param wechatAccount 公众号信息表外键原始ID
     * @param subscribeTime 用户关注时间
     * @param city 城市
     * @param qrScene 二维码扫码场景id
     * @param qrSceneStr 二维码扫码场景描述
     * @param openId 微信用户在公众号中唯一的标示
     * @return
     */
    public String insertWechatUserInfoEntity(Integer subscribe,String openId ,String nickname,Integer sex,String country,String province,String language,String headimgurl,
                                             String unionid,String remark,String subscribeScene,String wechatAccount,Integer subscribeTime,String city,
                                             Integer qrScene,String qrSceneStr);

    /**
     *  微信用户取消关注后需要修改用户 是否订阅公众号状态
     * @param wechatAccount 公众号信息表外键原始ID
     * @param openId 微信用户在公众号中唯一的标示
     * @param subscribe 是否订阅公众号,1 是 0 否
     * @return
     */
    public String updateSubscribe(String wechatAccount, String openId,Integer subscribe);

}
